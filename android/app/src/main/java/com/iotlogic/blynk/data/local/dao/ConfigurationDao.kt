package com.iotlogic.blynk.data.local.dao

import androidx.room.*
import com.iotlogic.blynk.data.local.entities.ConfigurationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigurationDao {
    
    @Query("SELECT * FROM configurations WHERE deviceId = :deviceId ORDER BY priority DESC, configKey ASC")
    fun getConfigurationsByDevice(deviceId: String): Flow<List<ConfigurationEntity>>
    
    @Query("SELECT * FROM configurations WHERE deviceId = :deviceId AND category = :category ORDER BY priority DESC, configKey ASC")
    fun getConfigurationsByDeviceAndCategory(deviceId: String, category: String): Flow<List<ConfigurationEntity>>
    
    @Query("SELECT * FROM configurations WHERE deviceId = :deviceId AND configKey = :configKey")
    suspend fun getConfiguration(deviceId: String, configKey: String): ConfigurationEntity?
    
    @Query("SELECT * FROM configurations WHERE deviceId = :deviceId AND configKey = :configKey")
    fun observeConfiguration(deviceId: String, configKey: String): Flow<ConfigurationEntity?>
    
    @Query("SELECT configValue FROM configurations WHERE deviceId = :deviceId AND configKey = :configKey")
    suspend fun getConfigurationValue(deviceId: String, configKey: String): String?
    
    @Query("SELECT * FROM configurations WHERE category = :category ORDER BY deviceId, priority DESC")
    fun getConfigurationsByCategory(category: String): Flow<List<ConfigurationEntity>>
    
    @Query("SELECT DISTINCT category FROM configurations WHERE deviceId = :deviceId")
    suspend fun getCategoriesForDevice(deviceId: String): List<String>
    
    @Query("SELECT DISTINCT category FROM configurations")
    suspend fun getAllCategories(): List<String>
    
    @Query("SELECT DISTINCT configKey FROM configurations WHERE category = :category")
    suspend fun getConfigKeysForCategory(category: String): List<String>
    
    @Query("SELECT * FROM configurations WHERE isReadOnly = 0 AND deviceId = :deviceId ORDER BY priority DESC")
    fun getEditableConfigurations(deviceId: String): Flow<List<ConfigurationEntity>>
    
    @Query("SELECT * FROM configurations WHERE isReadOnly = 1 AND deviceId = :deviceId ORDER BY configKey ASC")
    fun getReadOnlyConfigurations(deviceId: String): Flow<List<ConfigurationEntity>>
    
    @Query("SELECT * FROM configurations WHERE syncStatus = :status ORDER BY lastModified ASC")
    suspend fun getConfigurationsBySyncStatus(status: String): List<ConfigurationEntity>
    
    @Query("SELECT * FROM configurations WHERE syncStatus = 'PENDING' ORDER BY priority DESC, lastModified ASC LIMIT :limit")
    suspend fun getPendingSyncConfigurations(limit: Int = 50): List<ConfigurationEntity>
    
    @Query("SELECT COUNT(*) FROM configurations WHERE deviceId = :deviceId")
    suspend fun getConfigurationCountForDevice(deviceId: String): Int
    
    @Query("SELECT COUNT(*) FROM configurations WHERE deviceId = :deviceId AND category = :category")
    suspend fun getConfigurationCountForCategory(deviceId: String, category: String): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfiguration(configuration: ConfigurationEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfigurations(configurations: List<ConfigurationEntity>)
    
    @Update
    suspend fun updateConfiguration(configuration: ConfigurationEntity)
    
    @Query("UPDATE configurations SET configValue = :value, lastModified = :timestamp, syncStatus = 'PENDING' WHERE deviceId = :deviceId AND configKey = :configKey")
    suspend fun updateConfigurationValue(
        deviceId: String, 
        configKey: String, 
        value: String, 
        timestamp: Long = System.currentTimeMillis()
    )
    
    @Query("UPDATE configurations SET syncStatus = :status WHERE id = :configurationId")
    suspend fun updateSyncStatus(configurationId: String, status: String)
    
    @Query("UPDATE configurations SET syncStatus = :status WHERE id IN (:configurationIds)")
    suspend fun updateSyncStatusBatch(configurationIds: List<String>, status: String)
    
    @Query("UPDATE configurations SET syncStatus = 'PENDING', lastModified = :timestamp WHERE deviceId = :deviceId AND configKey IN (:configKeys)")
    suspend fun markConfigurationsForSync(
        deviceId: String, 
        configKeys: List<String>, 
        timestamp: Long = System.currentTimeMillis()
    )
    
    @Delete
    suspend fun deleteConfiguration(configuration: ConfigurationEntity)
    
    @Query("DELETE FROM configurations WHERE id = :configurationId")
    suspend fun deleteConfigurationById(configurationId: String)
    
    @Query("DELETE FROM configurations WHERE deviceId = :deviceId")
    suspend fun deleteConfigurationsByDevice(deviceId: String)
    
    @Query("DELETE FROM configurations WHERE deviceId = :deviceId AND configKey = :configKey")
    suspend fun deleteConfiguration(deviceId: String, configKey: String)
    
    @Query("DELETE FROM configurations WHERE deviceId = :deviceId AND category = :category")
    suspend fun deleteConfigurationsByCategory(deviceId: String, category: String)
    
    @Query("DELETE FROM configurations")
    suspend fun deleteAllConfigurations()
    
    // Bulk operations for device setup/reset
    @Transaction
    suspend fun resetDeviceConfigurations(deviceId: String, defaultConfigurations: List<ConfigurationEntity>) {
        deleteConfigurationsByDevice(deviceId)
        insertConfigurations(defaultConfigurations)
    }
    
    @Transaction
    suspend fun updateDeviceConfigurations(deviceId: String, configurations: Map<String, String>) {
        val timestamp = System.currentTimeMillis()
        configurations.forEach { (key, value) ->
            updateConfigurationValue(deviceId, key, value, timestamp)
        }
    }
    
    // Configuration validation
    @Query("SELECT * FROM configurations WHERE deviceId = :deviceId AND validationRules IS NOT NULL")
    suspend fun getConfigurationsWithValidation(deviceId: String): List<ConfigurationEntity>
    
    // Export/Import configurations
    @Query("SELECT * FROM configurations WHERE deviceId = :deviceId")
    suspend fun exportDeviceConfigurations(deviceId: String): List<ConfigurationEntity>
    
    @Transaction
    suspend fun importDeviceConfigurations(deviceId: String, configurations: List<ConfigurationEntity>) {
        val deviceConfigs = configurations.map { it.copy(deviceId = deviceId, lastModified = System.currentTimeMillis()) }
        insertConfigurations(deviceConfigs)
    }
}