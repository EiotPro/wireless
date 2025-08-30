package com.iotlogic.blynk.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "telemetry",
    foreignKeys = [
        ForeignKey(
            entity = DeviceEntity::class,
            parentColumns = ["id"],
            childColumns = ["deviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["deviceId"]),
        Index(value = ["timestamp"]),
        Index(value = ["sensorType"])
    ]
)
data class TelemetryEntity(
    @PrimaryKey val id: String,
    val deviceId: String,
    val sensorType: String, // temperature, humidity, motion, light, etc.
    val value: Double,
    val unit: String? = null,
    val timestamp: Long,
    val quality: String? = null, // GOOD, QUESTIONABLE, BAD
    val rawValue: String? = null, // Original raw sensor reading
    val metadata: String? = null, // JSON string for additional metadata
    val isProcessed: Boolean = false,
    val syncStatus: String = "PENDING", // PENDING, SYNCED, FAILED
    val createdAt: Long = System.currentTimeMillis()
)