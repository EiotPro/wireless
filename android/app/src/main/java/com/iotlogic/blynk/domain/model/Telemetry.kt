package com.iotlogic.blynk.domain.model

data class Telemetry(
    val id: String,
    val deviceId: String,
    val sensorType: String, // temperature, humidity, motion, light, etc.
    val value: Double,
    val unit: String? = null,
    val timestamp: Long,
    val quality: String? = null, // GOOD, QUESTIONABLE, BAD
    val rawValue: String? = null, // Original raw sensor reading
    val metadata: Map<String, Any>? = null,
    val isProcessed: Boolean = false,
    val syncStatus: String = "PENDING",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getDataQuality(): DataQuality {
        return when (quality?.uppercase()) {
            "GOOD" -> DataQuality.GOOD
            "QUESTIONABLE" -> DataQuality.QUESTIONABLE
            "BAD" -> DataQuality.BAD
            else -> DataQuality.UNKNOWN
        }
    }
    
    fun getSensorTypeCategory(): SensorCategory {
        return when (sensorType.lowercase()) {
            "temperature", "temp" -> SensorCategory.ENVIRONMENTAL
            "humidity", "moisture" -> SensorCategory.ENVIRONMENTAL
            "pressure", "barometric" -> SensorCategory.ENVIRONMENTAL
            "light", "luminosity", "lux" -> SensorCategory.ENVIRONMENTAL
            "motion", "pir", "movement" -> SensorCategory.MOTION
            "acceleration", "accelerometer" -> SensorCategory.MOTION
            "gyroscope", "gyro" -> SensorCategory.MOTION
            "gps", "location", "position" -> SensorCategory.LOCATION
            "voltage", "current", "power" -> SensorCategory.ELECTRICAL
            "battery", "charge" -> SensorCategory.POWER
            "sound", "noise", "audio" -> SensorCategory.AUDIO
            "gas", "co2", "air_quality" -> SensorCategory.AIR_QUALITY
            else -> SensorCategory.OTHER
        }
    }
    
    fun getFormattedValue(): String {
        return if (unit != null) {
            "$value $unit"
        } else {
            value.toString()
        }
    }
    
    fun isRecent(thresholdMinutes: Int = 60): Boolean {
        val thresholdMs = thresholdMinutes * 60 * 1000L
        return (System.currentTimeMillis() - timestamp) < thresholdMs
    }
}

enum class DataQuality {
    GOOD, QUESTIONABLE, BAD, UNKNOWN
}

enum class SensorCategory {
    ENVIRONMENTAL,
    MOTION,
    LOCATION,
    ELECTRICAL,
    POWER,
    AUDIO,
    AIR_QUALITY,
    OTHER
}

// Using the SyncStatus enum from com.iotlogic.blynk.domain.model package