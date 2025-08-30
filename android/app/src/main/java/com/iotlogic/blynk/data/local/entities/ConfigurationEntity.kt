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
    tableName = "configurations",
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
        Index(value = ["configKey"])
    ]
)
@TypeConverters(ConfigurationConverters::class)
data class ConfigurationEntity(
    @PrimaryKey val id: String,
    val deviceId: String,
    val configKey: String,
    val configValue: String,
    val dataType: String, // STRING, INTEGER, DOUBLE, BOOLEAN, JSON
    val category: String, // NETWORK, SENSOR, COMMUNICATION, POWER, etc.
    val description: String? = null,
    val isReadOnly: Boolean = false,
    val validationRules: Map<String, Any>? = null, // min, max, pattern, options
    val defaultValue: String? = null,
    val unit: String? = null,
    val priority: Int = 0, // Higher priority configs are applied first
    val syncStatus: String = "PENDING", // PENDING, SYNCED, FAILED
    val lastModified: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

class ConfigurationConverters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromValidationRules(value: Map<String, Any>?): String? {
        return value?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toValidationRules(value: String?): Map<String, Any>? {
        return value?.let {
            val mapType = object : TypeToken<Map<String, Any>>() {}.type
            gson.fromJson(it, mapType)
        }
    }
}