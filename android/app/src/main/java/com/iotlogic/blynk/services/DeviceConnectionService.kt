package com.iotlogic.blynk.services

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.iotlogic.blynk.R
import com.iotlogic.blynk.domain.repository.DeviceRepository
import com.iotlogic.blynk.domain.repository.TelemetryRepository
import com.iotlogic.blynk.hardware.HardwareManager
import com.iotlogic.blynk.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@AndroidEntryPoint
class DeviceConnectionService : Service() {
    
    @Inject
    lateinit var hardwareManager: HardwareManager
    
    @Inject
    lateinit var deviceRepository: DeviceRepository
    
    @Inject
    lateinit var telemetryRepository: TelemetryRepository
    
    private val binder = DeviceConnectionBinder()
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Track active connections
    private val activeConnections = ConcurrentHashMap<String, DeviceConnection>()
    
    // Service state
    private var isRunning = false
    private var notificationId = 1001
    
    companion object {
        const val ACTION_START_SERVICE = "START_SERVICE"
        const val ACTION_STOP_SERVICE = "STOP_SERVICE"
        const val ACTION_CONNECT_DEVICE = "CONNECT_DEVICE"
        const val ACTION_DISCONNECT_DEVICE = "DISCONNECT_DEVICE"
        
        const val EXTRA_DEVICE_ID = "device_id"
        const val EXTRA_DEVICE_PROTOCOL = "device_protocol"
        const val EXTRA_DEVICE_ADDRESS = "device_address"
        
        const val NOTIFICATION_CHANNEL_ID = "device_connection_channel"
        const val NOTIFICATION_CHANNEL_NAME = "Device Connections"
    }
    
    inner class DeviceConnectionBinder : Binder() {
        fun getService(): DeviceConnectionService = this@DeviceConnectionService
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initializeHardware()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SERVICE -> startService()
            ACTION_STOP_SERVICE -> stopService()
            ACTION_CONNECT_DEVICE -> {
                val deviceId = intent.getStringExtra(EXTRA_DEVICE_ID)
                val protocol = intent.getStringExtra(EXTRA_DEVICE_PROTOCOL)
                val address = intent.getStringExtra(EXTRA_DEVICE_ADDRESS)
                if (deviceId != null && protocol != null) {
                    connectToDevice(deviceId, protocol, address)
                }
            }
            ACTION_DISCONNECT_DEVICE -> {
                val deviceId = intent.getStringExtra(EXTRA_DEVICE_ID)
                if (deviceId != null) {
                    disconnectFromDevice(deviceId)
                }
            }
        }
        
        return START_STICKY // Restart service if killed
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
                description = "Notifications for device connection status"
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
        
        // Start monitoring device connections
        serviceScope.launch {
            monitorDeviceConnections()
        }
        
