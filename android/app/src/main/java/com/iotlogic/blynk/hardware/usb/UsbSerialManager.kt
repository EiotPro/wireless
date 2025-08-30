package com.iotlogic.blynk.hardware.usb

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import com.hoho.android.usbserial.driver.*
import com.hoho.android.usbserial.util.SerialInputOutputManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.io.IOException
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages USB Serial communication for LoRa modules and other serial devices
 */
@Singleton
class UsbSerialManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val serialExecutor = Executors.newSingleThreadExecutor()
    
    private val usbManager: UsbManager by lazy {
        context.getSystemService(Context.USB_SERVICE) as UsbManager
    }
    
    // Device management
    private val connectedDevices = mutableMapOf<String, UsbSerialConnection>()
    private val availableDrivers = mutableMapOf<String, UsbSerialDriver>()
    
    // State flows
    private val _deviceList = MutableStateFlow<List<UsbSerialDevice>>(emptyList())
    val deviceList: StateFlow<List<UsbSerialDevice>> = _deviceList.asStateFlow()
    
    private val _connectionState = MutableStateFlow<Map<String, UsbConnectionState>>(emptyMap())
    val connectionState: StateFlow<Map<String, UsbConnectionState>> = _connectionState.asStateFlow()
    
    private val _receivedData = MutableSharedFlow<UsbDataPacket>()
    val receivedData: SharedFlow<UsbDataPacket> = _receivedData.asSharedFlow()
    
    /**
     * Initialize USB Serial manager
     */
    suspend fun initialize(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                detectUsbDevices()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Detect available USB serial devices
     */
    suspend fun detectUsbDevices(): Result<List<UsbSerialDevice>> {
        return withContext(Dispatchers.IO) {
            try {
                val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
                val devices = mutableListOf<UsbSerialDevice>()
                
                availableDrivers.forEach { driver ->
                    val device = driver.device
                    val usbDevice = UsbSerialDevice(
                        deviceId = "${device.vendorId}:${device.productId}",
                        deviceName = device.deviceName,
                        vendorId = device.vendorId,
                        productId = device.productId,
                        serialNumber = device.serialNumber,
                        manufacturerName = device.manufacturerName,
                        productName = device.productName,
                        driverType = driver.javaClass.simpleName,
                        isConnected = false
                    )
                    devices.add(usbDevice)
                    this@UsbSerialManager.availableDrivers[usbDevice.deviceId] = driver
                }
                
                _deviceList.value = devices
                Result.success(devices)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Open connection to USB serial device
     */
    suspend fun openConnection(
        deviceId: String,
        baudRate: Int = 9600,
        dataBits: Int = 8,
        stopBits: Int = UsbSerialPort.STOPBITS_1,
        parity: Int = UsbSerialPort.PARITY_NONE
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val driver = availableDrivers[deviceId]
                    ?: return@withContext Result.failure(Exception("Device not found"))
                
                val connection = usbManager.openDevice(driver.device)
                    ?: return@withContext Result.failure(Exception("Failed to open USB device"))
                
                val port = driver.ports[0] // Use first port
                port.open(connection)
                port.setParameters(baudRate, dataBits, stopBits, parity)
                
                // Setup data listener
                val ioManager = SerialInputOutputManager(port, object : SerialInputOutputManager.Listener {
                    override fun onNewData(data: ByteArray) {
                        scope.launch {
                            val packet = UsbDataPacket(
                                deviceId = deviceId,
                                data = data,
                                timestamp = System.currentTimeMillis()
                            )
                            _receivedData.emit(packet)
                        }
                    }
                    
                    override fun onRunError(e: Exception) {
                        scope.launch {
                            updateConnectionState(deviceId, UsbConnectionState.ERROR)
                        }
                    }
                })
                
                val usbConnection = UsbSerialConnection(
                    deviceId = deviceId,
                    port = port,
                    connection = connection,
                    ioManager = ioManager
                )
                
                connectedDevices[deviceId] = usbConnection
                serialExecutor.submit(ioManager)
                
                updateConnectionState(deviceId, UsbConnectionState.CONNECTED)
                updateDeviceConnectionStatus(deviceId, true)
                
                Result.success(Unit)
            } catch (e: Exception) {
                updateConnectionState(deviceId, UsbConnectionState.ERROR)
                Result.failure(e)
            }
        }
    }
    
    /**
     * Close connection to USB serial device
     */
    suspend fun closeConnection(deviceId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val connection = connectedDevices[deviceId]
                if (connection != null) {
                    connection.ioManager.stop()
                    connection.port.close()
                    connection.connection.close()
                    connectedDevices.remove(deviceId)
                    
                    updateConnectionState(deviceId, UsbConnectionState.DISCONNECTED)
                    updateDeviceConnectionStatus(deviceId, false)
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Send data to USB serial device
     */
    suspend fun sendData(deviceId: String, data: ByteArray, timeout: Int = 1000): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val connection = connectedDevices[deviceId]
                    ?: return@withContext Result.failure(Exception("Device not connected"))
                
                connection.port.write(data, timeout)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Send AT command to LoRa module
     */
    suspend fun sendLoRaCommand(
        deviceId: String,
        command: String,
        waitForResponse: Boolean = true,
        timeout: Long = 5000L
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val atCommand = if (command.startsWith("AT")) command else "AT$command"
                val commandBytes = "$atCommand\r\n".toByteArray()
                
                // Send command
                val sendResult = sendData(deviceId, commandBytes)
                if (sendResult.isFailure) {
                    return@withContext Result.failure(sendResult.exceptionOrNull()!!)
                }
                
                if (!waitForResponse) {
                    return@withContext Result.success("Command sent")
                }
                
                // Wait for response
                val responseChannel = Channel<String>()
                val responseCollectorJob = launch {
                    receivedData
                        .filter { it.deviceId == deviceId }
                        .map { String(it.data) }
                        .collect { response ->
                            if (response.contains("OK") || response.contains("ERROR")) {
                                responseChannel.trySend(response)
                            }
                        }
                }
                
                val response = withTimeoutOrNull(timeout) {
                    responseChannel.receive()
                }
                
                responseCollectorJob.cancel()
                
                if (response != null) {
                    Result.success(response.trim())
                } else {
                    Result.failure(Exception("Command timeout"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Configure LoRa module parameters
     */
    suspend fun configureLoRaModule(
        deviceId: String,
        frequency: String = "868000000", // 868 MHz
        spreadingFactor: Int = 7,
        bandwidth: Int = 125, // 125 kHz
        codingRate: Int = 5,
        power: Int = 14
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Set frequency
                val freqResult = sendLoRaCommand(deviceId, "+FREQ=$frequency")
                if (freqResult.isFailure) {
                    return@withContext Result.failure(Exception("Failed to set frequency"))
                }
                
                // Set spreading factor
                val sfResult = sendLoRaCommand(deviceId, "+SF=$spreadingFactor")
                if (sfResult.isFailure) {
                    return@withContext Result.failure(Exception("Failed to set spreading factor"))
                }
                
                // Set bandwidth
                val bwResult = sendLoRaCommand(deviceId, "+BW=$bandwidth")
                if (bwResult.isFailure) {
                    return@withContext Result.failure(Exception("Failed to set bandwidth"))
                }
                
                // Set coding rate
                val crResult = sendLoRaCommand(deviceId, "+CR=$codingRate")
                if (crResult.isFailure) {
                    return@withContext Result.failure(Exception("Failed to set coding rate"))
                }
                
                // Set transmission power
                val powerResult = sendLoRaCommand(deviceId, "+PWR=$power")
                if (powerResult.isFailure) {
                    return@withContext Result.failure(Exception("Failed to set transmission power"))
                }
                
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Send LoRa message
     */
    suspend fun sendLoRaMessage(deviceId: String, message: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val hexMessage = message.toByteArray().joinToString("") { "%02X".format(it) }
                val result = sendLoRaCommand(deviceId, "+SEND=$hexMessage")
                
                if (result.isSuccess && result.getOrNull()?.contains("OK") == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to send LoRa message"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Put LoRa module in receive mode
     */
    suspend fun startLoRaReceive(deviceId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val result = sendLoRaCommand(deviceId, "+RX")
                if (result.isSuccess) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to start receive mode"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Get connected devices
     */
    fun getConnectedDevices(): List<String> {
        return connectedDevices.keys.toList()
    }
    
    /**
     * Check if device is connected
     */
    fun isDeviceConnected(deviceId: String): Boolean {
        return connectedDevices.containsKey(deviceId)
    }
    
    /**
     * Shutdown USB Serial manager
     */
    suspend fun shutdown() {
        // Close all connections
        val deviceIds = connectedDevices.keys.toList()
        deviceIds.forEach { deviceId ->
            closeConnection(deviceId)
        }
        
        serialExecutor.shutdown()
        scope.cancel()
    }
    
    private fun updateConnectionState(deviceId: String, state: UsbConnectionState) {
        val currentStates = _connectionState.value.toMutableMap()
        currentStates[deviceId] = state
        _connectionState.value = currentStates
    }
    
    private fun updateDeviceConnectionStatus(deviceId: String, isConnected: Boolean) {
        val currentDevices = _deviceList.value.toMutableList()
        val deviceIndex = currentDevices.indexOfFirst { it.deviceId == deviceId }
        if (deviceIndex >= 0) {
            currentDevices[deviceIndex] = currentDevices[deviceIndex].copy(isConnected = isConnected)
            _deviceList.value = currentDevices
        }
    }
}

/**
 * Represents a USB serial device
 */
data class UsbSerialDevice(
    val deviceId: String,
    val deviceName: String,
    val vendorId: Int,
    val productId: Int,
    val serialNumber: String?,
    val manufacturerName: String?,
    val productName: String?,
    val driverType: String,
    val isConnected: Boolean
)

/**
 * Represents received USB data
 */
data class UsbDataPacket(
    val deviceId: String,
    val data: ByteArray,
    val timestamp: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as UsbDataPacket
        return deviceId == other.deviceId && data.contentEquals(other.data)
    }
    
    override fun hashCode(): Int {
        var result = deviceId.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}

/**
 * USB connection wrapper
 */
data class UsbSerialConnection(
    val deviceId: String,
    val port: UsbSerialPort,
    val connection: UsbDeviceConnection,
    val ioManager: SerialInputOutputManager
)

/**
 * USB connection states
 */
enum class UsbConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}