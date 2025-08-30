package com.iotlogic.blynk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iotlogic.blynk.domain.model.Telemetry
import com.iotlogic.blynk.domain.repository.TelemetryRepository
import com.iotlogic.blynk.hardware.HardwareManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class TelemetryViewModel @Inject constructor(
    private val telemetryRepository: TelemetryRepository,
    private val hardwareManager: HardwareManager
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(TelemetryUiState())
    val uiState: StateFlow<TelemetryUiState> = _uiState.asStateFlow()
    
    // Recent telemetry data
    val recentTelemetry: StateFlow<List<Telemetry>> = telemetryRepository.getRecentTelemetry(100)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Selected device telemetry
    private val _selectedDeviceId = MutableStateFlow<String?>(null)
    val selectedDeviceId: StateFlow<String?> = _selectedDeviceId.asStateFlow()
    
    val deviceTelemetry: StateFlow<List<Telemetry>> = _selectedDeviceId
        .filterNotNull()
        .flatMapLatest { deviceId ->
            telemetryRepository.getTelemetryByDevice(deviceId, 200)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Sensor type filter
    private val _selectedSensorType = MutableStateFlow<String?>(null)
    val selectedSensorType: StateFlow<String?> = _selectedSensorType.asStateFlow()
    
    // Filtered telemetry by sensor type
    val filteredTelemetry: StateFlow<List<Telemetry>> = combine(
        deviceTelemetry,
        _selectedSensorType
    ) { telemetryList, sensorType ->
        if (sensorType == null) {
            telemetryList
        } else {
            telemetryList.filter { it.sensorType == sensorType }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    // Time range filter
    private val _timeRange = MutableStateFlow(TimeRange.LAST_HOUR)
    val timeRange: StateFlow<TimeRange> = _timeRange.asStateFlow()
    
    // Time-filtered telemetry
    val timeFilteredTelemetry: StateFlow<List<Telemetry>> = combine(
        filteredTelemetry,
        _timeRange
    ) { telemetryList, range ->
        val cutoffTime = System.currentTimeMillis() - range.durationMs
        telemetryList.filter { it.timestamp >= cutoffTime }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    init {
        observeRealtimeData()
    }
    
    /**
     * Set selected device for telemetry viewing
     */
    fun selectDevice(deviceId: String?) {
        _selectedDeviceId.value = deviceId
        if (deviceId != null) {
            loadTelemetryForDevice(deviceId)
        }
    }
    
    /**
     * Set sensor type filter
     */
    fun selectSensorType(sensorType: String?) {
        _selectedSensorType.value = sensorType
    }
    
    /**
     * Set time range filter
     */
    fun setTimeRange(range: TimeRange) {
        _timeRange.value = range
    }
    
    /**
     * Load telemetry data for specific device
     */
    private fun loadTelemetryForDevice(deviceId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Load sensor types for the device
                val sensorTypes = telemetryRepository.getSensorTypesForDevice(deviceId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    availableSensorTypes = sensorTypes
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    /**
     * Get telemetry statistics for a device and sensor
     */
    fun getTelemetryStatistics(deviceId: String, sensorType: String, timeRange: TimeRange): Flow<TelemetryStatistics> {
        return flow {
            val endTime = System.currentTimeMillis()
            val startTime = endTime - timeRange.durationMs
            
            val avg = telemetryRepository.getAverageTelemetryValue(deviceId, sensorType, startTime, endTime)
            val min = telemetryRepository.getMinTelemetryValue(deviceId, sensorType, startTime, endTime)
            val max = telemetryRepository.getMaxTelemetryValue(deviceId, sensorType, startTime, endTime)
            val count = telemetryRepository.getTelemetryCountSince(deviceId, startTime)
            
            emit(TelemetryStatistics(
                average = avg ?: 0.0,
                minimum = min ?: 0.0,
                maximum = max ?: 0.0,
                count = count,
                timeRange = timeRange
            ))
        }
    }
    
    /**
     * Get latest telemetry for device
     */
    suspend fun getLatestTelemetryForDevice(deviceId: String): Telemetry? {
        return telemetryRepository.getLatestTelemetryForDevice(deviceId)
    }
    
    /**
     * Store new telemetry data
     */
    fun storeTelemetryData(telemetry: Telemetry) {
        viewModelScope.launch {
            val result = telemetryRepository.storeTelemetryData(telemetry)
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
    
    /**
     * Store multiple telemetry data points
     */
    fun storeTelemetryBatch(telemetryList: List<Telemetry>) {
        viewModelScope.launch {
            val result = telemetryRepository.storeTelemetryBatch(telemetryList)
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
    
    /**
     * Clear old telemetry data
     */
    fun clearOldTelemetry(daysToKeep: Int = 30) {
        viewModelScope.launch {
            val cutoffTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(daysToKeep.toLong())
            val result = telemetryRepository.deleteOldTelemetry(cutoffTime)
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    message = "Old telemetry data cleared successfully"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
    
    /**
     * Export telemetry data
     */
    fun exportTelemetryData(deviceId: String, startTime: Long, endTime: Long): Flow<List<Telemetry>> {
        return telemetryRepository.getTelemetryByTimeRange(deviceId, startTime, endTime)
    }
    
    /**
     * Sync telemetry with backend
     */
    fun syncTelemetryData(token: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true)
            
            val result = telemetryRepository.uploadPendingTelemetry(token)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    message = "Telemetry data synced successfully"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
    
    /**
     * Observe real-time data from hardware managers
     */
    private fun observeRealtimeData() {
        // Observe MQTT messages for telemetry
        viewModelScope.launch {
            hardwareManager.getMqttManager().receivedMessages.collect { message ->
                if (message.topic.contains("/telemetry/")) {
                    parseMqttTelemetryMessage(message.topic, message.payloadString)
                }
            }
        }
        
        // Observe USB serial data
        viewModelScope.launch {
            hardwareManager.getUsbManager().receivedData.collect { dataPacket ->
                parseUsbTelemetryData(dataPacket.deviceId, String(dataPacket.data))
            }
        }
    }
    
    /**
     * Parse MQTT telemetry message
     */
    private fun parseMqttTelemetryMessage(topic: String, payload: String) {
        try {
            // Parse topic: devices/{token}/telemetry/{sensorType}
            val parts = topic.split("/")
            if (parts.size >= 4 && parts[0] == "devices" && parts[2] == "telemetry") {
                val deviceToken = parts[1]
                val sensorType = parts[3]
                
                // Parse payload (simplified JSON parsing)
                val value = parseValueFromPayload(payload)
                if (value != null) {
                    val telemetry = Telemetry(
                        id = UUID.randomUUID().toString(),
                        deviceId = deviceToken, // Would need to map token to device ID
                        sensorType = sensorType,
                        value = value,
                        timestamp = System.currentTimeMillis()
                    )
                    storeTelemetryData(telemetry)
                }
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(error = "Failed to parse MQTT telemetry: ${e.message}")
        }
    }
    
    /**
     * Parse USB serial telemetry data
     */
    private fun parseUsbTelemetryData(deviceId: String, data: String) {
        try {
            // Parse LoRa AT command responses or sensor data
            if (data.contains("RECV")) {
                // Parse received LoRa data
                val parts = data.split(",")
                if (parts.size >= 2) {
                    val value = parts[1].toDoubleOrNull()
                    if (value != null) {
                        val telemetry = Telemetry(
                            id = UUID.randomUUID().toString(),
                            deviceId = deviceId,
                            sensorType = "lora_data",
                            value = value,
                            timestamp = System.currentTimeMillis(),
                            rawValue = data
                        )
                        storeTelemetryData(telemetry)
                    }
                }
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(error = "Failed to parse USB telemetry: ${e.message}")
        }
    }
    
    /**
     * Parse value from JSON payload
     */
    private fun parseValueFromPayload(payload: String): Double? {
        return try {
            // Simplified JSON parsing - would use Gson in real implementation
            if (payload.contains("\"value\":")) {
                val valueStart = payload.indexOf("\"value\":") + 8
                val valueEnd = payload.indexOf(",", valueStart).takeIf { it != -1 } ?: payload.indexOf("}", valueStart)
                payload.substring(valueStart, valueEnd).trim().toDoubleOrNull()
            } else {
                payload.toDoubleOrNull()
            }
        } catch (e: Exception) {
            null
        }
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
     * Refresh telemetry data
     */
    fun refresh() {
        _selectedDeviceId.value?.let { deviceId ->
            loadTelemetryForDevice(deviceId)
        }
    }
}

/**
 * UI state for telemetry screen
 */
data class TelemetryUiState(
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val availableSensorTypes: List<String> = emptyList()
)

/**
 * Time range options for telemetry filtering
 */
enum class TimeRange(val displayName: String, val durationMs: Long) {
    LAST_HOUR("Last Hour", TimeUnit.HOURS.toMillis(1)),
    LAST_6_HOURS("Last 6 Hours", TimeUnit.HOURS.toMillis(6)),
    LAST_24_HOURS("Last 24 Hours", TimeUnit.HOURS.toMillis(24)),
    LAST_7_DAYS("Last 7 Days", TimeUnit.DAYS.toMillis(7)),
    LAST_30_DAYS("Last 30 Days", TimeUnit.DAYS.toMillis(30))
}

/**
 * Telemetry statistics
 */
data class TelemetryStatistics(
    val average: Double,
    val minimum: Double,
    val maximum: Double,
    val count: Int,
    val timeRange: TimeRange
)