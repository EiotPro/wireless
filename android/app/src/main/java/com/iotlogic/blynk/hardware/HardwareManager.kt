package com.iotlogic.blynk.hardware

import android.bluetooth.BluetoothManager
import android.content.Context
import android.hardware.usb.UsbManager
import android.net.wifi.WifiManager
import com.iotlogic.blynk.hardware.bluetooth.BluetoothLeManager
import com.iotlogic.blynk.hardware.mqtt.MqttConnectionManager
import com.iotlogic.blynk.hardware.usb.UsbSerialManager
import com.iotlogic.blynk.hardware.wifi.WiFiDeviceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Main hardware manager that coordinates all hardware interfaces
 * Provides unified access to Bluetooth LE, WiFi, USB Serial, and MQTT protocols
 */
@Singleton
class HardwareManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bluetoothLeManager: BluetoothLeManager,
    private val wifiDeviceManager: WiFiDeviceManager,
    private val usbSerialManager: UsbSerialManager,
    private val mqttConnectionManager: MqttConnectionManager
) {
    
    private val _hardwareState = MutableStateFlow(HardwareState())
    val hardwareState: StateFlow<HardwareState> = _hardwareState.asStateFlow()
    
    private val bluetoothManager: BluetoothManager by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }
    
    private val wifiManager: WifiManager by lazy {
        context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }
    
    private val usbManager: UsbManager by lazy {
        context.getSystemService(Context.USB_SERVICE) as UsbManager
    }
    
    /**
     * Initialize all hardware interfaces
     */
    suspend fun initializeHardware(): Result<Unit> {
        return try {
            // Initialize Bluetooth LE
            val bleResult = bluetoothLeManager.initialize()
            
            // Initialize WiFi
            val wifiResult = wifiDeviceManager.initialize()
            
            // Initialize USB Serial
            val usbResult = usbSerialManager.initialize()
            
            // Initialize MQTT
            val mqttResult = mqttConnectionManager.initialize()
            
            // Update hardware state
            _hardwareState.value = _hardwareState.value.copy(
                isBluetoothEnabled = bleResult.isSuccess,
                isWifiEnabled = wifiResult.isSuccess,
                isUsbEnabled = usbResult.isSuccess,
                isMqttEnabled = mqttResult.isSuccess,
                isInitialized = true
            )
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get available protocols based on hardware capabilities
     */
    fun getAvailableProtocols(): List<HardwareProtocol> {
        val protocols = mutableListOf<HardwareProtocol>()
        
        val state = _hardwareState.value
        
        if (state.isBluetoothEnabled) {
            protocols.add(HardwareProtocol.BLUETOOTH_LE)
        }
        
        if (state.isWifiEnabled) {
            protocols.add(HardwareProtocol.WIFI)
        }
        
        if (state.isUsbEnabled) {
            protocols.add(HardwareProtocol.USB_SERIAL)
        }
        
        if (state.isMqttEnabled) {
            protocols.add(HardwareProtocol.MQTT)
        }
        
        return protocols
    }
    
    /**
     * Check if specific protocol is available
     */
    fun isProtocolAvailable(protocol: HardwareProtocol): Boolean {
        return when (protocol) {
            HardwareProtocol.BLUETOOTH_LE -> _hardwareState.value.isBluetoothEnabled
            HardwareProtocol.WIFI -> _hardwareState.value.isWifiEnabled
            HardwareProtocol.USB_SERIAL -> _hardwareState.value.isUsbEnabled
            HardwareProtocol.MQTT -> _hardwareState.value.isMqttEnabled
        }
    }
    
    /**
     * Get specific hardware manager instance
     */
    fun getBluetoothManager(): BluetoothLeManager = bluetoothLeManager
    fun getWifiManager(): WiFiDeviceManager = wifiDeviceManager
    fun getUsbManager(): UsbSerialManager = usbSerialManager
    fun getMqttManager(): MqttConnectionManager = mqttConnectionManager
    
    /**
     * Shutdown all hardware interfaces
     */
    suspend fun shutdown() {
        bluetoothLeManager.shutdown()
        wifiDeviceManager.shutdown()
        usbSerialManager.shutdown()
        mqttConnectionManager.shutdown()
        
        _hardwareState.value = HardwareState()
    }
}

/**
 * Represents the current state of all hardware interfaces
 */
data class HardwareState(
    val isInitialized: Boolean = false,
    val isBluetoothEnabled: Boolean = false,
    val isWifiEnabled: Boolean = false,
    val isUsbEnabled: Boolean = false,
    val isMqttEnabled: Boolean = false,
    val lastError: String? = null
)

/**
 * Supported hardware protocols
 */
enum class HardwareProtocol {
    BLUETOOTH_LE,
    WIFI,
    USB_SERIAL,
    MQTT
}