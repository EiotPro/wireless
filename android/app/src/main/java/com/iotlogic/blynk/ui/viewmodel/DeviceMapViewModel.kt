package com.iotlogic.blynk.ui.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.iotlogic.blynk.domain.repository.DeviceRepository
import com.iotlogic.blynk.location.DeviceLocation
import com.iotlogic.blynk.location.GeofenceEvent
import com.iotlogic.blynk.location.LocationManager
import com.iotlogic.blynk.location.NearbyDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceMapViewModel @Inject constructor(
    private val locationManager: LocationManager,
    private val deviceRepository: DeviceRepository
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(DeviceMapUiState())
    val uiState: StateFlow<DeviceMapUiState> = _uiState.asStateFlow()
    
    // Current location from LocationManager
    val currentLocation: StateFlow<Location?> = locationManager.currentLocation
    
    // Device locations
    private val _deviceLocations = MutableStateFlow<List<DeviceLocation>>(emptyList())
    val deviceLocations: StateFlow<List<DeviceLocation>> = _deviceLocations.asStateFlow()
    
    // Nearby devices
    private val _nearbyDevices = MutableStateFlow<List<NearbyDevice>>(emptyList())
    val nearbyDevices: StateFlow<List<NearbyDevice>> = _nearbyDevices.asStateFlow()
    
    // Geofence events
    private val _geofenceEvents = MutableStateFlow<List<GeofenceEvent>>(emptyList())
    val geofenceEvents: StateFlow<List<GeofenceEvent>> = _geofenceEvents.asStateFlow()
    
    init {
        // Collect location updates
        viewModelScope.launch {
            locationManager.locationUpdates.collect { location ->
                updateNearbyDevices(location)
            }
        }
        
        // Collect geofence events
        viewModelScope.launch {
            locationManager.geofenceEvents.collect { event ->
                val currentEvents = _geofenceEvents.value.toMutableList()
                currentEvents.add(event)
                // Keep only last 10 events
                if (currentEvents.size > 10) {
                    currentEvents.removeAt(0)
                }
                _geofenceEvents.value = currentEvents
            }
        }
    }
    
    /**
     * Start location tracking
     */
    fun startLocationTracking() {
        if (!locationManager.hasLocationPermissions()) {
            _uiState.value = _uiState.value.copy(
                error = "Location permissions not granted"
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // Get last known location first
            val lastLocationResult = locationManager.getLastKnownLocation()
            lastLocationResult.getOrNull()?.let { location ->
                updateNearbyDevices(location)
            }
            
            // Start location tracking
            val result = locationManager.startLocationTracking()
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isTrackingLocation = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Failed to start location tracking"
                )
            }
        }
    }
    
    /**
     * Stop location tracking
     */
    fun stopLocationTracking() {
        viewModelScope.launch {
            locationManager.stopLocationTracking()
            _uiState.value = _uiState.value.copy(isTrackingLocation = false)
        }
    }
    
    /**
     * Refresh current location
     */
    fun refreshLocation() {
        viewModelScope.launch {
            val result = locationManager.getLastKnownLocation()
            result.getOrNull()?.let { location ->
                updateNearbyDevices(location)
            }
        }
    }
    
    /**
     * Load device locations from repository
     */
    fun loadDeviceLocations() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                deviceRepository.getDevices().collect { devices ->
                    val deviceLocations = devices.mapNotNull { device ->
                        // Extract location from device if available
                        if (device.latitude != null && device.longitude != null && 
                            device.latitude != 0.0 && device.longitude != 0.0) {
                            DeviceLocation(
                                deviceId = device.id,
                                deviceName = device.name,
                                latitude = device.latitude!!,
                                longitude = device.longitude!!,
                                lastUpdated = device.lastSeen
                            )
                        } else {
                            null
                        }
                    }
                    
                    _deviceLocations.value = deviceLocations
                    
                    // Add geofences for devices
                    addGeofencesForDevices(deviceLocations)
                    
                    // Update nearby devices if we have current location
                    currentLocation.value?.let { location ->
                        updateNearbyDevices(location)
                    }
                    
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load device locations"
                )
            }
        }
    }
    
    /**
     * Add geofences for all devices
     */
    private suspend fun addGeofencesForDevices(devices: List<DeviceLocation>) {
        devices.forEach { device ->
            locationManager.addDeviceGeofence(
                deviceId = device.deviceId,
                latitude = device.latitude,
                longitude = device.longitude,
                radius = 100f, // 100 meters
                deviceName = device.deviceName
            )
        }
    }
    
    /**
     * Add geofence at specific location
     */
    fun addGeofenceAtLocation(latLng: LatLng) {
        viewModelScope.launch {
            val geofenceId = "custom_${System.currentTimeMillis()}"
            locationManager.addDeviceGeofence(
                deviceId = geofenceId,
                latitude = latLng.latitude,
                longitude = latLng.longitude,
                radius = 50f, // 50 meters
                deviceName = "Custom Location"
            )
        }
    }
    
    /**
     * Update nearby devices based on current location
     */
    private suspend fun updateNearbyDevices(location: Location) {
        val deviceLocations = _deviceLocations.value
        if (deviceLocations.isNotEmpty()) {
            val nearbyDevices = locationManager.getNearbyDevices(
                currentLocation = location,
                deviceLocations = deviceLocations,
                maxDistance = 1000f // 1km
            )
            _nearbyDevices.value = nearbyDevices
        }
    }
    
    /**
     * Remove geofence
     */
    fun removeGeofence(geofenceId: String) {
        viewModelScope.launch {
            locationManager.removeGeofence(geofenceId)
        }
    }
    
    /**
     * Clear last geofence event
     */
    fun clearGeofenceEvent() {
        val currentEvents = _geofenceEvents.value.toMutableList()
        if (currentEvents.isNotEmpty()) {
            currentEvents.removeAt(currentEvents.size - 1)
            _geofenceEvents.value = currentEvents
        }
    }
    
    /**
     * Get device distance from current location
     */
    fun getDeviceDistance(deviceId: String): Float? {
        val currentLoc = currentLocation.value
        val device = _deviceLocations.value.find { it.deviceId == deviceId }
        
        return if (currentLoc != null && device != null) {
            locationManager.getDistance(
                currentLoc.latitude, currentLoc.longitude,
                device.latitude, device.longitude
            )
        } else {
            null
        }
    }
    
    /**
     * Check if device is within range
     */
    fun isDeviceInRange(deviceId: String, maxDistance: Float = 100f): Boolean {
        val distance = getDeviceDistance(deviceId)
        return distance != null && distance <= maxDistance
    }
    
    /**
     * Get active geofences
     */
    fun getActiveGeofences() = locationManager.getActiveGeofences()
    
    /**
     * Clear error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            stopLocationTracking()
        }
    }
}

/**
 * UI state for device map screen
 */
data class DeviceMapUiState(
    val isLoading: Boolean = false,
    val isTrackingLocation: Boolean = false,
    val error: String? = null
)