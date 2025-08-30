package com.iotlogic.blynk.data.repository

import com.iotlogic.blynk.data.local.dao.DeviceDao
import com.iotlogic.blynk.data.local.entities.DeviceEntity
import com.iotlogic.blynk.data.remote.ApiClient
import com.iotlogic.blynk.data.remote.DeviceDto
import com.iotlogic.blynk.domain.model.Device
import com.iotlogic.blynk.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepositoryImpl @Inject constructor(
    private val deviceDao: DeviceDao,
    private val apiClient: ApiClient
) : DeviceRepository {
    
    override fun getDevices(): Flow<List<Device>> {
        return deviceDao.getAllDevices().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getDeviceById(deviceId: String): Flow<Device?> {
        return deviceDao.observeDeviceById(deviceId).map { entity ->
            entity?.toDomain()
        }
    }
    
    override fun getDevicesByProtocol(protocol: String): Flow<List<Device>> {
        return deviceDao.getDevicesByProtocol(protocol).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getOnlineDevices(): Flow<List<Device>> {
        return deviceDao.getOnlineDevices().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun searchDevices(query: String): Flow<List<Device>> {
        return deviceDao.searchDevices(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun addDevice(device: Device): Result<Device> {
        return try {
            // Save to local database first
            val entity = device.toEntity()
            deviceDao.insertDevice(entity)
            
            // Sync with backend if possible
            // Note: This would typically include authentication token
            // For now, we'll just return success
            Result.success(device)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateDevice(device: Device): Result<Device> {
        return try {
            val entity = device.toEntity()
            deviceDao.updateDevice(entity)
            Result.success(device)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteDevice(deviceId: String): Result<Unit> {
        return try {
            deviceDao.deleteDeviceById(deviceId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun syncWithBackend(token: String): Result<Unit> {
        return try {
            val response = apiClient.getDevices(token)
            if (response.isSuccess) {
                val devicesResponse = response.getOrThrow()
                if (devicesResponse.success) {
                    val entities = devicesResponse.devices.map { it.toEntity() }
                    deviceDao.insertDevices(entities)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(devicesResponse.message))
                }
            } else {
                Result.failure(response.exceptionOrNull() ?: Exception("Sync failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateDeviceStatus(deviceId: String, status: String): Result<Unit> {
        return try {
            deviceDao.updateDeviceStatus(deviceId, status)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateDeviceOnlineStatus(deviceId: String, isOnline: Boolean): Result<Unit> {
        return try {
            val lastSeen = System.currentTimeMillis()
            deviceDao.updateDeviceOnlineStatus(deviceId, isOnline, lastSeen)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateSignalStrength(deviceId: String, signalStrength: Int, quality: String): Result<Unit> {
        return try {
            deviceDao.updateSignalStrength(deviceId, signalStrength, quality)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getDeviceCount(): Int {
        return deviceDao.getDeviceCount()
    }
    
    override suspend fun getDeviceCountByProtocol(protocol: String): Int {
        return deviceDao.getDeviceCountByProtocol(protocol)
    }
}

// Extension functions for mapping between data layer and domain layer
private fun DeviceEntity.toDomain(): Device {
    return Device(
        id = id,
        name = name,
        type = type,
        protocol = protocol,
        status = status,
        token = token,
        userId = userId,
        macAddress = macAddress,
        ipAddress = ipAddress,
        port = port,
        lastSeen = lastSeen,
        batteryLevel = batteryLevel,
        signalStrength = signalStrength,
        firmwareVersion = firmwareVersion,
        hardwareVersion = hardwareVersion,
        manufacturer = manufacturer,
        modelNumber = modelNumber,
        serialNumber = serialNumber,
        location = location,
        latitude = latitude,
        longitude = longitude,
        configuration = configuration,
        metadata = metadata,
        isOnline = isOnline,
        connectionQuality = connectionQuality,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

private fun Device.toEntity(): DeviceEntity {
    return DeviceEntity(
        id = id,
        name = name,
        type = type,
        protocol = protocol,
        status = status,
        token = token,
        userId = userId,
        macAddress = macAddress,
        ipAddress = ipAddress,
        port = port,
        lastSeen = lastSeen,
        batteryLevel = batteryLevel,
        signalStrength = signalStrength,
        firmwareVersion = firmwareVersion,
        hardwareVersion = hardwareVersion,
        manufacturer = manufacturer,
        modelNumber = modelNumber,
        serialNumber = serialNumber,
        location = location,
        latitude = latitude,
        longitude = longitude,
        configuration = configuration,
        metadata = metadata,
        isOnline = isOnline,
        connectionQuality = connectionQuality,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

private fun DeviceDto.toEntity(): DeviceEntity {
    return DeviceEntity(
        id = id,
        name = name,
        type = type,
        protocol = protocol,
        status = status,
        token = token,
        userId = userId,
        macAddress = macAddress,
        ipAddress = ipAddress,
        lastSeen = parseTimestamp(lastSeen),
        batteryLevel = batteryLevel,
        signalStrength = signalStrength,
        location = location,
        isOnline = isOnline,
        createdAt = parseTimestamp(createdAt),
        updatedAt = parseTimestamp(updatedAt)
    )
}

private fun parseTimestamp(timestamp: String): Long {
    // This would parse the timestamp from the backend format
    // For now, return current time
    return System.currentTimeMillis()
}