package com.iotlogic.blynk.data.repository

import com.iotlogic.blynk.data.local.dao.ConfigurationDao
import com.iotlogic.blynk.data.local.entities.ConfigurationEntity
import com.iotlogic.blynk.data.remote.ApiClient
import com.iotlogic.blynk.domain.model.ConfigDataType
import com.iotlogic.blynk.domain.model.Configuration
import com.iotlogic.blynk.domain.model.SyncStatus
import com.iotlogic.blynk.domain.repository.ConfigurationRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigurationRepositoryImpl @Inject constructor(
    private val configurationDao: ConfigurationDao,
    private val apiClient: ApiClient
) : ConfigurationRepository {
    
    override fun getConfigurationsByDevice(deviceId: String): Flow<List<Configuration>> {
        return configurationDao.getConfigurationsByDevice(deviceId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getConfigurationsByDeviceAndCategory(deviceId: String, category: String): Flow<List<Configuration>> {
        return configurationDao.getConfigurationsByDeviceAndCategory(deviceId, category).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getConfiguration(deviceId: String, configKey: String): Flow<Configuration?> {
        return configurationDao.observeConfiguration(deviceId, configKey).map { entity ->
            entity?.toDomain()
        }
    }
    
    override suspend fun getConfigurationValue(deviceId: String, configKey: String): String? {
        return configurationDao.getConfigurationValue(deviceId, configKey)
    }
    
    override suspend fun getCategoriesForDevice(deviceId: String): List<String> {
        return configurationDao.getCategoriesForDevice(deviceId)
    }
    
    override suspend fun saveDeviceConfig(configuration: Configuration): Result<Unit> {
        return try {
            val entity = configuration.toEntity()
            configurationDao.insertConfiguration(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun saveDeviceConfigs(configurations: List<Configuration>): Result<Unit> {
        return try {
            val entities = configurations.map { it.toEntity() }
            configurationDao.insertConfigurations(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateConfigurationValue(deviceId: String, configKey: String, value: String): Result<Unit> {
        return try {
            configurationDao.updateConfigurationValue(deviceId, configKey, value)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteConfiguration(deviceId: String, configKey: String): Result<Unit> {
        return try {
            configurationDao.deleteConfiguration(deviceId, configKey)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteConfigurationsByDevice(deviceId: String): Result<Unit> {
        return try {
            configurationDao.deleteConfigurationsByDevice(deviceId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun syncConfigurations(token: String, deviceId: String): Result<Unit> {
        return try {
            // This would sync configurations with the backend
            // Implementation would depend on specific API requirements
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun uploadPendingConfigurations(token: String): Result<Unit> {
        return try {
            val pendingConfigurations = configurationDao.getPendingSyncConfigurations()
            
            if (pendingConfigurations.isNotEmpty()) {
                // Group by device for batch upload
                val groupedByDevice = pendingConfigurations.groupBy { it.deviceId }
                
                for ((deviceId, configGroup) in groupedByDevice) {
                    // In real implementation, upload configurations to backend
                    val configIds = configGroup.map { it.id }
                    configurationDao.updateSyncStatusBatch(configIds, "SYNCED")
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markConfigurationsAsSynced(configurationIds: List<String>): Result<Unit> {
        return try {
            configurationDao.updateSyncStatusBatch(configurationIds, "SYNCED")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun resetDeviceConfigurations(deviceId: String, defaultConfigurations: List<Configuration>): Result<Unit> {
        return try {
            val entities = defaultConfigurations.map { it.toEntity() }
            configurationDao.resetDeviceConfigurations(deviceId, entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateDeviceConfigurations(deviceId: String, configurations: Map<String, String>): Result<Unit> {
        return try {
            configurationDao.updateDeviceConfigurations(deviceId, configurations)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getConfigurationCountForDevice(deviceId: String): Int {
        return configurationDao.getConfigurationCountForDevice(deviceId)
    }
    
    override suspend fun getEditableConfigurations(deviceId: String): List<Configuration> {
        return configurationDao.getEditableConfigurations(deviceId).first().map { it.toDomain() }
    }
    
    override suspend fun getReadOnlyConfigurations(deviceId: String): List<Configuration> {
        return configurationDao.getReadOnlyConfigurations(deviceId).first().map { it.toDomain() }
    }
}

// Extension functions for mapping
private fun ConfigurationEntity.toDomain(): Configuration {
    return Configuration(
        id = id,
        deviceId = deviceId,
        configKey = configKey,
        configValue = configValue,
        dataType = parseDataType(dataType),
        category = category,
        description = description,
        isReadOnly = isReadOnly,
        validationRules = parseValidationRules(validationRules),
        defaultValue = defaultValue,
        unit = unit,
        priority = priority,
        syncStatus = parseSyncStatus(syncStatus),
        lastModified = lastModified,
        createdAt = createdAt
    )
}

private fun Configuration.toEntity(): ConfigurationEntity {
    return ConfigurationEntity(
        id = id,
        deviceId = deviceId,
        configKey = configKey,
        configValue = configValue,
        dataType = dataType.name,
        category = category,
        description = description,
        isReadOnly = isReadOnly,
        validationRules = validationRules,
        defaultValue = defaultValue,
        unit = unit,
        priority = priority,
        syncStatus = syncStatus.name,
        lastModified = lastModified,
        createdAt = createdAt
    )
}

private fun parseDataType(dataType: String): ConfigDataType {
    return when (dataType.uppercase()) {
        "INTEGER" -> ConfigDataType.INTEGER
        "DOUBLE" -> ConfigDataType.DOUBLE
        "BOOLEAN" -> ConfigDataType.BOOLEAN
        "JSON" -> ConfigDataType.JSON
        else -> ConfigDataType.STRING
    }
}

private fun parseValidationRules(rules: Map<String, Any>?): Map<String, Any>? {
    return rules
}

private fun parseSyncStatus(status: String): SyncStatus {
    return try {
        SyncStatus.valueOf(status.uppercase())
    } catch (e: IllegalArgumentException) {
        // Default to PENDING if the status string doesn't match any enum value
        SyncStatus.PENDING
    }
}