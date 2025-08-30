package com.iotlogic.blynk.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class IoTLogicFirebaseMessagingService : FirebaseMessagingService() {
    
    @Inject
    lateinit var notificationManager: AppNotificationManager
    
    @Inject
    lateinit var fcmTokenManager: FCMTokenManager
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d("FCM", "From: ${remoteMessage.from}")
        
        // Handle FCM message
        notificationManager.handleFCMMessage(remoteMessage)
        
        // Log the message data for debugging
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCM", "Message data payload: ${remoteMessage.data}")
        }
        
        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
        }
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
        
        // Send token to server
        fcmTokenManager.updateToken(token)
    }
}