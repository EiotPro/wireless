package com.iotlogic.blynk.domain.model

data class Device(
    val id: String,
    val name: String,
    val type: String,
    val protocol: String, // BLE, WiFi, USB, MQTT
    val status: String, // CONNECTED, DISCONNECTED, ERROR
    val token: String,
    val userId: String,
    val description: String = "",
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
) {
    fun isConnected(): Boolean = status == "CONNECTED" && isOnline
    
    fun hasLocation(): Boolean = latitude != null && longitude != null
    
    fun getConnectionQualityLevel(): ConnectionQuality {
        return when (connectionQuality) {
            "EXCELLENT" -> ConnectionQuality.EXCELLENT
            "GOOD" -> ConnectionQuality.GOOD
            "FAIR" -> ConnectionQuality.FAIR
            "POOR" -> ConnectionQuality.POOR
            else -> ConnectionQuality.UNKNOWN
        }
    }
    
    fun getBatteryStatus(): BatteryStatus {
        return when {
            batteryLevel == null -> BatteryStatus.UNKNOWN
            batteryLevel >= 80 -> BatteryStatus.HIGH
            batteryLevel >= 50 -> BatteryStatus.MEDIUM
            batteryLevel >= 20 -> BatteryStatus.LOW
            else -> BatteryStatus.CRITICAL
        }
    }
    
    fun getProtocolType(): ProtocolType {
        return when (protocol.uppercase()) {
            "BLE", "BLUETOOTH" -> ProtocolType.BLUETOOTH_LE
            "WIFI", "HTTP" -> ProtocolType.WIFI
            "USB", "SERIAL" -> ProtocolType.USB_SERIAL
            "MQTT" -> ProtocolType.MQTT
            else -> ProtocolType.UNKNOWN
        }
    }
}

enum class ConnectionQuality {
    EXCELLENT, GOOD, FAIR, POOR, UNKNOWN
}

enum class BatteryStatus {
    HIGH, MEDIUM, LOW, CRITICAL, UNKNOWN
}

enum class ProtocolType {
    BLUETOOTH_LE, WIFI, USB_SERIAL, MQTT, UNKNOWN
}