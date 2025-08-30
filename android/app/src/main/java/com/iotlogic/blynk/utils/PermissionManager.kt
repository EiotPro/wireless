package com.iotlogic.blynk.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    /**
     * All permissions required for IoT hardware integration
     */
    object Permissions {
        // Bluetooth permissions
        val BLUETOOTH_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }
        
        // Location permissions (required for BLE scanning and WiFi)
        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        
        // Background location permission
        val BACKGROUND_LOCATION_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            emptyArray()
        }
        
        // WiFi permissions
        val WIFI_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE
        )
        
        // Camera permission for QR scanning
        val CAMERA_PERMISSION = arrayOf(
            Manifest.permission.CAMERA
        )
        
        // Storage permissions
        val STORAGE_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        
        // Foreground service permissions
        val FOREGROUND_SERVICE_PERMISSIONS = arrayOf(
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE
        )
        
        // Notification permissions
        val NOTIFICATION_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyArray()
        }
        
        // Other permissions
        val OTHER_PERMISSIONS = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.VIBRATE
        )
    }
    
    /**
     * Permission groups for different features
     */
    enum class PermissionGroup(val permissions: Array<String>, val description: String) {
        BLUETOOTH(
            Permissions.BLUETOOTH_PERMISSIONS,
            "Required for Bluetooth LE device scanning and communication"
        ),
        LOCATION(
            Permissions.LOCATION_PERMISSIONS,
            "Required for device location tracking and Bluetooth LE scanning"
        ),
        BACKGROUND_LOCATION(
            Permissions.BACKGROUND_LOCATION_PERMISSION,
            "Required for location tracking when app is in background"
        ),
        WIFI(
            Permissions.WIFI_PERMISSIONS,
            "Required for WiFi network management and device discovery"
        ),
        CAMERA(
            Permissions.CAMERA_PERMISSION,
            "Required for QR code scanning for device setup"
        ),
        STORAGE(
            Permissions.STORAGE_PERMISSIONS,
            "Required for storing device data and logs"
        ),
        FOREGROUND_SERVICE(
            Permissions.FOREGROUND_SERVICE_PERMISSIONS,
            "Required for maintaining device connections in background"
        ),
        NOTIFICATIONS(
            Permissions.NOTIFICATION_PERMISSIONS,
            "Required for device alerts and notifications"
        )
    }
    
    /**
     * Check if a specific permission is granted
     */
    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Check if all permissions in a group are granted
     */
    fun isPermissionGroupGranted(group: PermissionGroup): Boolean {
        return group.permissions.all { permission ->
            isPermissionGranted(permission)
        }
    }
    
    /**
     * Get missing permissions from a group
     */
    fun getMissingPermissions(group: PermissionGroup): List<String> {
        return group.permissions.filter { permission ->
            !isPermissionGranted(permission)
        }
    }
    
    /**
     * Get all missing permissions across groups
     */
    fun getAllMissingPermissions(groups: List<PermissionGroup>): List<String> {
        return groups.flatMap { group ->
            getMissingPermissions(group)
        }.distinct()
    }
    
    /**
     * Check Bluetooth permissions specifically
     */
    fun hasBluetoothPermissions(): Boolean {
        return isPermissionGroupGranted(PermissionGroup.BLUETOOTH) &&
                isPermissionGroupGranted(PermissionGroup.LOCATION)
    }
    
    /**
     * Check WiFi permissions
     */
    fun hasWiFiPermissions(): Boolean {
        return isPermissionGroupGranted(PermissionGroup.WIFI) &&
                isPermissionGroupGranted(PermissionGroup.LOCATION)
    }
    
    /**
     * Check camera permissions
     */
    fun hasCameraPermissions(): Boolean {
        return isPermissionGroupGranted(PermissionGroup.CAMERA)
    }
    
    /**
     * Check location permissions
     */
    fun hasLocationPermissions(): Boolean {
        return isPermissionGroupGranted(PermissionGroup.LOCATION)
    }
    
    /**
     * Check if background location is granted
     */
    fun hasBackgroundLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isPermissionGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            true // Not required on older versions
        }
    }
    
    /**
     * Check notification permissions
     */
    fun hasNotificationPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true // Not required on older versions
        }
    }
    
    /**
     * Get all permissions required for IoT functionality
     */
    fun getAllRequiredPermissions(): Array<String> {
        val allPermissions = mutableSetOf<String>()
        
        // Add all permission groups
        allPermissions.addAll(Permissions.BLUETOOTH_PERMISSIONS)
        allPermissions.addAll(Permissions.LOCATION_PERMISSIONS)
        allPermissions.addAll(Permissions.WIFI_PERMISSIONS)
        allPermissions.addAll(Permissions.CAMERA_PERMISSION)
        allPermissions.addAll(Permissions.STORAGE_PERMISSIONS)
        allPermissions.addAll(Permissions.FOREGROUND_SERVICE_PERMISSIONS)
        allPermissions.addAll(Permissions.NOTIFICATION_PERMISSIONS)
        allPermissions.addAll(Permissions.OTHER_PERMISSIONS)
        
        return allPermissions.toTypedArray()
    }
    
    /**
     * Get essential permissions (minimum required for basic functionality)
     */
    fun getEssentialPermissions(): Array<String> {
        val essentialPermissions = mutableSetOf<String>()
        
        essentialPermissions.addAll(Permissions.BLUETOOTH_PERMISSIONS)
        essentialPermissions.addAll(Permissions.LOCATION_PERMISSIONS)
        essentialPermissions.addAll(Permissions.WIFI_PERMISSIONS)
        essentialPermissions.addAll(Permissions.OTHER_PERMISSIONS)
        
        return essentialPermissions.toTypedArray()
    }
    
    /**
     * Get optional permissions (nice to have but not essential)
     */
    fun getOptionalPermissions(): Array<String> {
        val optionalPermissions = mutableSetOf<String>()
        
        optionalPermissions.addAll(Permissions.CAMERA_PERMISSION)
        optionalPermissions.addAll(Permissions.STORAGE_PERMISSIONS)
        optionalPermissions.addAll(Permissions.NOTIFICATION_PERMISSIONS)
        optionalPermissions.addAll(Permissions.BACKGROUND_LOCATION_PERMISSION)
        
        return optionalPermissions.toTypedArray()
    }
    
    /**
     * Check if all essential permissions are granted
     */
    fun hasEssentialPermissions(): Boolean {
        return getEssentialPermissions().all { permission ->
            isPermissionGranted(permission)
        }
    }
    
    /**
     * Get permission group for a specific permission
     */
    fun getPermissionGroup(permission: String): PermissionGroup? {
        return PermissionGroup.values().find { group ->
            permission in group.permissions
        }
    }
    
    /**
     * Get user-friendly description for permission
     */
    fun getPermissionDescription(permission: String): String {
        return when (permission) {
            Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH -> 
                "Access Bluetooth to discover and connect to IoT devices"
            Manifest.permission.BLUETOOTH_CONNECT -> 
                "Connect to Bluetooth devices for data exchange"
            Manifest.permission.BLUETOOTH_ADVERTISE -> 
                "Make device discoverable to other Bluetooth devices"
            Manifest.permission.BLUETOOTH_ADMIN -> 
                "Manage Bluetooth connections and settings"
            Manifest.permission.ACCESS_FINE_LOCATION -> 
                "Access precise location for device positioning and Bluetooth scanning"
            Manifest.permission.ACCESS_COARSE_LOCATION -> 
                "Access approximate location for device discovery"
            Manifest.permission.ACCESS_BACKGROUND_LOCATION -> 
                "Access location in background for continuous device monitoring"
            Manifest.permission.ACCESS_WIFI_STATE -> 
                "View WiFi connection information"
            Manifest.permission.CHANGE_WIFI_STATE -> 
                "Change WiFi connection state to connect to device networks"
            Manifest.permission.CAMERA -> 
                "Access camera to scan QR codes for device setup"
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES -> 
                "Read files to access device configuration and data"
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> 
                "Write files to save device logs and configuration"
            Manifest.permission.POST_NOTIFICATIONS -> 
                "Show notifications for device alerts and status updates"
            Manifest.permission.FOREGROUND_SERVICE -> 
                "Run background services to maintain device connections"
            else -> "Required for IoT device functionality"
        }
    }
    
    /**
     * Check if permission is dangerous (requires runtime request)
     */
    fun isDangerousPermission(permission: String): Boolean {
        return when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS -> true
            else -> when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && permission in arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_ADVERTISE
                ) -> true
                else -> false
            }
        }
    }
    
    /**
     * Get dangerous permissions that need runtime request
     */
    fun getDangerousPermissions(): Array<String> {
        return getAllRequiredPermissions().filter { permission ->
            isDangerousPermission(permission)
        }.toTypedArray()
    }
}