package com.iotlogic.blynk.location

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var locationManager: LocationManager
    
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        
        if (geofencingEvent?.hasError() == true) {
            // Handle error
            return
        }
        
        val geofenceTransition = geofencingEvent?.geofenceTransition
        
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {
            
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            val location = geofencingEvent.triggeringLocation
            
            triggeringGeofences?.forEach { geofence ->
                locationManager.processGeofenceEvent(
                    geofenceId = geofence.requestId,
                    transition = geofenceTransition,
                    location = location
                )
            }
        }
    }
    
    companion object {
        private const val REQUEST_CODE = 1001
        
        fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
            return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}