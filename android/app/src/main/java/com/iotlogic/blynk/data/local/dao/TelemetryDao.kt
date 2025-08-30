package com.iotlogic.blynk.data.local.dao

import androidx.room.*
import com.iotlogic.blynk.data.local.entities.TelemetryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TelemetryDao {
    
    @Query("SELECT * FROM telemetry ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentTelemetry(limit: Int = 100): Flow<List<TelemetryEntity>>
    
    @Query("SELECT * FROM telemetry WHERE deviceId = :deviceId ORDER BY timestamp DESC LIMIT :limit")
    fun getTelemetryByDevice(deviceId: String, limit: Int = 100): Flow<List<TelemetryEntity>>
    
    @Query("SELECT * FROM telemetry WHERE deviceId = :deviceId AND sensorType = :sensorType ORDER BY timestamp DESC LIMIT :limit")
    fun getTelemetryByDeviceAndSensor(deviceId: String, sensorType: String, limit: Int = 100): Flow<List<TelemetryEntity>>
    
    @Query("SELECT * FROM telemetry WHERE deviceId = :deviceId AND timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getTelemetryByTimeRange(deviceId: String, startTime: Long, endTime: Long): Flow<List<TelemetryEntity>>
    
    @Query("SELECT * FROM telemetry WHERE sensorType = :sensorType ORDER BY timestamp DESC LIMIT :limit")
    fun getTelemetryBySensorType(sensorType: String, limit: Int = 100): Flow<List<TelemetryEntity>>
    
    @Query("SELECT * FROM telemetry WHERE deviceId = :deviceId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestTelemetryForDevice(deviceId: String): TelemetryEntity?
    
    @Query("SELECT * FROM telemetry WHERE deviceId = :deviceId AND sensorType = :sensorType ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestTelemetryForSensor(deviceId: String, sensorType: String): TelemetryEntity?
    
    @Query("SELECT AVG(value) FROM telemetry WHERE deviceId = :deviceId AND sensorType = :sensorType AND timestamp BETWEEN :startTime AND :endTime")
    suspend fun getAverageTelemetryValue(deviceId: String, sensorType: String, startTime: Long, endTime: Long): Double?
    
    @Query("SELECT MIN(value) FROM telemetry WHERE deviceId = :deviceId AND sensorType = :sensorType AND timestamp BETWEEN :startTime AND :endTime")
    suspend fun getMinTelemetryValue(deviceId: String, sensorType: String, startTime: Long, endTime: Long): Double?
    
    @Query("SELECT MAX(value) FROM telemetry WHERE deviceId = :deviceId AND sensorType = :sensorType AND timestamp BETWEEN :startTime AND :endTime")
    suspend fun getMaxTelemetryValue(deviceId: String, sensorType: String, startTime: Long, endTime: Long): Double?
    
    @Query("SELECT COUNT(*) FROM telemetry WHERE deviceId = :deviceId")
    suspend fun getTelemetryCountForDevice(deviceId: String): Int
    
    @Query("SELECT COUNT(*) FROM telemetry WHERE deviceId = :deviceId AND timestamp > :timestamp")
    suspend fun getTelemetryCountSince(deviceId: String, timestamp: Long): Int
    
    @Query("SELECT DISTINCT sensorType FROM telemetry WHERE deviceId = :deviceId")
    suspend fun getSensorTypesForDevice(deviceId: String): List<String>
    
    @Query("SELECT DISTINCT sensorType FROM telemetry")
    suspend fun getAllSensorTypes(): List<String>
    
    @Query("SELECT * FROM telemetry WHERE syncStatus = :status ORDER BY timestamp ASC")
    suspend fun getTelemetryBySyncStatus(status: String): List<TelemetryEntity>
    
    @Query("SELECT * FROM telemetry WHERE syncStatus = 'PENDING' ORDER BY timestamp ASC LIMIT :limit")
    suspend fun getPendingSyncTelemetry(limit: Int = 50): List<TelemetryEntity>
    
    @Query("SELECT * FROM telemetry WHERE quality = :quality ORDER BY timestamp DESC LIMIT :limit")
    fun getTelemetryByQuality(quality: String, limit: Int = 100): Flow<List<TelemetryEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTelemetry(telemetry: TelemetryEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTelemetryBatch(telemetryList: List<TelemetryEntity>)
    
    @Update
    suspend fun updateTelemetry(telemetry: TelemetryEntity)
    
    @Query("UPDATE telemetry SET syncStatus = :status WHERE id = :telemetryId")
    suspend fun updateSyncStatus(telemetryId: String, status: String)
    
    @Query("UPDATE telemetry SET syncStatus = :status WHERE id IN (:telemetryIds)")
    suspend fun updateSyncStatusBatch(telemetryIds: List<String>, status: String)
    
    @Query("UPDATE telemetry SET isProcessed = 1 WHERE id = :telemetryId")
    suspend fun markAsProcessed(telemetryId: String)
    
    @Delete
    suspend fun deleteTelemetry(telemetry: TelemetryEntity)
    
    @Query("DELETE FROM telemetry WHERE id = :telemetryId")
    suspend fun deleteTelemetryById(telemetryId: String)
    
    @Query("DELETE FROM telemetry WHERE deviceId = :deviceId")
    suspend fun deleteTelemetryByDevice(deviceId: String)
    
    @Query("DELETE FROM telemetry WHERE timestamp < :timestamp")
    suspend fun deleteOldTelemetry(timestamp: Long)
    
    @Query("DELETE FROM telemetry WHERE deviceId = :deviceId AND timestamp < :timestamp")
    suspend fun deleteOldTelemetryForDevice(deviceId: String, timestamp: Long)
    
    @Query("DELETE FROM telemetry WHERE syncStatus = 'SYNCED' AND timestamp < :timestamp")
    suspend fun deleteSyncedOldTelemetry(timestamp: Long)
    
    @Query("DELETE FROM telemetry")
    suspend fun deleteAllTelemetry()
    
    // Aggregation queries for analytics
    @Query("""
        SELECT sensorType, AVG(value) as avgValue, MIN(value) as minValue, MAX(value) as maxValue, COUNT(*) as count
        FROM telemetry 
        WHERE deviceId = :deviceId AND timestamp BETWEEN :startTime AND :endTime 
        GROUP BY sensorType
    """)
    suspend fun getTelemetryStatsByDevice(deviceId: String, startTime: Long, endTime: Long): List<TelemetryStats>
    
    @Query("""
        SELECT 
            strftime('%Y-%m-%d %H:00:00', datetime(timestamp/1000, 'unixepoch')) as hour,
            AVG(value) as avgValue,
            MIN(value) as minValue,
            MAX(value) as maxValue,
            COUNT(*) as count
        FROM telemetry 
        WHERE deviceId = :deviceId AND sensorType = :sensorType AND timestamp BETWEEN :startTime AND :endTime 
        GROUP BY hour
        ORDER BY hour
    """)
    suspend fun getHourlyTelemetryStats(deviceId: String, sensorType: String, startTime: Long, endTime: Long): List<HourlyTelemetryStats>
}

data class TelemetryStats(
    val sensorType: String,
    val avgValue: Double,
    val minValue: Double,
    val maxValue: Double,
    val count: Int
)

data class HourlyTelemetryStats(
    val hour: String,
    val avgValue: Double,
    val minValue: Double,
    val maxValue: Double,
    val count: Int
)