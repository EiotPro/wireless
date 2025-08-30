package com.iotlogic.blynk.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.iotlogic.blynk.data.local.preferences.NotificationPreferences
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppNotificationManagerTest {

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var systemNotificationManager: NotificationManager

    @MockK
    private lateinit var notificationManagerCompat: NotificationManagerCompat

    @MockK
    private lateinit var firebaseMessaging: FirebaseMessaging

    @MockK
    private lateinit var notificationPreferences: NotificationPreferences

    @MockK
    private lateinit var fcmTokenManager: FCMTokenManager

    private lateinit var notificationManager: AppNotificationManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        
        // Mock context
        every { context.getSystemService(Context.NOTIFICATION_SERVICE) } returns systemNotificationManager
        every { context.packageName } returns "com.iotlogic.blynk"
        every { context.getString(any()) } returns "Test String"
        every { context.getString(any(), any()) } returns "Test String"

        // Mock notification manager
        mockkStatic(NotificationManagerCompat::class)
        every { NotificationManagerCompat.from(context) } returns notificationManagerCompat
        every { notificationManagerCompat.areNotificationsEnabled() } returns true

        notificationManager = AppNotificationManager(
            context = context
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `showDeviceAlert should create and display notification`() = runTest {
        // Given
        val deviceId = "device123"
        val deviceName = "Test Device"
        val alertType = "temperature"
        val message = "Temperature alert"
        val severity = "high"

        every { notificationManagerCompat.notify(any(), any()) } just Runs
        every { notificationPreferences.areDeviceAlertsEnabled() } returns true
        every { notificationPreferences.getMinimumPriority() } returns "low"

        // When
        notificationManager.showDeviceAlert(deviceId, deviceName, alertType, message, severity)

        // Then
        verify { notificationManagerCompat.notify(any(), any()) }
    }

    @Test
    fun `showDeviceAlert should not display when disabled`() = runTest {
        // Given
        val deviceId = "device123"
        val deviceName = "Test Device"
        val alertType = "temperature"
        val message = "Temperature alert"
        val severity = "low"

        every { notificationPreferences.areDeviceAlertsEnabled() } returns false

        // When
        notificationManager.showDeviceAlert(deviceId, deviceName, alertType, message, severity)

        // Then
        verify(exactly = 0) { notificationManagerCompat.notify(any(), any()) }
    }

    @Test
    fun `showDeviceAlert should not display when below minimum priority`() = runTest {
        // Given
        val deviceId = "device123"
        val deviceName = "Test Device"
        val alertType = "temperature"
        val message = "Temperature alert"
        val severity = "low"

        every { notificationPreferences.areDeviceAlertsEnabled() } returns true
        every { notificationPreferences.getMinimumPriority() } returns "high"

        // When
        notificationManager.showDeviceAlert(deviceId, deviceName, alertType, message, severity)

        // Then
        verify(exactly = 0) { notificationManagerCompat.notify(any(), any()) }
    }

    @Test
    fun `showSystemUpdate should create and display notification`() = runTest {
        // Given
        val title = "System Update"
        val message = "New update available"
        val version = "1.2.0"
        val isRequired = true

        every { notificationManagerCompat.notify(any(), any()) } just Runs
        every { notificationPreferences.areSystemUpdatesEnabled() } returns true

        // When
        notificationManager.showSystemUpdate(title, message, version, isRequired)

        // Then
        verify { notificationManagerCompat.notify(any(), any()) }
    }

    @Test
    fun `showGeofenceAlert should create and display notification`() = runTest {
        // Given
        val deviceId = "device123"
        val deviceName = "Test Device"
        val geofenceName = "Home"
        val eventType = "ENTER"
        val latitude = 40.7128
        val longitude = -74.0060

        every { notificationManagerCompat.notify(any(), any()) } just Runs
        every { notificationPreferences.areGeofenceAlertsEnabled() } returns true

        // When
        notificationManager.showGeofenceAlert(deviceId, deviceName, geofenceName, eventType, latitude, longitude)

        // Then
        verify { notificationManagerCompat.notify(any(), any()) }
    }

    @Test
    fun `showConnectionAlert should create and display notification`() = runTest {
        // Given
        val deviceId = "device123"
        val deviceName = "Test Device"
        val connectionStatus = "DISCONNECTED"
        val lastSeen = System.currentTimeMillis()

        every { notificationManagerCompat.notify(any(), any()) } just Runs
        every { notificationPreferences.areConnectionAlertsEnabled() } returns true

        // When
        notificationManager.showConnectionAlert(deviceId, deviceName, connectionStatus, lastSeen)

        // Then
        verify { notificationManagerCompat.notify(any(), any()) }
    }

    @Test
    fun `cancelNotification should cancel specific notification`() = runTest {
        // Given
        val notificationId = 12345

        every { notificationManagerCompat.cancel(any()) } just Runs

        // When
        notificationManager.cancelNotification(notificationId)

        // Then
        verify { notificationManagerCompat.cancel(notificationId) }
    }

    @Test
    fun `cancelDeviceNotifications should cancel all device notifications`() = runTest {
        // Given
        val deviceId = "device123"

        every { notificationManagerCompat.cancel(any()) } just Runs

        // When
        notificationManager.cancelDeviceNotifications(deviceId)

        // Then
        verify(atLeast = 1) { notificationManagerCompat.cancel(any()) }
    }

    @Test
    fun `cancelAllNotifications should cancel all notifications`() = runTest {
        // Given
        every { notificationManagerCompat.cancelAll() } just Runs

        // When
        notificationManager.cancelAllNotifications()

        // Then
        verify { notificationManagerCompat.cancelAll() }
    }

    @Test
    fun `handleFCMMessage should process device alert message`() = runTest {
        // Given
        val messageData = mapOf(
            "type" to "device_alert",
            "deviceId" to "device123",
            "deviceName" to "Test Device",
            "alertType" to "temperature",
            "message" to "High temperature detected",
            "severity" to "high"
        )

        val remoteMessage = mockk<RemoteMessage> {
            every { data } returns messageData
        }

        every { notificationManagerCompat.notify(any(), any()) } just Runs
        every { notificationPreferences.areDeviceAlertsEnabled() } returns true
        every { notificationPreferences.getMinimumPriority() } returns "low"

        // When
        notificationManager.handleFCMMessage(remoteMessage)

        // Then
        verify { notificationManagerCompat.notify(any(), any()) }
    }

    @Test
    fun `handleFCMMessage should process system update message`() = runTest {
        // Given
        val messageData = mapOf(
            "type" to "system_update",
            "title" to "System Update",
            "message" to "New version available",
            "version" to "1.2.0",
            "required" to "true"
        )

        val remoteMessage = mockk<RemoteMessage> {
            every { data } returns messageData
        }

        every { notificationManagerCompat.notify(any(), any()) } just Runs
        every { notificationPreferences.areSystemUpdatesEnabled() } returns true

        // When
        notificationManager.handleFCMMessage(remoteMessage)

        // Then
        verify { notificationManagerCompat.notify(any(), any()) }
    }

    @Test
    fun `handleFCMMessage should process geofence alert message`() = runTest {
        // Given
        val messageData = mapOf(
            "type" to "geofence_alert",
            "deviceId" to "device123",
            "deviceName" to "Test Device",
            "geofenceName" to "Home",
            "eventType" to "ENTER",
            "latitude" to "40.7128",
            "longitude" to "-74.0060"
        )

        val remoteMessage = mockk<RemoteMessage> {
            every { data } returns messageData
        }

        every { notificationManagerCompat.notify(any(), any()) } just Runs
        every { notificationPreferences.areGeofenceAlertsEnabled() } returns true

        // When
        notificationManager.handleFCMMessage(remoteMessage)

        // Then
        verify { notificationManagerCompat.notify(any(), any()) }
    }

    @Test
    fun `handleFCMMessage should ignore unknown message types`() = runTest {
        // Given
        val messageData = mapOf(
            "type" to "unknown_type",
            "message" to "Unknown message"
        )

        val remoteMessage = mockk<RemoteMessage> {
            every { data } returns messageData
        }

        // When
        notificationManager.handleFCMMessage(remoteMessage)

        // Then
        verify(exactly = 0) { notificationManagerCompat.notify(any(), any()) }
    }

    @Test
    fun `areNotificationsEnabled should return system notification status`() {
        // Given
        every { notificationManagerCompat.areNotificationsEnabled() } returns true

        // When
        val enabled = notificationManager.areNotificationsEnabled()

        // Then
        assertTrue(enabled)
    }

    @Test
    fun `createNotificationChannels should create all required channels`() {
        // Given
        every { systemNotificationManager.createNotificationChannel(any()) } just Runs

        // When
        notificationManager.createNotificationChannels()

        // Then
        verify(atLeast = 4) { systemNotificationManager.createNotificationChannel(any()) }
    }

    @Test
    fun `getNotificationId should generate consistent IDs for same device`() {
        // Given
        val deviceId = "device123"
        val type = "alert"

        // When
        val id1 = notificationManager.getNotificationId(deviceId, type)
        val id2 = notificationManager.getNotificationId(deviceId, type)

        // Then
        assertEquals(id1, id2)
    }

    @Test
    fun `getNotificationId should generate different IDs for different devices`() {
        // Given
        val deviceId1 = "device123"
        val deviceId2 = "device456"
        val type = "alert"

        // When
        val id1 = notificationManager.getNotificationId(deviceId1, type)
        val id2 = notificationManager.getNotificationId(deviceId2, type)

        // Then
        assertTrue(id1 != id2)
    }

    @Test
    fun `getPriorityFromSeverity should return correct priority values`() {
        // When & Then
        assertEquals(
            android.app.Notification.PRIORITY_MAX,
            notificationManager.getPriorityFromSeverity("critical")
        )
        assertEquals(
            android.app.Notification.PRIORITY_HIGH,
            notificationManager.getPriorityFromSeverity("high")
        )
        assertEquals(
            android.app.Notification.PRIORITY_DEFAULT,
            notificationManager.getPriorityFromSeverity("medium")
        )
        assertEquals(
            android.app.Notification.PRIORITY_LOW,
            notificationManager.getPriorityFromSeverity("low")
        )
        assertEquals(
            android.app.Notification.PRIORITY_DEFAULT,
            notificationManager.getPriorityFromSeverity("unknown")
        )
    }

    @Test
    fun `shouldShowNotification should respect priority filters`() {
        // Given
        every { notificationPreferences.getMinimumPriority() } returns "medium"

        // When & Then
        assertTrue(notificationManager.shouldShowNotification("critical"))
        assertTrue(notificationManager.shouldShowNotification("high"))
        assertTrue(notificationManager.shouldShowNotification("medium"))
        assertTrue(!notificationManager.shouldShowNotification("low"))
    }

    @Test
    fun `shouldShowNotification should allow all when minimum is low`() {
        // Given
        every { notificationPreferences.getMinimumPriority() } returns "low"

        // When & Then
        assertTrue(notificationManager.shouldShowNotification("critical"))
        assertTrue(notificationManager.shouldShowNotification("high"))
        assertTrue(notificationManager.shouldShowNotification("medium"))
        assertTrue(notificationManager.shouldShowNotification("low"))
    }
}