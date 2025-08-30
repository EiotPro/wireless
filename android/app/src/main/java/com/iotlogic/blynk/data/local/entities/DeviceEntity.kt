package com.iotlogic.blynk.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "devices")
@TypeConverters(DeviceConverters::class)
data class DeviceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val protocol: String, // BLE, WiFi, USB, MQTT
    val status: String, // CONNECTED, DISCONNECTED, ERROR
    val token: String,
    val userId: String,
    val macAddress: String? = null,
    val ipAddress: String? = null,
    val port: Int? = null,
    val lastSeen: Long,
    val batteryLevel: Int? = null,
    val signalStrength: Int? = null,
    val firmwareVersion: String? = null,
    val hardwareVersion: String? = null,
    val manufacturer: String? = null,
    val modelNumber: String? = null,
    val serialNumber: String? = null,
    val location: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val configuration: Map<String, String> = emptyMap(),
    val metadata: Map<String, String> = emptyMap(),
    val isOnline: Boolean = false,
    val connectionQuality: String? = null, // EXCELLENT, GOOD, FAIR, POOR
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

class DeviceConverters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromStringMap(value: Map<String, String>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toStringMap(value: String): Map<String, String> {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, mapType)
    }
}