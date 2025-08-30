package com.iotlogic.blynk.domain.repository

import com.iotlogic.blynk.domain.model.Telemetry
import kotlinx.coroutines.flow.Flow

interface TelemetryRepository {
    
    // Observation methods
    fun getRecentTelemetry(limit: Int = 100): Flow<List<Telemetry>>
    fun getTelemetryByDevice(deviceId: String, limit: Int = 100): Flow<List<Telemetry>>
    fun getTelemetryByDeviceAndSensor(deviceId: String, sensorType: String, limit: Int = 100): Flow<List<Telemetry>>
    fun getTelemetryByTimeRange(deviceId: String, startTime: Long, endTime: Long): Flow<List<Telemetry>>
    
    // Single item queries
    suspend fun getLatestTelemetryForDevice(deviceId: String): Telemetry?
    suspend fun getLatestTelemetryForSensor(deviceId: String, sensorType: String): Telemetry?
    
    // Analytics queries
    suspend fun getAverageTelemetryValue(deviceId: String, sensorType: String, startTime: Long, endTime: Long): Double?
    suspend fun getMinTelemetryValue(deviceId: String, sensorType: String, startTime: Long, endTime: Long): Double?
    suspend fun getMaxTelemetryValue(deviceId: String, sensorType: String, startTime: Long, endTime: Long): Double?
    
    // CRUD operations
    suspend fun storeTelemetryData(telemetry: Telemetry): Result<Unit>
    suspend fun storeTelemetryBatch(telemetryList: List<Telemetry>): Result<Unit>
    suspend fun deleteTelemetryByDevice(deviceId: String): Result<Unit>
    suspend fun deleteOldTelemetry(timestamp: Long): Result<Unit>
    
    // Sync operations
    suspend fun syncTelemetryData(token: String): Result<Unit>
    suspend fun uploadPendingTelemetry(token: String): Result<Unit>
    suspend fun markTelemetryAsSynced(telemetryIds: List<String>): Result<Unit>
    
    // Metadata queries
    suspend fun getSensorTypesForDevice(deviceId: String): List<String>
    suspend fun getTelemetryCountForDevice(deviceId: String): Int
    suspend fun getTelemetryCountSince(deviceId: String, timestamp: Long): Int
}