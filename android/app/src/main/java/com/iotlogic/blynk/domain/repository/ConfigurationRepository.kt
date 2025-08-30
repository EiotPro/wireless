package com.iotlogic.blynk.domain.repository

import com.iotlogic.blynk.domain.model.Configuration
import kotlinx.coroutines.flow.Flow

interface ConfigurationRepository {
    
    // Observation methods
    fun getConfigurationsByDevice(deviceId: String): Flow<List<Configuration>>
    fun getConfigurationsByDeviceAndCategory(deviceId: String, category: String): Flow<List<Configuration>>
    fun getConfiguration(deviceId: String, configKey: String): Flow<Configuration?>
    
    // Single item queries
    suspend fun getConfigurationValue(deviceId: String, configKey: String): String?
    suspend fun getCategoriesForDevice(deviceId: String): List<String>
    
    // CRUD operations
    suspend fun saveDeviceConfig(configuration: Configuration): Result<Unit>
    suspend fun saveDeviceConfigs(configurations: List<Configuration>): Result<Unit>
    suspend fun updateConfigurationValue(deviceId: String, configKey: String, value: String): Result<Unit>
    suspend fun deleteConfiguration(deviceId: String, configKey: String): Result<Unit>
    suspend fun deleteConfigurationsByDevice(deviceId: String): Result<Unit>
    
    // Sync operations
    suspend fun syncConfigurations(token: String, deviceId: String): Result<Unit>
    suspend fun uploadPendingConfigurations(token: String): Result<Unit>
    suspend fun markConfigurationsAsSynced(configurationIds: List<String>): Result<Unit>
    
    // Bulk operations
    suspend fun resetDeviceConfigurations(deviceId: String, defaultConfigurations: List<Configuration>): Result<Unit>
    suspend fun updateDeviceConfigurations(deviceId: String, configurations: Map<String, String>): Result<Unit>
    
    // Metadata queries
    suspend fun getConfigurationCountForDevice(deviceId: String): Int
    suspend fun getEditableConfigurations(deviceId: String): List<Configuration>
    suspend fun getReadOnlyConfigurations(deviceId: String): List<Configuration>
}