package com.iotlogic.blynk.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.RemoteMessage
import com.iotlogic.blynk.ui.main.MainActivity
import com.iotlogic.blynk.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val notificationManager = NotificationManagerCompat.from(context)
    
    // Notification events
    private val _notificationEvents = Channel<NotificationEvent>()
    val notificationEvents: Flow<NotificationEvent> = _notificationEvents.receiveAsFlow()
    
    companion object {
        const val CHANNEL_DEVICE_ALERTS = "device_alerts"
        const val CHANNEL_SYSTEM_UPDATES = "system_updates"
        const val CHANNEL_GEOFENCE_EVENTS = "geofence_events"
        const val CHANNEL_CONNECTION_STATUS = "connection_status"
        
        private const val NOTIFICATION_ID_DEVICE_ALERT = 1001
        private const val NOTIFICATION_ID_SYSTEM_UPDATE = 1002
        private const val NOTIFICATION_ID_GEOFENCE = 1003
        private const val NOTIFICATION_ID_CONNECTION = 1004
    }
    
    init {
        createNotificationChannels()
    }
    
    /**
     * Create notification channels for different types of notifications
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_DEVICE_ALERTS,
                    "Device Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alerts from IoT devices (sensor readings, malfunctions, etc.)"
                    enableVibration(true)
                    enableLights(true)
                },
                
                NotificationChannel(
                    CHANNEL_SYSTEM_UPDATES,
                    "System Updates",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "System status updates and maintenance notifications"
                },
                
                NotificationChannel(
                    CHANNEL_GEOFENCE_EVENTS,
                    "Location Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Geofence entry/exit notifications"
                    enableVibration(true)
                },
                
                NotificationChannel(
                    CHANNEL_CONNECTION_STATUS,
                    "Connection Status",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Device connection status changes"
                }
            )
            
            val systemNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            channels.forEach { channel ->
                systemNotificationManager.createNotificationChannel(channel)
            }
        }
    }
    
    /**
     * Handle FCM message
     */
    fun handleFCMMessage(remoteMessage: RemoteMessage) {
        scope.launch {
            val notificationType = remoteMessage.data["type"] ?: "general"
            val deviceId = remoteMessage.data["deviceId"]
            val severity = remoteMessage.data["severity"] ?: "info"
            
            val notification = when (notificationType) {
                "device_alert" -> createDeviceAlertNotification(remoteMessage)
                "system_update" -> createSystemUpdateNotification(remoteMessage)
                "geofence_event" -> createGeofenceNotification(remoteMessage)
                "connection_status" -> createConnectionStatusNotification(remoteMessage)
                else -> createGeneralNotification(remoteMessage)
            }
            
            notification?.let { 
                showNotification(it)
                
                // Emit notification event
                _notificationEvents.send(
                    NotificationEvent(
                        id = it.id,
                        type = notificationType,
                        deviceId = deviceId,
                        title = it.title,
                        message = it.message,
                        severity = severity,
                        timestamp = System.currentTimeMillis(),
                        data = remoteMessage.data
                    )
                )
            }
        }
    }
    
    /**
     * Create device alert notification
     */
    private fun createDeviceAlertNotification(remoteMessage: RemoteMessage): IoTNotification? {
        val deviceId = remoteMessage.data["deviceId"] ?: return null
        val deviceName = remoteMessage.data["deviceName"] ?: "Unknown Device"
        val alertType = remoteMessage.data["alertType"] ?: "alert"
        val severity = remoteMessage.data["severity"] ?: "info"
        
        val title = remoteMessage.notification?.title ?: "$deviceName Alert"
        val message = remoteMessage.notification?.body ?: "Device alert received"
        
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("deviceId", deviceId)
            putExtra("openDevice", true)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            deviceId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val priority = when (severity) {
            "critical" -> NotificationCompat.PRIORITY_MAX
            "high" -> NotificationCompat.PRIORITY_HIGH
            "medium" -> NotificationCompat.PRIORITY_DEFAULT
            else -> NotificationCompat.PRIORITY_LOW
        }
        
        return IoTNotification(
            id = NOTIFICATION_ID_DEVICE_ALERT + deviceId.hashCode(),
            channelId = CHANNEL_DEVICE_ALERTS,
            title = title,
            message = message,
            priority = priority,
            pendingIntent = pendingIntent,
            autoCancel = true,
            icon = R.drawable.ic_notification,
            color = when (severity) {
                "critical" -> android.graphics.Color.RED
                "high" -> android.graphics.Color.YELLOW
                else -> null
            }
        )
    }
    
    /**
     * Create system update notification
     */
    private fun createSystemUpdateNotification(remoteMessage: RemoteMessage): IoTNotification {
        val title = remoteMessage.notification?.title ?: "System Update"
        val message = remoteMessage.notification?.body ?: "System update available"
        
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_SYSTEM_UPDATE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return IoTNotification(
            id = NOTIFICATION_ID_SYSTEM_UPDATE,
            channelId = CHANNEL_SYSTEM_UPDATES,
            title = title,
            message = message,
            priority = NotificationCompat.PRIORITY_DEFAULT,
            pendingIntent = pendingIntent,
            autoCancel = true,
            icon = R.drawable.ic_notification
        )
    }
    
    /**
     * Create geofence notification
     */
    private fun createGeofenceNotification(remoteMessage: RemoteMessage): IoTNotification? {
        val deviceId = remoteMessage.data["deviceId"] ?: return null
        val deviceName = remoteMessage.data["deviceName"] ?: "Device"
        val eventType = remoteMessage.data["eventType"] ?: "enter"
        
        val title = when (eventType) {
            "enter" -> "Entered $deviceName area"
            "exit" -> "Left $deviceName area"
            else -> "Geofence event for $deviceName"
        }
        
        val message = remoteMessage.notification?.body ?: "Location-based event occurred"
        
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("deviceId", deviceId)
            putExtra("openMap", true)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_GEOFENCE + deviceId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return IoTNotification(
            id = NOTIFICATION_ID_GEOFENCE + deviceId.hashCode(),
            channelId = CHANNEL_GEOFENCE_EVENTS,
            title = title,
            message = message,
            priority = NotificationCompat.PRIORITY_DEFAULT,
            pendingIntent = pendingIntent,
            autoCancel = true,
            icon = R.drawable.ic_notification
        )
    }
    
    /**
     * Create connection status notification
     */
    private fun createConnectionStatusNotification(remoteMessage: RemoteMessage): IoTNotification? {
        val deviceId = remoteMessage.data["deviceId"] ?: return null
        val deviceName = remoteMessage.data["deviceName"] ?: "Device"
        val status = remoteMessage.data["status"] ?: "unknown"
        
        val title = when (status) {
            "connected" -> "$deviceName Connected"
            "disconnected" -> "$deviceName Disconnected"
            "reconnected" -> "$deviceName Reconnected"
            else -> "$deviceName Status Changed"
        }
        
        val message = remoteMessage.notification?.body ?: "Device connection status changed"
        
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("deviceId", deviceId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_CONNECTION + deviceId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return IoTNotification(
            id = NOTIFICATION_ID_CONNECTION + deviceId.hashCode(),
            channelId = CHANNEL_CONNECTION_STATUS,
            title = title,
            message = message,
            priority = NotificationCompat.PRIORITY_LOW,
            pendingIntent = pendingIntent,
            autoCancel = true,
            icon = R.drawable.ic_notification
        )
    }
    
    /**
     * Create general notification
     */
    private fun createGeneralNotification(remoteMessage: RemoteMessage): IoTNotification {
        val title = remoteMessage.notification?.title ?: "IoT Logic"
        val message = remoteMessage.notification?.body ?: "New notification"
        
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return IoTNotification(
            id = System.currentTimeMillis().toInt(),
            channelId = CHANNEL_SYSTEM_UPDATES,
            title = title,
            message = message,
            priority = NotificationCompat.PRIORITY_DEFAULT,
            pendingIntent = pendingIntent,
            autoCancel = true,
            icon = R.drawable.ic_notification
        )
    }
    
    /**
     * Show local notification for device alerts
     */
    fun showDeviceAlert(
        deviceId: String,
        deviceName: String,
        alertType: String,
        message: String,
        severity: String = "medium"
    ) {
        val title = "$deviceName Alert"
        
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("deviceId", deviceId)
            putExtra("openDevice", true)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            deviceId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val priority = when (severity) {
            "critical" -> NotificationCompat.PRIORITY_MAX
            "high" -> NotificationCompat.PRIORITY_HIGH
            "medium" -> NotificationCompat.PRIORITY_DEFAULT
            else -> NotificationCompat.PRIORITY_LOW
        }
        
        val notification = IoTNotification(
            id = NOTIFICATION_ID_DEVICE_ALERT + deviceId.hashCode(),
            channelId = CHANNEL_DEVICE_ALERTS,
            title = title,
            message = message,
            priority = priority,
            pendingIntent = pendingIntent,
            autoCancel = true,
            icon = R.drawable.ic_notification,
            color = when (severity) {
                "critical" -> android.graphics.Color.RED
                "high" -> android.graphics.Color.YELLOW
                else -> null
            }
        )
        
        showNotification(notification)
        
        scope.launch {
            _notificationEvents.send(
                NotificationEvent(
                    id = notification.id,
                    type = "device_alert",
                    deviceId = deviceId,
                    title = title,
                    message = message,
                    severity = severity,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
    
    /**
     * Show geofence event notification
     */
    fun showGeofenceEvent(
        deviceId: String,
        deviceName: String,
        eventType: String, // "enter" or "exit"
        location: String? = null
    ) {
        val title = when (eventType) {
            "enter" -> "Entered $deviceName area"
            "exit" -> "Left $deviceName area"
            else -> "Geofence event for $deviceName"
        }
        
        val message = location?.let { "Location: $it" } ?: "Geofence event occurred"
        
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("deviceId", deviceId)
            putExtra("openMap", true)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_GEOFENCE + deviceId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = IoTNotification(
            id = NOTIFICATION_ID_GEOFENCE + deviceId.hashCode(),
            channelId = CHANNEL_GEOFENCE_EVENTS,
            title = title,
            message = message,
            priority = NotificationCompat.PRIORITY_DEFAULT,
            pendingIntent = pendingIntent,
            autoCancel = true,
            icon = R.drawable.ic_notification
        )
        
        showNotification(notification)
        
        scope.launch {
            _notificationEvents.send(
                NotificationEvent(
                    id = notification.id,
                    type = "geofence_event",
                    deviceId = deviceId,
                    title = title,
                    message = message,
                    severity = "info",
                    timestamp = System.currentTimeMillis(),
                    data = mapOf("eventType" to eventType, "location" to (location ?: ""))
                )
            )
        }
    }
    
    /**
     * Show connection status notification
     */
    fun showConnectionStatus(
        deviceId: String,
        deviceName: String,
        isConnected: Boolean
    ) {
        val title = if (isConnected) "$deviceName Connected" else "$deviceName Disconnected"
        val message = if (isConnected) "Device is now online" else "Device is now offline"
        
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("deviceId", deviceId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_CONNECTION + deviceId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = IoTNotification(
            id = NOTIFICATION_ID_CONNECTION + deviceId.hashCode(),
            channelId = CHANNEL_CONNECTION_STATUS,
            title = title,
            message = message,
            priority = NotificationCompat.PRIORITY_LOW,
            pendingIntent = pendingIntent,
            autoCancel = true,
            icon = R.drawable.ic_notification
        )
        
        showNotification(notification)
        
        scope.launch {
            _notificationEvents.send(
                NotificationEvent(
                    id = notification.id,
                    type = "connection_status",
                    deviceId = deviceId,
                    title = title,
                    message = message,
                    severity = "info",
                    timestamp = System.currentTimeMillis(),
                    data = mapOf("isConnected" to isConnected.toString())
                )
            )
        }
    }
    
    /**
     * Show notification
     */
    private fun showNotification(notification: IoTNotification) {
        try {
            val builder = NotificationCompat.Builder(context, notification.channelId)
                .setContentTitle(notification.title)
                .setContentText(notification.message)
                .setSmallIcon(notification.icon)
                .setPriority(notification.priority)
                .setAutoCancel(notification.autoCancel)
                .setContentIntent(notification.pendingIntent)
            
            notification.color?.let { color ->
                builder.setColor(color)
            }
            
            if (notification.message.length > 50) {
                builder.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(notification.message)
                )
            }
            
            notificationManager.notify(notification.id, builder.build())
        } catch (e: SecurityException) {
            // Handle case where notification permissions are not granted
        }
    }
    
    /**
     * Cancel notification
     */
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
    
    /**
     * Cancel all notifications
     */
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
    
    /**
     * Check if notifications are enabled
     */
    fun areNotificationsEnabled(): Boolean {
        return notificationManager.areNotificationsEnabled()
    }
}

/**
 * IoT notification data class
 */
data class IoTNotification(
    val id: Int,
    val channelId: String,
    val title: String,
    val message: String,
    val priority: Int = NotificationCompat.PRIORITY_DEFAULT,
    val pendingIntent: PendingIntent? = null,
    val autoCancel: Boolean = true,
    val icon: Int = R.drawable.ic_notification,
    val color: Int? = null
)

/**
 * Notification event data class
 */
data class NotificationEvent(
    val id: Int,
    val type: String,
    val deviceId: String?,
    val title: String,
    val message: String,
    val severity: String,
    val timestamp: Long,
    val data: Map<String, String> = emptyMap()
)