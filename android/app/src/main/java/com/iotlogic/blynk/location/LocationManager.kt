package com.iotlogic.blynk.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val geofencingClient = LocationServices.getGeofencingClient(context)
    
    // Location updates
    private val _locationUpdates = MutableSharedFlow<Location>()
    val locationUpdates: SharedFlow<Location> = _locationUpdates.asSharedFlow()
    
    // Current location
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()
    
    // Location tracking state
    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()
    
    // Geofence events
    private val _geofenceEvents = Channel<GeofenceEvent>()
    val geofenceEvents: Flow<GeofenceEvent> = _geofenceEvents.receiveAsFlow()
    
    // Active geofences
    private val activeGeofences = mutableMapOf<String, GeofenceData>()
    
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    
    /**
     * Check if location permissions are granted
     */
    fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Get last known location
     */
    suspend fun getLastKnownLocation(): Result<Location?> {
        return withContext(Dispatchers.Main) {
            try {
                if (!hasLocationPermissions()) {
                    return@withContext Result.failure(Exception("Location permissions not granted"))
                }
                
                val location = fusedLocationClient.lastLocation.await()
                _currentLocation.value = location
                Result.success(location)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Start location tracking
     */
    suspend fun startLocationTracking(
        updateInterval: Long = 10000L, // 10 seconds
        fastestInterval: Long = 5000L,  // 5 seconds
        priority: Int = LocationRequest.PRIORITY_HIGH_ACCURACY
    ): Result<Unit> {
        return withContext(Dispatchers.Main) {
            try {
                if (!hasLocationPermissions()) {
                    return@withContext Result.failure(Exception("Location permissions not granted"))
                }
                
                if (_isTracking.value) {
                    return@withContext Result.success(Unit)
                }
                
                locationRequest = LocationRequest.create().apply {
                    interval = updateInterval
                    fastestInterval = fastestInterval
                    priority = priority
                }
                
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        locationResult.lastLocation?.let { location ->
                            _currentLocation.value = location
                            scope.launch {
                                _locationUpdates.emit(location)
                                checkGeofences(location)
                            }
                        }
                    }
                }
                
                fusedLocationClient.requestLocationUpdates(
                    locationRequest!!,
                    locationCallback!!,
                    Looper.getMainLooper()
                )
                
                _isTracking.value = true
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Stop location tracking
     */
    suspend fun stopLocationTracking() {
        withContext(Dispatchers.Main) {
            locationCallback?.let { callback ->
                fusedLocationClient.removeLocationUpdates(callback)
            }
            locationCallback = null
            locationRequest = null
            _isTracking.value = false
        }
    }
    
    /**
     * Add geofence around a device location
     */
    suspend fun addDeviceGeofence(
        deviceId: String,
        latitude: Double,
        longitude: Double,
        radius: Float = 100f, // meters
        deviceName: String = "Device"
    ): Result<Unit> {
        return withContext(Dispatchers.Main) {
            try {
                if (!hasLocationPermissions()) {
                    return@withContext Result.failure(Exception("Location permissions not granted"))
                }
                
                val geofenceData = GeofenceData(
                    id = deviceId,
                    name = deviceName,
                    latitude = latitude,
                    longitude = longitude,
                    radius = radius,
                    type = GeofenceType.DEVICE
                )
                
                val geofence = Geofence.Builder()
                    .setRequestId(deviceId)
                    .setCircularRegion(latitude, longitude, radius)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER or 
                        Geofence.GEOFENCE_TRANSITION_EXIT
                    )
                    .build()
                
                val geofencingRequest = GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(geofence)
                    .build()
                
                val pendingIntent = GeofenceBroadcastReceiver.getPendingIntent(context)
                
                geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                    .addOnSuccessListener {
                        activeGeofences[deviceId] = geofenceData
                    }
                    .addOnFailureListener { exception ->
                        throw exception
                    }
                    .await()
                
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Remove geofence
     */
    suspend fun removeGeofence(geofenceId: String): Result<Unit> {
        return withContext(Dispatchers.Main) {
            try {
                geofencingClient.removeGeofences(listOf(geofenceId))
                    .addOnSuccessListener {
                        activeGeofences.remove(geofenceId)
                    }
                    .await()
                
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Remove all geofences
     */
    suspend fun removeAllGeofences(): Result<Unit> {
        return withContext(Dispatchers.Main) {
            try {
                val pendingIntent = GeofenceBroadcastReceiver.getPendingIntent(context)
                geofencingClient.removeGeofences(pendingIntent)
                    .addOnSuccessListener {
                        activeGeofences.clear()
                    }
                    .await()
                
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Get distance between two locations
     */
    fun getDistance(
        lat1: Double, lng1: Double,
        lat2: Double, lng2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lng1, lat2, lng2, results)
        return results[0]
    }
    
    /**
     * Check if location is within geofence radius
     */
    fun isWithinGeofence(
        currentLat: Double, currentLng: Double,
        geofenceLat: Double, geofenceLng: Double,
        radius: Float
    ): Boolean {
        val distance = getDistance(currentLat, currentLng, geofenceLat, geofenceLng)
        return distance <= radius
    }
    
    /**
     * Get nearby devices based on current location
     */
    suspend fun getNearbyDevices(
        currentLocation: Location,
        deviceLocations: List<DeviceLocation>,
        maxDistance: Float = 1000f // meters
    ): List<NearbyDevice> {
        return withContext(Dispatchers.Default) {
            deviceLocations.mapNotNull { deviceLocation ->
                val distance = getDistance(
                    currentLocation.latitude, currentLocation.longitude,
                    deviceLocation.latitude, deviceLocation.longitude
                )
                
                if (distance <= maxDistance) {
                    NearbyDevice(
                        deviceId = deviceLocation.deviceId,
                        deviceName = deviceLocation.deviceName,
                        distance = distance,
                        location = LatLng(deviceLocation.latitude, deviceLocation.longitude)
                    )
                } else {
                    null
                }
            }.sortedBy { it.distance }
        }
    }
    
    /**
     * Check geofences manually (for when not using system geofencing)
     */
    private suspend fun checkGeofences(location: Location) {
        activeGeofences.values.forEach { geofence ->
            val isInside = isWithinGeofence(
                location.latitude, location.longitude,
                geofence.latitude, geofence.longitude,
                geofence.radius
            )
            
            // Simple geofence state tracking (in real app, would need more sophisticated state management)
            val event = if (isInside) {
                GeofenceEvent(
                    geofenceId = geofence.id,
                    geofenceName = geofence.name,
                    transition = GeofenceTransition.ENTER,
                    location = location,
                    timestamp = System.currentTimeMillis()
                )
            } else {
                GeofenceEvent(
                    geofenceId = geofence.id,
                    geofenceName = geofence.name,
                    transition = GeofenceTransition.EXIT,
                    location = location,
                    timestamp = System.currentTimeMillis()
                )
            }
            
            _geofenceEvents.trySend(event)
        }
    }
    
    /**
     * Process geofence event from broadcast receiver
     */
    fun processGeofenceEvent(
        geofenceId: String,
        transition: Int,
        location: Location?
    ) {
        val geofence = activeGeofences[geofenceId]
        if (geofence != null && location != null) {
            val geofenceTransition = when (transition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> GeofenceTransition.ENTER
                Geofence.GEOFENCE_TRANSITION_EXIT -> GeofenceTransition.EXIT
                else -> return
            }
            
            val event = GeofenceEvent(
                geofenceId = geofenceId,
                geofenceName = geofence.name,
                transition = geofenceTransition,
                location = location,
                timestamp = System.currentTimeMillis()
            )
            
            _geofenceEvents.trySend(event)
        }
    }
    
    /**
     * Get active geofences
     */
    fun getActiveGeofences(): List<GeofenceData> {
        return activeGeofences.values.toList()
    }
    
    /**
     * Clean up resources
     */
    fun cleanup() {
        scope.cancel()
        scope.launch {
            stopLocationTracking()
            removeAllGeofences()
        }
    }
}

/**
 * Extension function to await Google Play Services Task
 */
private suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T {
    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener { task ->
            if (task.exception != null) {
                cont.resumeWith(Result.failure(task.exception!!))
            } else {
                cont.resumeWith(Result.success(task.result))
            }
        }
    }
}

/**
 * Geofence data
 */
data class GeofenceData(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val type: GeofenceType
)

/**
 * Geofence types
 */
enum class GeofenceType {
    DEVICE,
    AREA,
    CUSTOM
}

/**
 * Geofence event
 */
data class GeofenceEvent(
    val geofenceId: String,
    val geofenceName: String,
    val transition: GeofenceTransition,
    val location: Location,
    val timestamp: Long
)

/**
 * Geofence transitions
 */
enum class GeofenceTransition {
    ENTER,
    EXIT
}

/**
 * Device location data
 */
data class DeviceLocation(
    val deviceId: String,
    val deviceName: String,
    val latitude: Double,
    val longitude: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Nearby device data
 */
data class NearbyDevice(
    val deviceId: String,
    val deviceName: String,
    val distance: Float,
    val location: LatLng
)