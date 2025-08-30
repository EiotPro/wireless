package com.iotlogic.blynk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iotlogic.blynk.domain.model.Device
import com.iotlogic.blynk.domain.repository.DeviceRepository
import com.iotlogic.blynk.hardware.HardwareManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val hardwareManager: HardwareManager
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(DeviceUiState())
    val uiState: StateFlow<DeviceUiState> = _uiState.asStateFlow()
    
    // Device list
    val devices: StateFlow<List<Device>> = deviceRepository.getDevices()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Online devices
    val onlineDevices: StateFlow<List<Device>> = deviceRepository.getOnlineDevices()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Hardware state
    val hardwareState = hardwareManager.hardwareState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = hardwareManager.hardwareState.value
        )
    
    init {
        initializeHardware()
        loadDevices()
    }
    
    /**
     * Initialize hardware managers
     */
    private fun initializeHardware() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val result = hardwareManager.initializeHardware()
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
    
    /**
     * Load devices from repository
     */
    fun loadDevices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Sync with backend if needed
                // val result = deviceRepository.syncWithBackend(token)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    /**
     * Search devices
     */
    fun searchDevices(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
    
    /**
     * Get filtered devices based on search query
     */
    val filteredDevices: StateFlow<List<Device>> = combine(
        devices,
        _uiState.map { it.searchQuery }
    ) { deviceList, query ->
        if (query.isEmpty()) {
            deviceList
        } else {
            deviceList.filter { device ->
                device.name.contains(query, ignoreCase = true) ||
                device.type.contains(query, ignoreCase = true) ||
                device.protocol.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    /**
     * Filter devices by protocol
     */
    fun filterByProtocol(protocol: String?) {
        _uiState.value = _uiState.value.copy(selectedProtocol = protocol)
    }
    
    /**
     * Get devices filtered by protocol
     */
    fun getDevicesByProtocol(protocol: String): Flow<List<Device>> {
        return deviceRepository.getDevicesByProtocol(protocol)
    }
    
    /**
     * Add a new device
     */
    fun addDevice(device: Device) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val result = deviceRepository.addDevice(device)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Device added successfully"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
    
    /**
     * Update device
     */
    fun updateDevice(device: Device) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val result = deviceRepository.updateDevice(device)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Device updated successfully"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
    
    /**
     * Delete device
     */
    fun deleteDevice(deviceId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val result = deviceRepository.deleteDevice(deviceId)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Device deleted successfully"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
    
    /**
     * Connect to device using appropriate hardware manager
     */
    fun connectToDevice(device: Device) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                when (device.getProtocolType()) {
                    com.iotlogic.blynk.domain.model.ProtocolType.BLUETOOTH_LE -> {
                        device.macAddress?.let { address ->
                            val result = hardwareManager.getBluetoothManager().connectToDevice(address)
                            handleConnectionResult(device.id, result.isSuccess)
                        }
                    }
                    com.iotlogic.blynk.domain.model.ProtocolType.WIFI -> {
                        // WiFi connection logic would go here
                        handleConnectionResult(device.id, true)
                    }
                    com.iotlogic.blynk.domain.model.ProtocolType.USB_SERIAL -> {
                        val result = hardwareManager.getUsbManager().openConnection(device.id)
                        handleConnectionResult(device.id, result.isSuccess)
                    }
                    com.iotlogic.blynk.domain.model.ProtocolType.MQTT -> {
                        // MQTT connection logic would go here
                        handleConnectionResult(device.id, true)
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Unsupported protocol: ${device.protocol}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    /**
     * Disconnect from device
     */
    fun disconnectFromDevice(device: Device) {
        viewModelScope.launch {
            try {
                when (device.getProtocolType()) {
                    com.iotlogic.blynk.domain.model.ProtocolType.BLUETOOTH_LE -> {
                        device.macAddress?.let { address ->
                            hardwareManager.getBluetoothManager().disconnectDevice(address)
                        }
                    }
                    com.iotlogic.blynk.domain.model.ProtocolType.USB_SERIAL -> {
                        hardwareManager.getUsbManager().closeConnection(device.id)
                    }
                    else -> {
                        // Handle other protocols
                    }
                }
                
                deviceRepository.updateDeviceStatus(device.id, "DISCONNECTED")
                deviceRepository.updateDeviceOnlineStatus(device.id, false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    /**
     * Refresh devices
     */
    fun refresh() {
        loadDevices()
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * Clear message
     */
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
    
    /**
     * Get device statistics
     */
    suspend fun getDeviceStatistics(): DeviceStatistics {
        val totalDevices = deviceRepository.getDeviceCount()
        val bleDevices = deviceRepository.getDeviceCountByProtocol("BLE")
        val wifiDevices = deviceRepository.getDeviceCountByProtocol("WiFi")
        val usbDevices = deviceRepository.getDeviceCountByProtocol("USB")
        val mqttDevices = deviceRepository.getDeviceCountByProtocol("MQTT")
        
        return DeviceStatistics(
            totalDevices = totalDevices,
            bleDevices = bleDevices,
            wifiDevices = wifiDevices,
            usbDevices = usbDevices,
            mqttDevices = mqttDevices,
            onlineDevices = onlineDevices.value.size
        )
    }
    
    private fun handleConnectionResult(deviceId: String, success: Boolean) {
        viewModelScope.launch {
            if (success) {
                deviceRepository.updateDeviceStatus(deviceId, "CONNECTED")
                deviceRepository.updateDeviceOnlineStatus(deviceId, true)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Connected to device successfully"
                )
            } else {
                deviceRepository.updateDeviceStatus(deviceId, "DISCONNECTED")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to connect to device"
                )
            }
        }
    }
}

/**
 * UI state for device screen
 */
data class DeviceUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val searchQuery: String = "",
    val selectedProtocol: String? = null
)

/**
 * Device statistics
 */
data class DeviceStatistics(
    val totalDevices: Int,
    val bleDevices: Int,
    val wifiDevices: Int,
    val usbDevices: Int,
    val mqttDevices: Int,
    val onlineDevices: Int
)