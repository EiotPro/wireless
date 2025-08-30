package com.iotlogic.blynk.domain.repository

import com.iotlogic.blynk.domain.model.Device
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    
    // Observation methods
    fun getDevices(): Flow<List<Device>>
    fun getDeviceById(deviceId: String): Flow<Device?>
    fun getDevicesByProtocol(protocol: String): Flow<List<Device>>
    fun getOnlineDevices(): Flow<List<Device>>
    fun searchDevices(query: String): Flow<List<Device>>
    
    // CRUD operations
    suspend fun addDevice(device: Device): Result<Device>
    suspend fun updateDevice(device: Device): Result<Device>
    suspend fun deleteDevice(deviceId: String): Result<Unit>
    
    // Sync operations
    suspend fun syncWithBackend(token: String): Result<Unit>
    
    // Status updates
    suspend fun updateDeviceStatus(deviceId: String, status: String): Result<Unit>
    suspend fun updateDeviceOnlineStatus(deviceId: String, isOnline: Boolean): Result<Unit>
    suspend fun updateSignalStrength(deviceId: String, signalStrength: Int, quality: String): Result<Unit>
    
    // Statistics
    suspend fun getDeviceCount(): Int
    suspend fun getDeviceCountByProtocol(protocol: String): Int
}