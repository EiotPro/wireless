package com.iotlogic.blynk.services

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.iotlogic.blynk.R
import com.iotlogic.blynk.domain.model.Telemetry
import com.iotlogic.blynk.domain.repository.DeviceRepository
import com.iotlogic.blynk.domain.repository.TelemetryRepository
import com.iotlogic.blynk.hardware.mqtt.MqttConnectionManager
import com.iotlogic.blynk.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MqttBackgroundService : Service() {
    
    @Inject
    lateinit var mqttConnectionManager: MqttConnectionManager
    
    @Inject
    lateinit var deviceRepository: DeviceRepository
    
    @Inject
    lateinit var telemetryRepository: TelemetryRepository
    
    private val binder = MqttServiceBinder()
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Service state
    private var isRunning = false
    private var notificationId = 1002
    private var connectedBrokers = mutableSetOf<String>()
    
    companion object {
        const val ACTION_START_SERVICE = "START_MQTT_SERVICE"
        const val ACTION_STOP_SERVICE = "STOP_MQTT_SERVICE"
        const val ACTION_CONNECT_BROKER = "CONNECT_BROKER"
        const val ACTION_DISCONNECT_BROKER = "DISCONNECT_BROKER"
        const val ACTION_SUBSCRIBE_TOPICS = "SUBSCRIBE_TOPICS"
        const val ACTION_PUBLISH_MESSAGE = "PUBLISH_MESSAGE"
        
        const val EXTRA_BROKER_ID = "broker_id"
        const val EXTRA_BROKER_URL = "broker_url"
        const val EXTRA_CLIENT_ID = "client_id"
        const val EXTRA_USERNAME = "username"
        const val EXTRA_PASSWORD = "password"
        const val EXTRA_TOPICS = "topics"
        const val EXTRA_TOPIC = "topic"
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_QOS = "qos"
        const val EXTRA_RETAINED = "retained"
        
        const val NOTIFICATION_CHANNEL_ID = "mqtt_service_channel"
        const val NOTIFICATION_CHANNEL_NAME = "MQTT Service"
        
        // Default MQTT configuration
        const val DEFAULT_BROKER_URL = "tcp://broker.hivemq.com:1883"
        const val DEFAULT_CLIENT_ID_PREFIX = "IoTLogic_"
    }
    
    inner class MqttServiceBinder : Binder() {
        fun getService(): MqttBackgroundService = this@MqttBackgroundService
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initializeMqtt()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SERVICE -> startService()
            ACTION_STOP_SERVICE -> stopService()
            ACTION_CONNECT_BROKER -> {
                val brokerId = intent.getStringExtra(EXTRA_BROKER_ID) ?: "default"
                val brokerUrl = intent.getStringExtra(EXTRA_BROKER_URL) ?: DEFAULT_BROKER_URL
                val clientId = intent.getStringExtra(EXTRA_CLIENT_ID)
                val username = intent.getStringExtra(EXTRA_USERNAME)
                val password = intent.getStringExtra(EXTRA_PASSWORD)
                connectToBroker(brokerId, brokerUrl, clientId, username, password)
            }
            ACTION_DISCONNECT_BROKER -> {
                val brokerId = intent.getStringExtra(EXTRA_BROKER_ID) ?: "default"
                disconnectFromBroker(brokerId)
            }
            ACTION_SUBSCRIBE_TOPICS -> {
                val brokerId = intent.getStringExtra(EXTRA_BROKER_ID) ?: "default"
                val topics = intent.getStringArrayExtra(EXTRA_TOPICS)?.toList() ?: emptyList()
                subscribeToTopics(brokerId, topics)
            }
            ACTION_PUBLISH_MESSAGE -> {
                val brokerId = intent.getStringExtra(EXTRA_BROKER_ID) ?: "default"
                val topic = intent.getStringExtra(EXTRA_TOPIC) ?: ""
                val message = intent.getStringExtra(EXTRA_MESSAGE) ?: ""
                val qos = intent.getIntExtra(EXTRA_QOS, 1)
                val retained = intent.getBooleanExtra(EXTRA_RETAINED, false)
                publishMessage(brokerId, topic, message, qos, retained)
            }
        }
        
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder = binder
    
    override fun onDestroy() {
        super.onDestroy()
        stopService()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "MQTT service for real-time IoT communication"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun startService() {
        if (isRunning) return
        
        isRunning = true
        
        val notification = createServiceNotification()
        startForeground(notificationId, notification)
        
        // Start monitoring MQTT connections
        serviceScope.launch {
            monitorMqttConnections()
        }
        
        // Start processing MQTT messages
        serviceScope.launch {
            processMqttMessages()
        }
        
        // Auto-connect to default broker
        connectToBroker("default", DEFAULT_BROKER_URL)
    }
    
    private fun stopService() {
        isRunning = false
        
        serviceScope.launch {
            // Disconnect from all brokers
            connectedBrokers.toList().forEach { brokerId ->
                mqttConnectionManager.disconnect(brokerId)
            }
            connectedBrokers.clear()
            
            serviceScope.cancel()
            stopForeground(true)
            stopSelf()
        }
    }
    
    private fun initializeMqtt() {
        serviceScope.launch {
            val result = mqttConnectionManager.initialize()
            if (result.isFailure) {
                updateNotification("MQTT initialization failed")
            }
        }
    }
    
    private fun connectToBroker(
        brokerId: String,
        brokerUrl: String,
        clientId: String? = null,
        username: String? = null,
        password: String? = null
    ) {
        serviceScope.launch {
            val actualClientId = clientId ?: "${DEFAULT_CLIENT_ID_PREFIX}${System.currentTimeMillis()}"
            
            val result = mqttConnectionManager.connect(
                brokerId = brokerId,
                brokerUrl = brokerUrl,
                clientId = actualClientId,
                username = username,
                password = password,
                cleanSession = true,
                keepAliveInterval = 60,
                connectionTimeout = 30
            )
            
            if (result.isSuccess) {
                connectedBrokers.add(brokerId)
                
                // Subscribe to device topics
                subscribeToDeviceTopics(brokerId)
                
                updateNotification("Connected to ${connectedBrokers.size} MQTT broker(s)")
            } else {
                updateNotification("Failed to connect to MQTT broker: $brokerId")
            }
        }
    }
    
    private fun disconnectFromBroker(brokerId: String) {
        serviceScope.launch {
            mqttConnectionManager.disconnect(brokerId)
            connectedBrokers.remove(brokerId)
            updateNotification("Connected to ${connectedBrokers.size} MQTT broker(s)")
        }
    }
    
    private suspend fun subscribeToDeviceTopics(brokerId: String) {
        // Subscribe to general device topics
        val deviceTopics = listOf(
            "devices/+/telemetry/+",
            "devices/+/status",
            "devices/+/commands/+",
            "system/alerts/+",
            "system/status"
        )
        
        mqttConnectionManager.subscribe(brokerId, deviceTopics)
    }
    
    private fun subscribeToTopics(brokerId: String, topics: List<String>) {
        serviceScope.launch {
            if (topics.isNotEmpty()) {
                mqttConnectionManager.subscribe(brokerId, topics)
            }
        }
    }
    
    private fun publishMessage(
        brokerId: String,
        topic: String,
        message: String,
        qos: Int = 1,
        retained: Boolean = false
    ) {
        serviceScope.launch {
            mqttConnectionManager.publishString(brokerId, topic, message, qos, retained)
        }
    }
    
    private suspend fun monitorMqttConnections() {
        mqttConnectionManager.connectionStates.collect { connectionStates ->
            val activeConnections = connectionStates.filter { (_, state) ->
                state.name == "CONNECTED"
            }.keys
            
            connectedBrokers.retainAll(activeConnections)
            updateNotification("Connected to ${connectedBrokers.size} MQTT broker(s)")
        }
    }
    
    private suspend fun processMqttMessages() {
        mqttConnectionManager.receivedMessages.collect { message ->
            try {
                when {
                    message.topic.startsWith("devices/") && message.topic.contains("/telemetry/") -> {
                        handleTelemetryMessage(message.topic, message.payloadString)
                    }
                    message.topic.startsWith("devices/") && message.topic.endsWith("/status") -> {
                        handleDeviceStatusMessage(message.topic, message.payloadString)
                    }
                    message.topic.startsWith("system/alerts/") -> {
                        handleSystemAlert(message.topic, message.payloadString)
                    }
                    message.topic.startsWith("system/status") -> {
                        handleSystemStatus(message.payloadString)
                    }
                }
            } catch (e: Exception) {
                // Log error but continue processing
            }
        }
    }
    
    private suspend fun handleTelemetryMessage(topic: String, payload: String) {
        try {
            // Parse topic: devices/{token}/telemetry/{sensorType}
            val parts = topic.split("/")
            if (parts.size >= 4) {
                val deviceToken = parts[1]
                val sensorType = parts[3]
                
                // Parse payload (simplified JSON parsing)
                val telemetryData = parseTelemetryPayload(payload)
                
                if (telemetryData != null) {
                    val telemetry = Telemetry(
                        id = UUID.randomUUID().toString(),
                        deviceId = deviceToken, // Would need to map token to device ID
                        sensorType = sensorType,
                        value = telemetryData.value,
                        unit = telemetryData.unit,
                        timestamp = telemetryData.timestamp ?: System.currentTimeMillis(),
                        rawValue = payload
                    )
                    
                    telemetryRepository.storeTelemetryData(telemetry)
                    
                    // Update device last seen
                    deviceRepository.updateDeviceOnlineStatus(deviceToken, true)
                }
            }
        } catch (e: Exception) {
            // Log parsing error
        }
    }
    
    private suspend fun handleDeviceStatusMessage(topic: String, payload: String) {
        try {
            // Parse topic: devices/{token}/status
            val parts = topic.split("/")
            if (parts.size >= 3) {
                val deviceToken = parts[1]
                
                // Parse status payload
                val status = parseStatusPayload(payload)
                
                deviceRepository.updateDeviceStatus(deviceToken, status.status)
                deviceRepository.updateDeviceOnlineStatus(deviceToken, status.isOnline)
                
                status.batteryLevel?.let { battery ->
                    deviceRepository.updateSignalStrength(deviceToken, 0, "")
                }
            }
        } catch (e: Exception) {
            // Log parsing error
        }
    }
    
    private suspend fun handleSystemAlert(topic: String, payload: String) {
        // Handle system-wide alerts
        // Could trigger notifications or system actions
    }
    
    private suspend fun handleSystemStatus(payload: String) {
        // Handle system status updates
    }
    
    private fun parseTelemetryPayload(payload: String): TelemetryData? {
        return try {
            // Simplified JSON parsing - would use Gson in real implementation
            when {
                payload.startsWith("{") -> {
                    // JSON format: {"value": 25.6, "unit": "°C", "timestamp": 1234567890}
                    val value = extractJsonValue(payload, "value")?.toDoubleOrNull()
                    val unit = extractJsonString(payload, "unit")
                    val timestamp = extractJsonValue(payload, "timestamp")?.toLongOrNull()
                    
                    if (value != null) {
                        TelemetryData(value, unit, timestamp)
                    } else null
                }
                else -> {
                    // Simple value
                    val value = payload.toDoubleOrNull()
                    if (value != null) {
                        TelemetryData(value, null, null)
                    } else null
                }
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun parseStatusPayload(payload: String): DeviceStatus {
        return try {
            if (payload.startsWith("{")) {
                val status = extractJsonString(payload, "status") ?: "UNKNOWN"
                val isOnline = extractJsonValue(payload, "online")?.toBooleanStrictOrNull() ?: false
                val batteryLevel = extractJsonValue(payload, "battery")?.toIntOrNull()
                
                DeviceStatus(status, isOnline, batteryLevel)
            } else {
                DeviceStatus(payload, true, null)
            }
        } catch (e: Exception) {
            DeviceStatus("UNKNOWN", false, null)
        }
    }
    
    private fun extractJsonValue(json: String, key: String): String? {
        val pattern = "\"$key\"\\s*:\\s*([^,}]+)".toRegex()
        return pattern.find(json)?.groupValues?.get(1)?.trim()?.removeSurrounding("\"")
    }
    
    private fun extractJsonString(json: String, key: String): String? {
        val pattern = "\"$key\"\\s*:\\s*\"([^\"]+)\"".toRegex()
        return pattern.find(json)?.groupValues?.get(1)
    }
    
    private fun createServiceNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("IoTLogic MQTT Service")
            .setContentText("Monitoring MQTT connections")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
    
    private fun updateNotification(message: String) {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("IoTLogic MQTT Service")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
        
        val notificationManager = getSystemService(android.app.NotificationManager::class.java)
        notificationManager.notify(notificationId, notification)
    }
    
    // Public interface
    fun getConnectedBrokers(): Set<String> = connectedBrokers.toSet()
    
    fun isConnectedToBroker(brokerId: String): Boolean = connectedBrokers.contains(brokerId)
    
    fun publishToDevice(deviceToken: String, command: String, parameters: Map<String, Any> = emptyMap()) {
        val topic = "devices/$deviceToken/commands/$command"
        val payload = if (parameters.isNotEmpty()) {
            val paramsJson = parameters.entries.joinToString(",") { "\"${it.key}\":\"${it.value}\"" }
            """{"command":"$command","parameters":{$paramsJson},"timestamp":${System.currentTimeMillis()}}"""
        } else {
            """{"command":"$command","timestamp":${System.currentTimeMillis()}}"""
        }
        
        publishMessage("default", topic, payload)
    }
    
    fun sendTelemetryForDevice(deviceToken: String, sensorType: String, value: Double, unit: String? = null) {
        val topic = "devices/$deviceToken/telemetry/$sensorType"
        val payload = if (unit != null) {
            """{"value":$value,"unit":"$unit","timestamp":${System.currentTimeMillis()}}"""
        } else {
            """{"value":$value,"timestamp":${System.currentTimeMillis()}}"""
        }
        
        publishMessage("default", topic, payload)
    }
}

// Data classes for parsing
data class TelemetryData(
    val value: Double,
    val unit: String?,
    val timestamp: Long?
)

data class DeviceStatus(
    val status: String,
    val isOnline: Boolean,
    val batteryLevel: Int?
)