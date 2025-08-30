package com.iotlogic.blynk.hardware.bluetooth

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages Bluetooth LE operations including scanning, connecting, and communicating with devices
 */
@Singleton
class BluetoothLeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val bluetoothManager: BluetoothManager by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }
    
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager.adapter
    }
    
    private val bluetoothLeScanner: BluetoothLeScanner? by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }
    
    // Device management
    private val connectedDevices = ConcurrentHashMap<String, BluetoothGatt>()
    private val discoveredDevices = mutableMapOf<String, BleDevice>()
    
    // State flows
    private val _scanResults = MutableSharedFlow<BleDevice>()
    val scanResults: SharedFlow<BleDevice> = _scanResults.asSharedFlow()
    
    private val _connectionState = MutableStateFlow<Map<String, BleConnectionState>>(emptyMap())
    val connectionState: StateFlow<Map<String, BleConnectionState>> = _connectionState.asStateFlow()
    
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()
    
    // Scanning
    private var scanCallback: ScanCallback? = null
    private var scanJob: Job? = null
    
    /**
     * Initialize Bluetooth LE manager
     */
    suspend fun initialize(): Result<Unit> {
        return withContext(Dispatchers.Main) {
            try {
                if (bluetoothAdapter == null) {
                    Result.failure(Exception("Bluetooth not supported on this device"))
                } else if (!bluetoothAdapter!!.isEnabled) {
                    Result.failure(Exception("Bluetooth is not enabled"))
                } else {
                    Result.success(Unit)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Start scanning for BLE devices
     */
    suspend fun startScan(
        serviceUuids: List<ParcelUuid>? = null,
        scanDuration: Long = 30000L // 30 seconds default
    ): Result<Unit> {
        return withContext(Dispatchers.Main) {
            try {
                if (!hasBluetoothPermissions()) {
                    return@withContext Result.failure(Exception("Missing Bluetooth permissions"))
                }
                
                if (_isScanning.value) {
                    stopScan()
                }
                
                val scanner = bluetoothLeScanner
                    ?: return@withContext Result.failure(Exception("Bluetooth LE scanner not available"))
                
                val settings = ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                    .setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT)
                    .setReportDelay(0L)
                    .build()
                
                val filters = serviceUuids?.map { uuid ->
                    ScanFilter.Builder()
                        .setServiceUuid(uuid)
                        .build()
                } ?: emptyList()
                
                scanCallback = object : ScanCallback() {
                    override fun onScanResult(callbackType: Int, result: ScanResult) {
                        val device = result.device
                        val bleDevice = BleDevice(
                            address = device.address,
                            name = device.name ?: "Unknown Device",
                            rssi = result.rssi,
                            advertisementData = result.scanRecord?.bytes,
                            serviceUuids = result.scanRecord?.serviceUuids?.map { it.uuid } ?: emptyList(),
                            isConnectable = result.isConnectable,
                            timestamp = System.currentTimeMillis()
                        )
                        
                        discoveredDevices[device.address] = bleDevice
                        scope.launch {
                            _scanResults.emit(bleDevice)
                        }
                    }
                    
                    override fun onBatchScanResults(results: MutableList<ScanResult>) {
                        results.forEach { result ->
                            onScanResult(ScanSettings.CALLBACK_TYPE_ALL_MATCHES, result)
                        }
                    }
                    
                    override fun onScanFailed(errorCode: Int) {
                        scope.launch {
                            _isScanning.value = false
                            // Handle scan failure
                        }
                    }
                }
                
                scanner.startScan(filters, settings, scanCallback)
                _isScanning.value = true
                
                // Auto-stop scanning after duration
                scanJob = scope.launch {
                    delay(scanDuration)
                    stopScan()
                }
                
                Result.success(Unit)
            } catch (e: SecurityException) {
                Result.failure(Exception("Missing Bluetooth permissions: ${e.message}"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Stop scanning for BLE devices
     */
    suspend fun stopScan() {
        withContext(Dispatchers.Main) {
            try {
                scanJob?.cancel()
                scanCallback?.let { callback ->
                    if (hasBluetoothPermissions()) {
                        bluetoothLeScanner?.stopScan(callback)
                    }
                }
                scanCallback = null
                _isScanning.value = false
            } catch (e: SecurityException) {
                // Handle permission errors
            }
        }
    }
    
    /**
     * Connect to a BLE device
     */
    suspend fun connectToDevice(deviceAddress: String): Result<BluetoothGatt> {
        return withContext(Dispatchers.Main) {
            try {
                if (!hasBluetoothPermissions()) {
                    return@withContext Result.failure(Exception("Missing Bluetooth permissions"))
                }
                
                val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
                    ?: return@withContext Result.failure(Exception("Device not found"))
                
                val gattCallback = object : BluetoothGattCallback() {
                    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                        val connectionState = when (newState) {
                            BluetoothProfile.STATE_CONNECTED -> {
                                connectedDevices[deviceAddress] = gatt
                                scope.launch {
                                    // Discover services after connection
                                    delay(600) // Small delay for stable connection
                                    if (hasBluetoothPermissions()) {
                                        gatt.discoverServices()
                                    }
                                }
                                BleConnectionState.CONNECTED
                            }
                            BluetoothProfile.STATE_DISCONNECTED -> {
                                connectedDevices.remove(deviceAddress)
                                BleConnectionState.DISCONNECTED
                            }
                            BluetoothProfile.STATE_CONNECTING -> BleConnectionState.CONNECTING
                            BluetoothProfile.STATE_DISCONNECTING -> BleConnectionState.DISCONNECTING
                            else -> BleConnectionState.DISCONNECTED
                        }
                        
                        updateConnectionState(deviceAddress, connectionState)
                    }
                    
                    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            updateConnectionState(deviceAddress, BleConnectionState.SERVICES_DISCOVERED)
                        }
                    }
                    
                    override fun onCharacteristicRead(
                        gatt: BluetoothGatt,
                        characteristic: BluetoothGattCharacteristic,
                        status: Int
                    ) {
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            // Handle characteristic read
                        }
                    }
                    
                    override fun onCharacteristicWrite(
                        gatt: BluetoothGatt,
                        characteristic: BluetoothGattCharacteristic,
                        status: Int
                    ) {
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            // Handle characteristic write
                        }
                    }
                    
                    override fun onCharacteristicChanged(
                        gatt: BluetoothGatt,
                        characteristic: BluetoothGattCharacteristic
                    ) {
                        // Handle notifications/indications
                    }
                }
                
                val gatt = device.connectGatt(context, false, gattCallback)
                Result.success(gatt)
                
            } catch (e: SecurityException) {
                Result.failure(Exception("Missing Bluetooth permissions: ${e.message}"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Disconnect from a BLE device
     */
    suspend fun disconnectDevice(deviceAddress: String): Result<Unit> {
        return withContext(Dispatchers.Main) {
            try {
                val gatt = connectedDevices[deviceAddress]
                if (gatt != null && hasBluetoothPermissions()) {
                    gatt.disconnect()
                    gatt.close()
                    connectedDevices.remove(deviceAddress)
                }
                Result.success(Unit)
            } catch (e: SecurityException) {
                Result.failure(Exception("Missing Bluetooth permissions: ${e.message}"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Send command to a connected BLE device
     */
    suspend fun sendCommand(
        deviceAddress: String,
        serviceUuid: UUID,
        characteristicUuid: UUID,
        data: ByteArray
    ): Result<Unit> {
        return withContext(Dispatchers.Main) {
            try {
                if (!hasBluetoothPermissions()) {
                    return@withContext Result.failure(Exception("Missing Bluetooth permissions"))
                }
                
                val gatt = connectedDevices[deviceAddress]
                    ?: return@withContext Result.failure(Exception("Device not connected"))
                
                val service = gatt.getService(serviceUuid)
                    ?: return@withContext Result.failure(Exception("Service not found"))
                
                val characteristic = service.getCharacteristic(characteristicUuid)
                    ?: return@withContext Result.failure(Exception("Characteristic not found"))
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val result = gatt.writeCharacteristic(characteristic, data, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
                    if (result == BluetoothStatusCodes.SUCCESS) {
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception("Write failed with status: $result"))
                    }
                } else {
                    @Suppress("DEPRECATION")
                    characteristic.value = data
                    @Suppress("DEPRECATION")
                    val success = gatt.writeCharacteristic(characteristic)
                    if (success) {
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception("Write characteristic failed"))
                    }
                }
            } catch (e: SecurityException) {
                Result.failure(Exception("Missing Bluetooth permissions: ${e.message}"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Subscribe to notifications from a BLE characteristic
     */
    suspend fun subscribeToNotifications(
        deviceAddress: String,
        serviceUuid: UUID,
        characteristicUuid: UUID
    ): Result<Flow<ByteArray>> {
        return withContext(Dispatchers.Main) {
            try {
                if (!hasBluetoothPermissions()) {
                    return@withContext Result.failure(Exception("Missing Bluetooth permissions"))
                }
                
                val gatt = connectedDevices[deviceAddress]
                    ?: return@withContext Result.failure(Exception("Device not connected"))
                
                val service = gatt.getService(serviceUuid)
                    ?: return@withContext Result.failure(Exception("Service not found"))
                
                val characteristic = service.getCharacteristic(characteristicUuid)
                    ?: return@withContext Result.failure(Exception("Characteristic not found"))
                
                val success = gatt.setCharacteristicNotification(characteristic, true)
                if (!success) {
                    return@withContext Result.failure(Exception("Failed to enable notifications"))
                }
                
                // Enable notifications on the device
                val descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                if (descriptor != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                    } else {
                        @Suppress("DEPRECATION")
                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        @Suppress("DEPRECATION")
                        gatt.writeDescriptor(descriptor)
                    }
                }
                
                // Create flow for notifications (implementation would need callback handling)
                val notificationFlow = flow<ByteArray> {
                    // This would be implemented with proper callback handling
                }
                
                Result.success(notificationFlow)
            } catch (e: SecurityException) {
                Result.failure(Exception("Missing Bluetooth permissions: ${e.message}"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Get list of discovered devices
     */
    fun getDiscoveredDevices(): List<BleDevice> {
        return discoveredDevices.values.toList()
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
    fun isDeviceConnected(deviceAddress: String): Boolean {
        return connectedDevices.containsKey(deviceAddress)
    }
    
    /**
     * Shutdown BLE manager
     */
    suspend fun shutdown() {
        stopScan()
        
        // Disconnect all devices
        val devices = connectedDevices.keys.toList()
        devices.forEach { address ->
            disconnectDevice(address)
        }
        
        scope.cancel()
    }
    
    private fun updateConnectionState(deviceAddress: String, state: BleConnectionState) {
        val currentStates = _connectionState.value.toMutableMap()
        currentStates[deviceAddress] = state
        _connectionState.value = currentStates
    }
    
    private fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }
}

/**
 * Represents a discovered BLE device
 */
data class BleDevice(
    val address: String,
    val name: String,
    val rssi: Int,
    val advertisementData: ByteArray?,
    val serviceUuids: List<UUID>,
    val isConnectable: Boolean,
    val timestamp: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BleDevice
        return address == other.address
    }
    
    override fun hashCode(): Int {
        return address.hashCode()
    }
}

/**
 * BLE connection states
 */
enum class BleConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    SERVICES_DISCOVERED,
    DISCONNECTING
}