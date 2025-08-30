package com.iotlogic.blynk.notifications

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.iotlogic.blynk.data.local.preferences.AuthPreferences
import com.iotlogic.blynk.data.remote.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FCMTokenManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences
) {
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "fcm_prefs", Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_FCM_TOKEN = "fcm_token"
        private const val KEY_TOKEN_SENT_TO_SERVER = "token_sent_to_server"
        private const val KEY_LAST_UPDATE_TIME = "last_update_time"
    }
    
    /**
     * Initialize FCM and get token
     */
    fun initializeFCM() {
        scope.launch {
            try {
                // Get FCM token
                val token = FirebaseMessaging.getInstance().token.await()
                Log.d("FCM", "FCM Registration Token: $token")
                
                // Save token locally
                saveTokenLocally(token)
                
                // Send token to server if not sent or if token changed
                if (!isTokenSentToServer() || hasTokenChanged(token)) {
                    sendTokenToServer(token)
                }
                
                // Subscribe to topics
                subscribeToTopics()
                
            } catch (e: Exception) {
                Log.e("FCM", "Failed to get FCM token", e)
            }
        }
    }
    
    /**
     * Update FCM token
     */
    fun updateToken(token: String) {
        scope.launch {
            Log.d("FCM", "Updating FCM token: $token")
            
            // Save token locally
            saveTokenLocally(token)
            
            // Send token to server
            sendTokenToServer(token)
        }
    }
    
    /**
     * Send token to server
     */
    private suspend fun sendTokenToServer(token: String) {
        try {
            // Get user ID or device ID for registration
            val deviceId = getDeviceId()
            
            val response = apiService.registerFCMToken(
                token = token,
                deviceId = deviceId,
                platform = "android"
            )
            
            if (response.isSuccessful) {
                markTokenAsSent()
                Log.d("FCM", "Token sent to server successfully")
            } else {
                Log.e("FCM", "Failed to send token to server: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("FCM", "Error sending token to server", e)
        }
    }
    
    /**
     * Subscribe to FCM topics
     */
    private suspend fun subscribeToTopics() {
        try {
            val topics = listOf(
                "device_alerts",
                "system_updates",
                "general_notifications"
            )
            
            topics.forEach { topic ->
                FirebaseMessaging.getInstance().subscribeToTopic(topic).await()
                Log.d("FCM", "Subscribed to topic: $topic")
            }
            
            // Subscribe to user-specific topic if user is logged in
            val userId = getUserId()
            if (userId != null) {
                FirebaseMessaging.getInstance().subscribeToTopic("user_$userId").await()
                Log.d("FCM", "Subscribed to user topic: user_$userId")
            }
            
        } catch (e: Exception) {
            Log.e("FCM", "Error subscribing to topics", e)
        }
    }
    
    /**
     * Unsubscribe from FCM topics
     */
    suspend fun unsubscribeFromTopics() {
        try {
            val topics = listOf(
                "device_alerts",
                "system_updates",
                "general_notifications"
            )
            
            topics.forEach { topic ->
                FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).await()
                Log.d("FCM", "Unsubscribed from topic: $topic")
            }
            
            // Unsubscribe from user-specific topic
            val userId = getUserId()
            if (userId != null) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("user_$userId").await()
                Log.d("FCM", "Unsubscribed from user topic: user_$userId")
            }
            
        } catch (e: Exception) {
            Log.e("FCM", "Error unsubscribing from topics", e)
        }
    }
    
    /**
     * Subscribe to device-specific topic
     */
    suspend fun subscribeToDeviceTopic(deviceId: String) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("device_$deviceId").await()
            Log.d("FCM", "Subscribed to device topic: device_$deviceId")
        } catch (e: Exception) {
            Log.e("FCM", "Error subscribing to device topic", e)
        }
    }
    
    /**
     * Unsubscribe from device-specific topic
     */
    suspend fun unsubscribeFromDeviceTopic(deviceId: String) {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("device_$deviceId").await()
            Log.d("FCM", "Unsubscribed from device topic: device_$deviceId")
        } catch (e: Exception) {
            Log.e("FCM", "Error unsubscribing from device topic", e)
        }
    }
    
    /**
     * Subscribe to location-based topic for geofencing
     */
    suspend fun subscribeToLocationTopic(locationId: String) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("location_$locationId").await()
            Log.d("FCM", "Subscribed to location topic: location_$locationId")
        } catch (e: Exception) {
            Log.e("FCM", "Error subscribing to location topic", e)
        }
    }
    
    /**
     * Get current FCM token
     */
    suspend fun getCurrentToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e("FCM", "Error getting current token", e)
            null
        }
    }
    
    /**
     * Get saved token from local storage
     */
    fun getSavedToken(): String? {
        return sharedPreferences.getString(KEY_FCM_TOKEN, null)
    }
    
    /**
     * Delete FCM token
     */
    suspend fun deleteToken() {
        try {
            FirebaseMessaging.getInstance().deleteToken().await()
            
            // Clear local storage
            sharedPreferences.edit()
                .remove(KEY_FCM_TOKEN)
                .putBoolean(KEY_TOKEN_SENT_TO_SERVER, false)
                .apply()
                
            Log.d("FCM", "FCM token deleted")
        } catch (e: Exception) {
            Log.e("FCM", "Error deleting token", e)
        }
    }
    
    /**
     * Check if notifications are enabled for specific categories
     */
    fun areDeviceAlertsEnabled(): Boolean {
        return sharedPreferences.getBoolean("device_alerts_enabled", true)
    }
    
    fun areSystemUpdatesEnabled(): Boolean {
        return sharedPreferences.getBoolean("system_updates_enabled", true)
    }
    
    fun areGeofenceAlertsEnabled(): Boolean {
        return sharedPreferences.getBoolean("geofence_alerts_enabled", true)
    }
    
    fun areConnectionAlertsEnabled(): Boolean {
        return sharedPreferences.getBoolean("connection_alerts_enabled", false)
    }
    
    /**
     * Update notification preferences
     */
    fun updateNotificationPreferences(
        deviceAlerts: Boolean = areDeviceAlertsEnabled(),
        systemUpdates: Boolean = areSystemUpdatesEnabled(),
        geofenceAlerts: Boolean = areGeofenceAlertsEnabled(),
        connectionAlerts: Boolean = areConnectionAlertsEnabled()
    ) {
        sharedPreferences.edit()
            .putBoolean("device_alerts_enabled", deviceAlerts)
            .putBoolean("system_updates_enabled", systemUpdates)
            .putBoolean("geofence_alerts_enabled", geofenceAlerts)
            .putBoolean("connection_alerts_enabled", connectionAlerts)
            .apply()
        
        // TODO: Update server preferences - API method not implemented yet
        // scope.launch {
        //     try {
        //         // apiService.updateNotificationPreferences(
        //         //     deviceAlerts = deviceAlerts,
        //         //     systemUpdates = systemUpdates,
        //         //     geofenceAlerts = geofenceAlerts,
        //         //     connectionAlerts = connectionAlerts
        //         // )
        //     } catch (e: Exception) {
        //         Log.e("FCM", "Error updating notification preferences", e)
        //     }
        // }
    }
    
    /**
     * Save token locally
     */
    private fun saveTokenLocally(token: String) {
        sharedPreferences.edit()
            .putString(KEY_FCM_TOKEN, token)
            .putLong(KEY_LAST_UPDATE_TIME, System.currentTimeMillis())
            .apply()
    }
    
    /**
     * Check if token was sent to server
     */
    private fun isTokenSentToServer(): Boolean {
        return sharedPreferences.getBoolean(KEY_TOKEN_SENT_TO_SERVER, false)
    }
    
    /**
     * Mark token as sent to server
     */
    private fun markTokenAsSent() {
        sharedPreferences.edit()
            .putBoolean(KEY_TOKEN_SENT_TO_SERVER, true)
            .apply()
    }
    
    /**
     * Check if token has changed
     */
    private fun hasTokenChanged(newToken: String): Boolean {
        val savedToken = getSavedToken()
        return savedToken != newToken
    }
    
    /**
     * Get device ID for token registration
     */
    private fun getDeviceId(): String {
        // Get device ID from SharedPreferences or generate one
        val devicePrefs = context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE)
        val deviceId = devicePrefs.getString("device_id", null)
        
        return if (deviceId != null) {
            deviceId
        } else {
            val newDeviceId = "android_${System.currentTimeMillis()}"
            devicePrefs.edit().putString("device_id", newDeviceId).apply()
            newDeviceId
        }
    }
    
    /**
     * Get user ID if logged in
     */
    private fun getUserId(): String? {
        // Get user ID from auth preferences
        return try {
            authPreferences.getUserId()
        } catch (e: Exception) {
            null
        }
    }
}