package com.iotlogic.blynk.domain.model

data class Configuration(
    val id: String,
    val deviceId: String,
    val configKey: String,
    val configValue: String,
    val dataType: ConfigDataType,
    val category: String, // NETWORK, SENSOR, COMMUNICATION, POWER, etc.
    val description: String? = null,
    val isReadOnly: Boolean = false,
    val validationRules: Map<String, Any>? = null, // min, max, pattern, options
    val defaultValue: String? = null,
    val unit: String? = null,
    val priority: Int = 0, // Higher priority configs are applied first
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val lastModified: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getTypedValue(): Any? {
        return when (dataType) {
            ConfigDataType.STRING -> configValue
            ConfigDataType.INTEGER -> configValue.toIntOrNull()
            ConfigDataType.DOUBLE -> configValue.toDoubleOrNull()
            ConfigDataType.BOOLEAN -> configValue.toBooleanStrictOrNull()
            ConfigDataType.JSON -> configValue // Would need JSON parsing
        }
    }
    
    fun isValid(): Boolean {
        return when (dataType) {
            ConfigDataType.INTEGER -> configValue.toIntOrNull() != null
            ConfigDataType.DOUBLE -> configValue.toDoubleOrNull() != null
            ConfigDataType.BOOLEAN -> configValue.toBooleanStrictOrNull() != null
            ConfigDataType.STRING, ConfigDataType.JSON -> true
        }
    }
    
    fun validateValue(newValue: String): ValidationResult {
        if (!isValid()) {
            return ValidationResult.Invalid("Invalid data type")
        }
        
        validationRules?.let { rules ->
            when (dataType) {
                ConfigDataType.INTEGER -> {
                    val intValue = newValue.toIntOrNull() ?: return ValidationResult.Invalid("Not a valid integer")
                    val min = rules["min"] as? Int
                    val max = rules["max"] as? Int
                    
                    if (min != null && intValue < min) {
                        return ValidationResult.Invalid("Value must be at least $min")
                    }
                    if (max != null && intValue > max) {
                        return ValidationResult.Invalid("Value must be at most $max")
                    }
                }
                
                ConfigDataType.DOUBLE -> {
                    val doubleValue = newValue.toDoubleOrNull() ?: return ValidationResult.Invalid("Not a valid number")
                    val min = rules["min"] as? Double
                    val max = rules["max"] as? Double
                    
                    if (min != null && doubleValue < min) {
                        return ValidationResult.Invalid("Value must be at least $min")
                    }
                    if (max != null && doubleValue > max) {
                        return ValidationResult.Invalid("Value must be at most $max")
                    }
                }
                
                ConfigDataType.STRING -> {
                    val pattern = rules["pattern"] as? String
                    val options = rules["options"] as? List<String>
                    
                    if (pattern != null && !newValue.matches(Regex(pattern))) {
                        return ValidationResult.Invalid("Value doesn't match required pattern")
                    }
                    if (options != null && !options.contains(newValue)) {
                        return ValidationResult.Invalid("Value must be one of: ${options.joinToString()}")
                    }
                }
                
                else -> { /* No additional validation for other types */ }
            }
        }
        
        return ValidationResult.Valid
    }
    
    fun getCategoryType(): ConfigCategory {
        return when (category.uppercase()) {
            "NETWORK", "WIFI", "BLUETOOTH", "CONNECTIVITY" -> ConfigCategory.NETWORK
            "SENSOR", "SENSORS", "MEASUREMENT" -> ConfigCategory.SENSOR
            "COMMUNICATION", "PROTOCOL", "MQTT", "HTTP" -> ConfigCategory.COMMUNICATION
            "POWER", "BATTERY", "ENERGY" -> ConfigCategory.POWER
            "SECURITY", "AUTH", "AUTHENTICATION" -> ConfigCategory.SECURITY
            "DISPLAY", "UI", "INTERFACE" -> ConfigCategory.DISPLAY
            "SYSTEM", "OS", "FIRMWARE" -> ConfigCategory.SYSTEM
            else -> ConfigCategory.OTHER
        }
    }
}

enum class ConfigDataType {
    STRING, INTEGER, DOUBLE, BOOLEAN, JSON
}

enum class ConfigCategory {
    NETWORK,
    SENSOR,
    COMMUNICATION,
    POWER,
    SECURITY,
    DISPLAY,
    SYSTEM,
    OTHER
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
}