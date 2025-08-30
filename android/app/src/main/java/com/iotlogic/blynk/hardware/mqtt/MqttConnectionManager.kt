package com.iotlogic.blynk.hardware.mqtt

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages MQTT connections and messaging for real-time IoT communication
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class MqttConnectionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // MQTT clients management
    private val mqttClients = ConcurrentHashMap<String, MqttAsyncClient>()
    private val subscriptions = ConcurrentHashMap<String, MutableSet<String>>()
    
    // State flows
    private val _connectionStates = MutableStateFlow<Map<String, MqttConnectionState>>(emptyMap())
    val connectionStates: StateFlow<Map<String, MqttConnectionState>> = _connectionStates.asStateFlow()
    
    private val _receivedMessages = MutableSharedFlow<MqttReceivedMessage>()
    val receivedMessages: SharedFlow<MqttReceivedMessage> = _receivedMessages.asSharedFlow()
    
    // Note: publishResults is used in deliveryComplete callback
    private val _publishResults = MutableSharedFlow<MqttPublishResult>()
    val publishResults: SharedFlow<MqttPublishResult> = _publishResults.asSharedFlow()
    
    /**
     * Initialize MQTT manager
     */
    suspend fun initialize(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // MQTT doesn't require specific initialization
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Connect to MQTT broker
     */
    suspend fun connect(
        brokerId: String,
        brokerUrl: String,
        clientId: String? = null,
        username: String? = null,
        password: String? = null,
        cleanSession: Boolean = true,
        keepAliveInterval: Int = 60,
        connectionTimeout: Int = 30,
        enableSsl: Boolean = false
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val actualClientId = clientId ?: "IoTLogic_${System.currentTimeMillis()}"
                val persistence = MemoryPersistence()
                
                val client = MqttAsyncClient(brokerUrl, actualClientId, persistence)
                
                val connOpts = MqttConnectOptions().apply {
                    isCleanSession = cleanSession
                    this.keepAliveInterval = keepAliveInterval
                    this.connectionTimeout = connectionTimeout
                    
                    if (username != null && password != null) {
                        userName = username
                        setPassword(password.toCharArray())
                    }
                    
                    if (enableSsl) {
                        socketFactory = javax.net.ssl.SSLSocketFactory.getDefault()
                    }
                }
                
                // Set callback for the client
                client.setCallback(object : MqttCallback {
                    override fun connectionLost(cause: Throwable?) {
                        scope.launch {
                            updateConnectionState(brokerId, MqttConnectionState.DISCONNECTED)
                        }
                    }
                    
                    override fun messageArrived(topic: String, message: MqttMessage) {
                        scope.launch {
                            val receivedMessage = MqttReceivedMessage(
                                brokerId = brokerId,
                                topic = topic,
                                payload = message.payload,
                                qos = message.qos,
                                retained = message.isRetained,
                                timestamp = System.currentTimeMillis()
                            )
                            _receivedMessages.emit(receivedMessage)
                        }
                    }
                    
                    override fun deliveryComplete(token: IMqttDeliveryToken) {
                        scope.launch {
                            val publishResult = MqttPublishResult(
                                brokerId = brokerId,
                                messageId = token.messageId,
                                isSuccess = true,
                                timestamp = System.currentTimeMillis()
                            )
                            _publishResults.emit(publishResult)
                        }
                    }
                })
                
                updateConnectionState(brokerId, MqttConnectionState.CONNECTING)
                
                // Connect with callback
                val connectResult = suspendCancellableCoroutine<Boolean>(
                    onCancellation = {
                        // Handle cancellation if needed
                    }
                ) { continuation ->
                    client.connect(connOpts, null, object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken) {
                            continuation.resume(true)
                        }

                        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                            continuation.resume(false)
                        }
                    })
                }
                
                if (connectResult) {
                    mqttClients[brokerId] = client
                    subscriptions[brokerId] = mutableSetOf()
                    updateConnectionState(brokerId, MqttConnectionState.CONNECTED)
                    Result.success(Unit)
                } else {
                    updateConnectionState(brokerId, MqttConnectionState.FAILED)
                    Result.failure(Exception("Failed to connect to MQTT broker"))
                }
            } catch (e: Exception) {
                updateConnectionState(brokerId, MqttConnectionState.FAILED)
                Result.failure(e)
            }
        }
    }
    
    /**
     * Disconnect from MQTT broker
     */
    suspend fun disconnect(brokerId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val client = mqttClients[brokerId]
                if (client != null && client.isConnected) {
                    suspendCancellableCoroutine<Boolean>(
                        onCancellation = {
                            // Handle cancellation if needed
                        }
                    ) { continuation ->
                        client.disconnect(null, object : IMqttActionListener {
                            override fun onSuccess(asyncActionToken: IMqttToken) {
                                continuation.resume(true)
                            }

                            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                                continuation.resume(false)
                            }
                        })
                    }
                    
                    mqttClients.remove(brokerId)
                    subscriptions.remove(brokerId)
                    updateConnectionState(brokerId, MqttConnectionState.DISCONNECTED)
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Subscribe to MQTT topics
     */
    suspend fun subscribe(
        brokerId: String,
        topics: List<String>,
        qos: IntArray = intArrayOf()
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val client = mqttClients[brokerId]
                    ?: return@withContext Result.failure(Exception("Not connected to broker"))
                
                val actualQos = if (qos.isEmpty()) {
                    IntArray(topics.size) { 1 } // Default QoS 1
                } else {
                    qos
                }
                
                val subscribeResult = suspendCancellableCoroutine<Boolean>(
                    onCancellation = {
                        // Handle cancellation if needed
                    }
                ) { continuation ->
                    client.subscribe(topics.toTypedArray(), actualQos, null, object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken) {
                            continuation.resume(true)
                        }

                        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                            continuation.resume(false)
                        }
                    })
                }
                
                if (subscribeResult) {
                    subscriptions[brokerId]?.addAll(topics)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to subscribe to topics"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Unsubscribe from MQTT topics
     */
    suspend fun unsubscribe(brokerId: String, topics: List<String>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val client = mqttClients[brokerId]
                    ?: return@withContext Result.failure(Exception("Not connected to broker"))
                
                val unsubscribeResult = suspendCancellableCoroutine<Boolean>(
                    onCancellation = {
                        // Handle cancellation if needed
                    }
                ) { continuation ->
                    client.unsubscribe(topics.toTypedArray(), null, object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken) {
                            continuation.resume(true)
                        }

                        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                            continuation.resume(false)
                        }
                    })
                }
                
                if (unsubscribeResult) {
                    subscriptions[brokerId]?.removeAll(topics.toSet())
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to unsubscribe from topics"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Publish message to MQTT topic
     */
    suspend fun publish(
        brokerId: String,
        topic: String,
        payload: ByteArray,
        qos: Int = 1,
        retained: Boolean = false
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val client = mqttClients[brokerId]
                    ?: return@withContext Result.failure(Exception("Not connected to broker"))
                
                val message = MqttMessage(payload).apply {
                    this.qos = qos
                    isRetained = retained
                }
                
                val publishResult = suspendCancellableCoroutine<Boolean>(
                    onCancellation = {
                        // Handle cancellation if needed
                    }
                ) { continuation ->
                    client.publish(topic, message, null, object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken) {
                            continuation.resume(true)
                        }

                        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                            continuation.resume(false)
                        }
                    })
                }
                
                if (publishResult) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to publish message"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Publish string message to MQTT topic
     */
    suspend fun publishString(
        brokerId: String,
        topic: String,
        message: String,
        qos: Int = 1,
        retained: Boolean = false
    ): Result<Unit> {
        return publish(brokerId, topic, message.toByteArray(), qos, retained)
    }
    
    /**
     * Send device telemetry data
     */
    suspend fun sendTelemetry(
        brokerId: String,
        deviceToken: String,
        sensorType: String,
        value: Any,
        unit: String? = null
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val topic = "devices/$deviceToken/telemetry/$sensorType"
                val payload = if (unit != null) {
                    """{"value":$value,"unit":"$unit","timestamp":${System.currentTimeMillis()}}"""
                } else {
                    """{"value":$value,"timestamp":${System.currentTimeMillis()}}"""
                }
                
                publishString(brokerId, topic, payload)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Subscribe to device commands
     */
    suspend fun subscribeToDeviceCommands(brokerId: String, deviceToken: String): Result<Unit> {
        val commandTopic = "devices/$deviceToken/commands/+"
        return subscribe(brokerId, listOf(commandTopic))
    }
    
    /**
     * Send device command
     */
    suspend fun sendDeviceCommand(
        brokerId: String,
        deviceToken: String,
        command: String,
        parameters: Map<String, Any> = emptyMap()
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val topic = "devices/$deviceToken/commands/$command"
                val payload = if (parameters.isNotEmpty()) {
                    val paramsJson = parameters.entries.joinToString(",") { 
                        "\"${it.key}\":\"${it.value}\"" 
                    }
                    """{"command":"$command","parameters":{$paramsJson},"timestamp":${System.currentTimeMillis()}}"""
                } else {
                    """{"command":"$command","timestamp":${System.currentTimeMillis()}}"""
                }
                
                publishString(brokerId, topic, payload)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Get connection status
     */
    fun isConnected(brokerId: String): Boolean {
        return mqttClients[brokerId]?.isConnected ?: false
    }
    
    /**
     * Get subscribed topics for a broker
     */
    fun getSubscribedTopics(brokerId: String): Set<String> {
        return subscriptions[brokerId]?.toSet() ?: emptySet()
    }
    
    /**
     * Get all connected broker IDs
     */
    fun getConnectedBrokers(): List<String> {
        return mqttClients.keys.toList()
    }
    
    /**
     * Shutdown MQTT manager
     */
    suspend fun shutdown() {
        // Disconnect all clients
        val brokerIds = mqttClients.keys.toList()
        brokerIds.forEach { brokerId ->
            disconnect(brokerId)
        }
        
        scope.cancel()
    }
    
    private fun updateConnectionState(brokerId: String, state: MqttConnectionState) {
        val currentStates = _connectionStates.value.toMutableMap()
        currentStates[brokerId] = state
        _connectionStates.value = currentStates
    }
}

/**
 * Represents a received MQTT message
 */
data class MqttReceivedMessage(
    val brokerId: String,
    val topic: String,
    val payload: ByteArray,
    val qos: Int,
    val retained: Boolean,
    val timestamp: Long
) {
    val payloadString: String
        get() = String(payload)
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MqttReceivedMessage
        return brokerId == other.brokerId && topic == other.topic && payload.contentEquals(other.payload)
    }
    
    override fun hashCode(): Int {
        var result = brokerId.hashCode()
        result = 31 * result + topic.hashCode()
        result = 31 * result + payload.contentHashCode()
        return result
    }
}

/**
 * Represents MQTT publish result
 */
data class MqttPublishResult(
    val brokerId: String,
    val messageId: Int,
    val isSuccess: Boolean,
    val timestamp: Long
)

/**
 * MQTT connection states
 */
enum class MqttConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    FAILED
}