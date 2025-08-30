package com.iotlogic.blynk.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(
    tableName = "command_queue",
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
        Index(value = ["status"]),
        Index(value = ["priority"]),
        Index(value = ["createdAt"])
    ]
)
@TypeConverters(CommandQueueConverters::class)
data class CommandQueueEntity(
    @PrimaryKey val id: String,
    val deviceId: String,
    val command: String,
    val parameters: Map<String, Any> = emptyMap(),
    val priority: Int = 0, // Higher number = higher priority
    val status: String = "PENDING", // PENDING, SENT, COMPLETED, FAILED, CANCELLED
    val retryCount: Int = 0,
    val maxRetries: Int = 3,
    val result: String? = null,
    val errorMessage: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val scheduledAt: Long? = null, // For delayed execution
    val sentAt: Long? = null,
    val completedAt: Long? = null,
    val expiresAt: Long? = null // Command expiration time
)

class CommandQueueConverters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromParametersMap(value: Map<String, Any>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toParametersMap(value: String): Map<String, Any> {
        val mapType = object : TypeToken<Map<String, Any>>() {}.type
        return gson.fromJson(value, mapType) ?: emptyMap()
    }
}