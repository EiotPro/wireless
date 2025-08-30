package com.iotlogic.blynk.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iotlogic.blynk.notifications.FCMTokenManager
import com.iotlogic.blynk.notifications.AppNotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: AppNotificationManager,
    private val fcmTokenManager: FCMTokenManager
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()
    
    /**
     * Load notification settings
     */
    fun loadNotificationSettings() {
        viewModelScope.launch {
            val areSystemNotificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
            val fcmToken = fcmTokenManager.getSavedToken()
            
            _uiState.value = _uiState.value.copy(
                areSystemNotificationsEnabled = areSystemNotificationsEnabled,
                deviceAlertsEnabled = fcmTokenManager.areDeviceAlertsEnabled(),
                systemUpdatesEnabled = fcmTokenManager.areSystemUpdatesEnabled(),
                geofenceAlertsEnabled = fcmTokenManager.areGeofenceAlertsEnabled(),
                connectionAlertsEnabled = fcmTokenManager.areConnectionAlertsEnabled(),
                fcmToken = fcmToken,
                isTokenRegistered = fcmToken != null,
                isLoading = false
            )
        }
    }
    
    /**
     * Toggle device alerts
     */
    fun toggleDeviceAlerts(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(deviceAlertsEnabled = enabled)
        updateNotificationPreferences()
    }
    
    /**
     * Toggle system updates
     */
    fun toggleSystemUpdates(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(systemUpdatesEnabled = enabled)
        updateNotificationPreferences()
    }
    
    /**
     * Toggle geofence alerts
     */
    fun toggleGeofenceAlerts(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(geofenceAlertsEnabled = enabled)
        updateNotificationPreferences()
    }
    
    /**
     * Toggle connection alerts
     */
    fun toggleConnectionAlerts(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(connectionAlertsEnabled = enabled)
        updateNotificationPreferences()
    }
    
    /**
     * Toggle quiet hours
     */
    fun toggleQuietHours(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(quietHoursEnabled = enabled)
        // Save quiet hours setting to preferences
        saveQuietHoursSettings()
    }
    
    /**
     * Set quiet hours start time
     */
    fun setQuietHoursStart(time: String) {
        _uiState.value = _uiState.value.copy(quietHoursStart = time)
        saveQuietHoursSettings()
    }
    
    /**
     * Set quiet hours end time
     */
    fun setQuietHoursEnd(time: String) {
        _uiState.value = _uiState.value.copy(quietHoursEnd = time)
        saveQuietHoursSettings()
    }
    
    /**
     * Set minimum priority
     */
    fun setMinimumPriority(priority: String) {
        _uiState.value = _uiState.value.copy(minimumPriority = priority)
        savePrioritySettings()
    }
    
    /**
     * Open system notification settings
     */
    fun openSystemNotificationSettings() {
        try {
            val intent = Intent().apply {
                when {
                    android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O -> {
                        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    else -> {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = android.net.Uri.parse("package:${context.packageName}")
                    }
                }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to general settings
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
    
    /**
     * Clear notification history
     */
    fun clearNotificationHistory() {
        viewModelScope.launch {
            notificationManager.cancelAllNotifications()
            // Clear any stored notification history
            clearNotificationHistoryFromStorage()
        }
    }
    
    /**
     * Send test notification
     */
    fun sendTestNotification() {
        notificationManager.showDeviceAlert(
            deviceId = "test_device",
            deviceName = "Test Device",
            alertType = "test",
            message = "This is a test notification to verify that notifications are working correctly.",
            severity = "medium"
        )
    }
    
    /**
     * Reset settings to defaults
     */
    fun resetToDefaults() {
        _uiState.value = _uiState.value.copy(
            deviceAlertsEnabled = true,
            systemUpdatesEnabled = true,
            geofenceAlertsEnabled = true,
            connectionAlertsEnabled = false,
            quietHoursEnabled = false,
            quietHoursStart = "22:00",
            quietHoursEnd = "07:00",
            minimumPriority = "All"
        )
        
        updateNotificationPreferences()
        saveQuietHoursSettings()
        savePrioritySettings()
    }
    
    /**
     * Refresh FCM token
     */
    fun refreshFCMToken() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshingToken = true)
            
            try {
                // Get new token
                val newToken = fcmTokenManager.getCurrentToken()
                
                if (newToken != null) {
                    // Update token
                    fcmTokenManager.updateToken(newToken)
                    
                    _uiState.value = _uiState.value.copy(
                        fcmToken = newToken,
                        isTokenRegistered = true,
                        isRefreshingToken = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isRefreshingToken = false,
                        error = "Failed to get FCM token"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRefreshingToken = false,
                    error = e.message ?: "Failed to refresh token"
                )
            }
        }
    }
    
    /**
     * Update notification preferences
     */
    private fun updateNotificationPreferences() {
        val state = _uiState.value
        fcmTokenManager.updateNotificationPreferences(
            deviceAlerts = state.deviceAlertsEnabled,
            systemUpdates = state.systemUpdatesEnabled,
            geofenceAlerts = state.geofenceAlertsEnabled,
            connectionAlerts = state.connectionAlertsEnabled
        )
    }
    
    /**
     * Save quiet hours settings
     */
    private fun saveQuietHoursSettings() {
        val state = _uiState.value
        val prefs = context.getSharedPreferences("notification_settings", Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean("quiet_hours_enabled", state.quietHoursEnabled)
            .putString("quiet_hours_start", state.quietHoursStart)
            .putString("quiet_hours_end", state.quietHoursEnd)
            .apply()
    }
    
    /**
     * Save priority settings
     */
    private fun savePrioritySettings() {
        val state = _uiState.value
        val prefs = context.getSharedPreferences("notification_settings", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("minimum_priority", state.minimumPriority)
            .apply()
    }
    
    /**
     * Load quiet hours settings
     */
    private fun loadQuietHoursSettings() {
        val prefs = context.getSharedPreferences("notification_settings", Context.MODE_PRIVATE)
        val quietHoursEnabled = prefs.getBoolean("quiet_hours_enabled", false)
        val quietHoursStart = prefs.getString("quiet_hours_start", "22:00") ?: "22:00"
        val quietHoursEnd = prefs.getString("quiet_hours_end", "07:00") ?: "07:00"
        val minimumPriority = prefs.getString("minimum_priority", "All") ?: "All"
        
        _uiState.value = _uiState.value.copy(
            quietHoursEnabled = quietHoursEnabled,
            quietHoursStart = quietHoursStart,
            quietHoursEnd = quietHoursEnd,
            minimumPriority = minimumPriority
        )
    }
    
    /**
     * Clear notification history from storage
     */
    private fun clearNotificationHistoryFromStorage() {
        val prefs = context.getSharedPreferences("notification_history", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
    
    /**
     * Check if current time is within quiet hours
     */
    fun isQuietHours(): Boolean {
        val state = _uiState.value
        if (!state.quietHoursEnabled) return false
        
        // Implementation would check current time against quiet hours
        // This is a simplified version
        return false
    }
    
    /**
     * Check if notification should be shown based on priority filter
     */
    fun shouldShowNotification(notificationPriority: String): Boolean {
        val state = _uiState.value
        val minimumPriority = state.minimumPriority
        
        if (minimumPriority == "All") return true
        
        val priorities = listOf("Low", "Medium", "High", "Critical")
        val minimumIndex = priorities.indexOf(minimumPriority)
        val notificationIndex = priorities.indexOf(notificationPriority)
        
        return if (minimumIndex >= 0 && notificationIndex >= 0) {
            notificationIndex >= minimumIndex
        } else {
            true
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    init {
        loadQuietHoursSettings()
    }
}

/**
 * UI state for notification settings screen
 */
data class NotificationSettingsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val areSystemNotificationsEnabled: Boolean = false,
    val deviceAlertsEnabled: Boolean = true,
    val systemUpdatesEnabled: Boolean = true,
    val geofenceAlertsEnabled: Boolean = true,
    val connectionAlertsEnabled: Boolean = false,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "07:00",
    val minimumPriority: String = "All",
    val fcmToken: String? = null,
    val isTokenRegistered: Boolean = false,
    val isRefreshingToken: Boolean = false
)