        // Start hardware monitoring
        serviceScope.launch {
            monitorHardwareState()
        }
    }
    
    private fun stopService() {
        isRunning = false
        
        // Disconnect all devices
        serviceScope.launch {
            disconnectAllDevices()
            serviceScope.cancel()
            stopForeground(true)
            stopSelf()
        }
    }
    
    private fun initializeHardware() {
        serviceScope.launch {
            val result = hardwareManager.initializeHardware()
            if (result.isFailure) {
                // Handle hardware initialization failure
                updateNotification("Hardware initialization failed")
            }
        }
    }
    
    private suspend fun monitorDeviceConnections() {
        deviceRepository.getDevices().collect { devices ->
            val connectedDevices = devices.filter { it.isConnected() }
            
            // Connect to devices that should be connected but aren't
            connectedDevices.forEach { device ->
                if (!activeConnections.containsKey(device.id)) {
                    connectToDevice(device.id, device.protocol, device.macAddress ?: device.ipAddress)
                }
            }
            
            // Disconnect from devices that shouldn't be connected
            val deviceIds = devices.map { it.id }.toSet()
            activeConnections.keys.filter { it !in deviceIds }.forEach { deviceId ->
                disconnectFromDevice(deviceId)
            }
            
            updateNotification("${activeConnections.size} devices connected")
        }
    }
    
    private suspend fun monitorHardwareState() {
        hardwareManager.hardwareState.collect { state ->
            if (!state.isInitialized) {
                // Try to reinitialize hardware
                val result = hardwareManager.initializeHardware()
                if (result.isFailure) {
                    updateNotification("Hardware error - retrying...")
                }
            }
        }
    }
    
    private fun connectToDevice(deviceId: String, protocol: String, address: String?) {
        if (activeConnections.containsKey(deviceId)) return
        
        serviceScope.launch {
            try {
                val connection = when (protocol.uppercase()) {
                    "BLE", "BLUETOOTH" -> {
                        if (address != null) {
                            val result = hardwareManager.getBluetoothManager().connectToDevice(address)
                            if (result.isSuccess) {
                                BleDeviceConnection(deviceId, address, hardwareManager, deviceRepository, telemetryRepository)
                            } else {
                                null
                            }
                        } else null
                    }
                    "WIFI", "HTTP" -> {
                        WiFiDeviceConnection(deviceId, address, hardwareManager, deviceRepository, telemetryRepository)
                    }
                    "USB", "SERIAL" -> {
                        val result = hardwareManager.getUsbManager().openConnection(deviceId)
                        if (result.isSuccess) {
                            UsbDeviceConnection(deviceId, hardwareManager, deviceRepository, telemetryRepository)
                        } else {
                            null
                        }
                    }
                    "MQTT" -> {
                        MqttDeviceConnection(deviceId, hardwareManager, deviceRepository, telemetryRepository)
                    }
                    else -> null
                }
                
                connection?.let {
                    activeConnections[deviceId] = it
                    it.start()
                    
                    // Update device status
                    deviceRepository.updateDeviceStatus(deviceId, "CONNECTED")
                    deviceRepository.updateDeviceOnlineStatus(deviceId, true)
                    
                    updateNotification("${activeConnections.size} devices connected")
                }
            } catch (e: Exception) {
                // Handle connection error
                deviceRepository.updateDeviceStatus(deviceId, "ERROR")
            }
        }
    }
    
    private fun disconnectFromDevice(deviceId: String) {
        activeConnections[deviceId]?.let { connection ->
            serviceScope.launch {
                connection.stop()
                activeConnections.remove(deviceId)
                
                // Update device status
                deviceRepository.updateDeviceStatus(deviceId, "DISCONNECTED")
                deviceRepository.updateDeviceOnlineStatus(deviceId, false)
                
                updateNotification("${activeConnections.size} devices connected")
            }
        }
    }
    
    private suspend fun disconnectAllDevices() {
        activeConnections.values.forEach { connection ->
            connection.stop()
        }
        activeConnections.clear()
    }
    
    private fun createServiceNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("IoTLogic Device Service")
            .setContentText("Maintaining device connections")
            .setSmallIcon(R.drawable.ic_notification) // You'd need to add this icon
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
    
    private fun updateNotification(message: String) {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("IoTLogic Device Service")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
        
        val notificationManager = getSystemService(android.app.NotificationManager::class.java)
        notificationManager.notify(notificationId, notification)
    }
    
    // Public interface for activities/fragments
    fun getActiveConnections(): Map<String, DeviceConnection> {
        return activeConnections.toMap()
    }
    
    fun isDeviceConnected(deviceId: String): Boolean {
        return activeConnections.containsKey(deviceId)
    }
    
    fun sendCommandToDevice(deviceId: String, command: String, parameters: Map<String, Any> = emptyMap()) {
        activeConnections[deviceId]?.sendCommand(command, parameters)
    }
}

/**
 * Base class for device connections
 */
abstract class DeviceConnection(
    protected val deviceId: String,
    protected val hardwareManager: HardwareManager,
    protected val deviceRepository: DeviceRepository,
    protected val telemetryRepository: TelemetryRepository
) {
    protected val connectionScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    protected var isActive = false
    
    abstract suspend fun start()
    abstract suspend fun stop()
    abstract fun sendCommand(command: String, parameters: Map<String, Any>)
    
    protected suspend fun updateLastSeen() {
        deviceRepository.updateDeviceOnlineStatus(deviceId, true)
    }
}

/**
 * BLE device connection implementation
 */
