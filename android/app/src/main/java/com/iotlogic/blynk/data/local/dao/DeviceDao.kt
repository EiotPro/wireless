package com.iotlogic.blynk.data.local.dao

import androidx.room.*
import com.iotlogic.blynk.data.local.entities.DeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    
    @Query("SELECT * FROM devices ORDER BY name ASC")
    fun getAllDevices(): Flow<List<DeviceEntity>>
    
    @Query("SELECT * FROM devices WHERE id = :deviceId")
    suspend fun getDeviceById(deviceId: String): DeviceEntity?
    
    @Query("SELECT * FROM devices WHERE id = :deviceId")
    fun observeDeviceById(deviceId: String): Flow<DeviceEntity?>
    
    @Query("SELECT * FROM devices WHERE token = :token")
    suspend fun getDeviceByToken(token: String): DeviceEntity?
    
    @Query("SELECT * FROM devices WHERE userId = :userId ORDER BY name ASC")
    fun getDevicesByUser(userId: String): Flow<List<DeviceEntity>>
    
    @Query("SELECT * FROM devices WHERE protocol = :protocol ORDER BY name ASC")
    fun getDevicesByProtocol(protocol: String): Flow<List<DeviceEntity>>
    
    @Query("SELECT * FROM devices WHERE status = :status ORDER BY lastSeen DESC")
    fun getDevicesByStatus(status: String): Flow<List<DeviceEntity>>
    
    @Query("SELECT * FROM devices WHERE isOnline = 1 ORDER BY lastSeen DESC")
    fun getOnlineDevices(): Flow<List<DeviceEntity>>
    
    @Query("SELECT * FROM devices WHERE isOnline = 0 ORDER BY lastSeen DESC")
    fun getOfflineDevices(): Flow<List<DeviceEntity>>
    
    @Query("SELECT * FROM devices WHERE lastSeen > :timestamp ORDER BY lastSeen DESC")
    fun getRecentlyActiveDevices(timestamp: Long): Flow<List<DeviceEntity>>
    
    @Query("SELECT * FROM devices WHERE name LIKE '%' || :query || '%' OR type LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchDevices(query: String): Flow<List<DeviceEntity>>
    
    @Query("SELECT * FROM devices WHERE latitude IS NOT NULL AND longitude IS NOT NULL")
    fun getDevicesWithLocation(): Flow<List<DeviceEntity>>
    
    @Query("SELECT COUNT(*) FROM devices")
    suspend fun getDeviceCount(): Int
    
    @Query("SELECT COUNT(*) FROM devices WHERE status = :status")
    suspend fun getDeviceCountByStatus(status: String): Int
    
    @Query("SELECT COUNT(*) FROM devices WHERE protocol = :protocol")
    suspend fun getDeviceCountByProtocol(protocol: String): Int
    
    @Query("SELECT DISTINCT protocol FROM devices")
    suspend fun getUsedProtocols(): List<String>
    
    @Query("SELECT DISTINCT type FROM devices")
    suspend fun getDeviceTypes(): List<String>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: DeviceEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<DeviceEntity>)
    
    @Update
    suspend fun updateDevice(device: DeviceEntity)
    
    @Query("UPDATE devices SET status = :status, updatedAt = :timestamp WHERE id = :deviceId")
    suspend fun updateDeviceStatus(deviceId: String, status: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE devices SET isOnline = :isOnline, lastSeen = :lastSeen, updatedAt = :timestamp WHERE id = :deviceId")
    suspend fun updateDeviceOnlineStatus(
        deviceId: String, 
        isOnline: Boolean, 
        lastSeen: Long, 
        timestamp: Long = System.currentTimeMillis()
    )
    
    @Query("UPDATE devices SET batteryLevel = :batteryLevel, updatedAt = :timestamp WHERE id = :deviceId")
    suspend fun updateBatteryLevel(deviceId: String, batteryLevel: Int, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE devices SET signalStrength = :signalStrength, connectionQuality = :quality, updatedAt = :timestamp WHERE id = :deviceId")
    suspend fun updateSignalStrength(
        deviceId: String, 
        signalStrength: Int, 
        quality: String, 
        timestamp: Long = System.currentTimeMillis()
    )
    
    @Query("UPDATE devices SET latitude = :latitude, longitude = :longitude, location = :location, updatedAt = :timestamp WHERE id = :deviceId")
    suspend fun updateDeviceLocation(
        deviceId: String, 
        latitude: Double?, 
        longitude: Double?, 
        location: String?, 
        timestamp: Long = System.currentTimeMillis()
    )
    
    @Delete
    suspend fun deleteDevice(device: DeviceEntity)
    
    @Query("DELETE FROM devices WHERE id = :deviceId")
    suspend fun deleteDeviceById(deviceId: String)
    
    @Query("DELETE FROM devices WHERE userId = :userId")
    suspend fun deleteDevicesByUser(userId: String)
    
    @Query("DELETE FROM devices WHERE lastSeen < :timestamp AND isOnline = 0")
    suspend fun deleteInactiveDevices(timestamp: Long)
    
    @Query("DELETE FROM devices")
    suspend fun deleteAllDevices()
}