class BleDeviceConnection(
    deviceId: String,
    private val deviceAddress: String,
    hardwareManager: HardwareManager,
    deviceRepository: DeviceRepository,
    telemetryRepository: TelemetryRepository
) : DeviceConnection(deviceId, hardwareManager, deviceRepository, telemetryRepository) {
    
    override suspend fun start() {
        isActive = true
        
        // Monitor BLE connection state
        connectionScope.launch {
            hardwareManager.getBluetoothManager().connectionState.collect { states ->
                val deviceState = states[deviceAddress]
                if (deviceState != null) {
                    when (deviceState.name) {
                        "CONNECTED", "SERVICES_DISCOVERED" -> updateLastSeen()
                        "DISCONNECTED" -> {
                            if (isActive) {
                                // Try to reconnect
                                delay(5000)
                                hardwareManager.getBluetoothManager().connectToDevice(deviceAddress)
                            }
                        }
                    }
                }
            }
        }
    }
    
    override suspend fun stop() {
        isActive = false
        hardwareManager.getBluetoothManager().disconnectDevice(deviceAddress)
        connectionScope.cancel()
    }
    
    override fun sendCommand(command: String, parameters: Map<String, Any>) {
        // Implement BLE command sending
        connectionScope.launch {
            // This would send BLE commands via characteristics
        }
    }
}

/**
 * WiFi device connection implementation
 */
class WiFiDeviceConnection(
    deviceId: String,
    private val deviceAddress: String?,
    hardwareManager: HardwareManager,
    deviceRepository: DeviceRepository,
    telemetryRepository: TelemetryRepository
) : DeviceConnection(deviceId, hardwareManager, deviceRepository, telemetryRepository) {
    
    override suspend fun start() {
        isActive = true
        
        // Monitor WiFi device via HTTP pings
        connectionScope.launch {
            while (isActive) {
                deviceAddress?.let { address ->
                    val isReachable = hardwareManager.getWifiManager().pingDevice(address)
                    if (isReachable) {
                        updateLastSeen()
                    }
                }
                delay(30000) // Check every 30 seconds
            }
        }
    }
    
    override suspend fun stop() {
        isActive = false
        connectionScope.cancel()
    }
    
    override fun sendCommand(command: String, parameters: Map<String, Any>) {
        connectionScope.launch {
            deviceAddress?.let { address ->
                hardwareManager.getWifiManager().sendHttpCommand(
                    address.split(":")[0],
                    address.split(":").getOrElse(1) { "80" }.toInt(),
                    "/command",
                    "POST",
                    """{"command":"$command","parameters":$parameters}""",
                    mapOf("Content-Type" to "application/json")
                )
            }
        }
    }
}

/**
 * USB device connection implementation
 */
class UsbDeviceConnection(
    deviceId: String,
    hardwareManager: HardwareManager,
    deviceRepository: DeviceRepository,
    telemetryRepository: TelemetryRepository
) : DeviceConnection(deviceId, hardwareManager, deviceRepository, telemetryRepository) {
    
    override suspend fun start() {
        isActive = true
        
        // Monitor USB connection
        connectionScope.launch {
            hardwareManager.getUsbManager().receivedData.collect { dataPacket ->
                if (dataPacket.deviceId == deviceId) {
                    updateLastSeen()
                    // Process received data
                }
            }
        }
    }
    
    override suspend fun stop() {
        isActive = false
        hardwareManager.getUsbManager().closeConnection(deviceId)
        connectionScope.cancel()
    }
    
    override fun sendCommand(command: String, parameters: Map<String, Any>) {
        connectionScope.launch {
            hardwareManager.getUsbManager().sendLoRaCommand(deviceId, command)
        }
    }
}

/**
 * MQTT device connection implementation
 */
class MqttDeviceConnection(
    deviceId: String,
    hardwareManager: HardwareManager,
    deviceRepository: DeviceRepository,
    telemetryRepository: TelemetryRepository
) : DeviceConnection(deviceId, hardwareManager, deviceRepository, telemetryRepository) {
    
    override suspend fun start() {
        isActive = true
        
        // Monitor MQTT messages
        connectionScope.launch {
            hardwareManager.getMqttManager().receivedMessages.collect { message ->
                if (message.topic.contains(deviceId)) {
                    updateLastSeen()
                    // Process MQTT message
                }
            }
        }
    }
    
    override suspend fun stop() {
        isActive = false
        connectionScope.cancel()
    }
    
    override fun sendCommand(command: String, parameters: Map<String, Any>) {
        connectionScope.launch {
            hardwareManager.getMqttManager().sendDeviceCommand("default", deviceId, command, parameters)
        }
    }
}