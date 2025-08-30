package com.iotlogic.blynk.data.sync

import android.content.Context
import androidx.work.*
import com.iotlogic.blynk.data.local.dao.CommandQueueDao
import com.iotlogic.blynk.data.local.dao.DeviceDao
import com.iotlogic.blynk.data.local.dao.TelemetryDao
import com.iotlogic.blynk.data.local.entities.CommandQueueEntity
import com.iotlogic.blynk.data.local.preferences.AuthPreferences
import com.iotlogic.blynk.data.remote.ApiService
import com.iotlogic.blynk.data.remote.UpdateDeviceRequest
import com.iotlogic.blynk.data.remote.SubmitTelemetryBatchRequest
import com.iotlogic.blynk.data.remote.TelemetryDataItem
import com.iotlogic.blynk.domain.model.SyncStatus
import com.iotlogic.blynk.hardware.HardwareManager
import com.iotlogic.blynk.utils.NetworkUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val commandQueueDao: CommandQueueDao,
    private val deviceDao: DeviceDao,
    private val telemetryDao: TelemetryDao,
    private val apiService: ApiService,
    private val hardwareManager: HardwareManager,
    private val networkUtils: NetworkUtils,
    private val authPreferences: AuthPreferences
) {
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val workManager = WorkManager.getInstance(context)
    
    // Sync state
    private val _syncStatus = MutableStateFlow(SyncStatus.IDLE)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()
    
    private val _pendingCommandCount = MutableStateFlow(0)
    val pendingCommandCount: StateFlow<Int> = _pendingCommandCount.asStateFlow()
    
    private val _lastSyncTime = MutableStateFlow<Long?>(null)
    val lastSyncTime: StateFlow<Long?> = _lastSyncTime.asStateFlow()
    
    // Auto-sync configuration
    private var autoSyncEnabled = true
    private var syncIntervalMinutes = 15L
    
    init {
        // Monitor pending commands
        monitorPendingCommands()
        
        // Schedule periodic sync
        schedulePeriodicSync()
        
        // Monitor network connectivity for auto-sync
        monitorNetworkConnectivity()
    }
    
    /**
     * Start comprehensive synchronization
     */
    suspend fun startFullSync(token: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                _syncStatus.value = SyncStatus.SYNCING
                
                // 1. Process pending commands
                val commandResult = processPendingCommands(token)
                if (commandResult.isFailure) {
                    _syncStatus.value = SyncStatus.ERROR
                    return@withContext commandResult
                }
                
                // 2. Sync telemetry data
                val telemetryResult = syncTelemetryData(token)
                if (telemetryResult.isFailure) {
                    _syncStatus.value = SyncStatus.ERROR
                    return@withContext telemetryResult
                }
                
                // 3. Sync device configurations
                val configResult = syncDeviceConfigurations(token)
                if (configResult.isFailure) {
                    _syncStatus.value = SyncStatus.ERROR
                    return@withContext configResult
                }
                
                // 4. Clean up old data
                cleanupOldData()
                
                _lastSyncTime.value = System.currentTimeMillis()
                _syncStatus.value = SyncStatus.COMPLETED
                
                Result.success(Unit)
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.ERROR
                Result.failure(e)
            }
        }
    }
    
    /**
     * Process pending commands in the queue
     */
    suspend fun processPendingCommands(token: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val readyCommands = commandQueueDao.getReadyCommands(limit = 50)
                
                if (readyCommands.isEmpty()) {
                    return@withContext Result.success(Unit)
                }
                
                var successCount = 0
                var failureCount = 0
                
                readyCommands.forEach { command ->
                    try {
                        commandQueueDao.markCommandAsSent(command.id)
                        
                        val result = executeCommand(command, token)
                        
                        if (result.isSuccess) {
                            commandQueueDao.markCommandAsCompleted(
                                commandId = command.id,
                                result = result.getOrNull()
                            )
                            successCount++
                        } else {
                            val errorMessage = result.exceptionOrNull()?.message
                            
                            if (command.retryCount < command.maxRetries) {
                                commandQueueDao.markCommandAsFailed(
                                    commandId = command.id,
                                    errorMessage = errorMessage
                                )
                            } else {
                                // Max retries reached, mark as failed permanently
                                commandQueueDao.updateCommand(
                                    command.copy(
                                        status = "FAILED",
                                        errorMessage = "Max retries exceeded: $errorMessage"
                                    )
                                )
                            }
                            failureCount++
                        }
                    } catch (e: Exception) {
                        commandQueueDao.markCommandAsFailed(
                            commandId = command.id,
                            errorMessage = e.message
                        )
                        failureCount++
                    }
                }
                
                if (failureCount == 0) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Command processing completed with $failureCount failures out of ${readyCommands.size} commands"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Execute a single command
     */
    private suspend fun executeCommand(command: CommandQueueEntity, token: String): Result<String> {
        return try {
            when (command.command) {
                "device_control" -> {
                    val deviceId = command.parameters["deviceId"] as? String
                        ?: return Result.failure(Exception("Device ID not found in command parameters"))
                    
                    val action = command.parameters["action"] as? String
                        ?: return Result.failure(Exception("Action not found in command parameters"))
                    
                    val value = command.parameters["value"]
                    
                    // TODO: Implement hardware command execution through appropriate protocol manager
                    // val hardwareResult = hardwareManager.sendDeviceCommand(deviceId, action, value)
                    val hardwareResult = Result.success("Hardware command simulated")
                    
                    if (hardwareResult.isSuccess) {
                        // Also sync with backend
                        val apiResult = apiService.sendDeviceCommand(
                            token = "Bearer $token",
                            deviceId = deviceId,
                            command = mapOf(
                                "action" to action,
                                "value" to (value ?: ""),
                                "timestamp" to System.currentTimeMillis()
                            )
                        )
                        
                        if (apiResult.isSuccessful) {
                            Result.success("Command executed successfully")
                        } else {
                            Result.failure(Exception("API call failed: ${apiResult.message()}"))
                        }
                    } else {
                        Result.failure(hardwareResult.exceptionOrNull() ?: Exception("Hardware command failed"))
                    }
                }
                
                "configuration_update" -> {
                    val deviceId = command.parameters["deviceId"] as? String
                        ?: return Result.failure(Exception("Device ID not found"))
                    
                    val configurations = command.parameters["configurations"] as? Map<String, Any>
                        ?: return Result.failure(Exception("Configurations not found"))
                    
                    val response = apiService.updateDevice(
                        token = "Bearer $token",
                        request = UpdateDeviceRequest(
                            id = deviceId,
                            name = null,
                            type = null,
                            status = null,
                            configuration = configurations.mapValues { it.value.toString() }
                        )
                    )
                    
                    if (response.isSuccessful) {
                        Result.success("Configuration updated successfully")
                    } else {
                        Result.failure(Exception("Configuration update failed: ${response.message()}"))
                    }
                }
                
                "telemetry_sync" -> {
                    val telemetryData = command.parameters["telemetryData"] as? List<Map<String, Any>>
                        ?: return Result.failure(Exception("Telemetry data not found"))
                    
                    val response = apiService.submitTelemetryBatch(
                        token = "Bearer $token",
                        request = SubmitTelemetryBatchRequest(
                            deviceToken = "", // This should be obtained from device
                            telemetryData = telemetryData.map { data ->
                                TelemetryDataItem(
                                    sensorType = data["sensorType"] as? String ?: "",
                                    value = (data["value"] as? Number)?.toDouble() ?: 0.0,
                                    unit = data["unit"] as? String,
                                    timestamp = (data["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
                                )
                            }
                        )
                    )
                    
                    if (response.isSuccessful) {
                        Result.success("Telemetry synced successfully")
                    } else {
                        Result.failure(Exception("Telemetry sync failed: ${response.message()}"))
                    }
                }
                
                else -> Result.failure(Exception("Unknown command type: ${command.command}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Sync telemetry data with backend
     */
    private suspend fun syncTelemetryData(token: String): Result<Unit> {
        return try {
            val pendingTelemetry = telemetryDao.getPendingSyncTelemetry()
            
            if (pendingTelemetry.isNotEmpty()) {
                val batches = pendingTelemetry.chunked(100) // Process in batches of 100
                
                batches.forEach { batch ->
                    val telemetryData = batch.map { telemetry ->
                        mapOf(
                            "id" to telemetry.id,
                            "deviceId" to telemetry.deviceId,
                            "sensorType" to telemetry.sensorType,
                            "value" to telemetry.value,
                            "unit" to telemetry.unit,
                            "timestamp" to telemetry.timestamp,
                            "quality" to telemetry.quality,
                            "metadata" to telemetry.metadata
                        )
                    }
                    
                    val response = apiService.submitTelemetryBatch(
                        token = "Bearer $token",
                        request = SubmitTelemetryBatchRequest(
                            deviceToken = "", // This should be obtained from device
                            telemetryData = telemetryData.map { data ->
                                TelemetryDataItem(
                                    sensorType = data["sensorType"] as? String ?: "",
                                    value = (data["value"] as? Number)?.toDouble() ?: 0.0,
                                    unit = data["unit"] as? String,
                                    timestamp = (data["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
                                )
                            }
                        )
                    )
                    
                    if (response.isSuccessful) {
                        // Mark as synced
                        batch.forEach { telemetry ->
                            telemetryDao.updateTelemetry(
                                telemetry.copy(syncStatus = "SYNCED")
                            )
                        }
                    } else {
                        // Mark as failed
                        batch.forEach { telemetry ->
                            telemetryDao.updateTelemetry(
                                telemetry.copy(syncStatus = "FAILED")
                            )
                        }
                        return Result.failure(Exception("Telemetry sync failed: ${response.message()}"))
                    }
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Sync device configurations with backend
     */
    private suspend fun syncDeviceConfigurations(token: String): Result<Unit> {
        return try {
            val devices = deviceDao.getAllDevices()
            
            // TODO: Implement device configuration sync with backend
            // devices.collect { deviceList ->
            //     deviceList.forEach { device ->
            //         val response = apiService.getDeviceById(
            //             token = "Bearer $token",
            //             deviceId = device.id
            //         )
            //
            //         if (response.isSuccessful) {
            //             val serverDevice = response.body()?.device
            //             // Update local configurations with server data
            //             // Implementation would depend on your API response structure
            //         }
            //     }
            // }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Queue a command for offline execution
     */
    suspend fun queueCommand(
        deviceId: String,
        command: String,
        parameters: Map<String, Any>,
        priority: Int = 0,
        maxRetries: Int = 3,
        expirationTime: Long? = null
    ): Result<String> {
        return try {
            val commandId = "cmd_${System.currentTimeMillis()}_${(1000..9999).random()}"
            
            val commandEntity = CommandQueueEntity(
                id = commandId,
                deviceId = deviceId,
                command = command,
                parameters = parameters,
                priority = priority,
                maxRetries = maxRetries,
                expiresAt = expirationTime
            )
            
            commandQueueDao.insertCommand(commandEntity)
            
            // If online, try to execute immediately
            if (networkUtils.isNetworkAvailable()) {
                val token = authPreferences.getAuthToken()
                if (token != null) {
                    scope.launch {
                        // Small delay to ensure transaction is committed
                        delay(100)
                        processPendingCommands(token)
                    }
                }
            }
            
            Result.success(commandId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Cancel a queued command
     */
    suspend fun cancelCommand(commandId: String): Result<Unit> {
        return try {
            commandQueueDao.cancelCommand(commandId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Retry failed commands
     */
    suspend fun retryFailedCommands(): Result<Unit> {
        return try {
            val retryableCommands = commandQueueDao.getRetryableCommands()
            
            retryableCommands.forEach { command ->
                commandQueueDao.retryFailedCommand(command.id)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Clean up old completed and cancelled commands
     */
    private suspend fun cleanupOldData() {
        try {
            val cutoffTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7) // 7 days old
            
            // Clean up old commands
            commandQueueDao.cleanupOldCommands(cutoffTime)
            
            // Clean up expired commands
            commandQueueDao.deleteExpiredCommands()
            
            // Clean up old telemetry (keep only last 30 days)
            val telemetryCutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
            telemetryDao.deleteOldTelemetry(telemetryCutoff)
            
        } catch (e: Exception) {
            // Log error but don't fail the sync
        }
    }
    
    /**
     * Monitor pending commands count
     */
    private fun monitorPendingCommands() {
        scope.launch {
            while (isActive) {
                try {
                    val count = commandQueueDao.getPendingCommandCount()
                    _pendingCommandCount.value = count
                    delay(5000) // Check every 5 seconds
                } catch (e: Exception) {
                    delay(10000) // Wait longer on error
                }
            }
        }
    }
    
    /**
     * Schedule periodic background sync
     */
    private fun schedulePeriodicSync() {
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(syncIntervalMinutes, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 5, TimeUnit.MINUTES)
            .addTag("periodic_sync")
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            "IoTLogicPeriodicSync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
    
    /**
     * Monitor network connectivity for auto-sync
     */
    private fun monitorNetworkConnectivity() {
        scope.launch {
            networkUtils.networkAvailability.collect { isAvailable ->
                if (isAvailable && autoSyncEnabled) {
                    delay(2000) // Wait a bit for connection to stabilize
                    // Trigger sync if there are pending commands
                    val pendingCount = commandQueueDao.getPendingCommandCount()
                    if (pendingCount > 0) {
                        val token = authPreferences.getAuthToken()
                        if (token != null) {
                            processPendingCommands(token)
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Get sync statistics
     */
    suspend fun getSyncStatistics(): SyncStatistics {
        return SyncStatistics(
            pendingCommands = commandQueueDao.getPendingCommandCount(),
            retryableCommands = commandQueueDao.getRetryableCommandCount(),
            lastSyncTime = _lastSyncTime.value,
            currentStatus = _syncStatus.value,
            averageExecutionTime = commandQueueDao.getAverageExecutionTime()
        )
    }
    
    /**
     * Configure auto-sync settings
     */
    fun configureAutoSync(enabled: Boolean, intervalMinutes: Long = 15L) {
        autoSyncEnabled = enabled
        syncIntervalMinutes = intervalMinutes
        
        if (enabled) {
            schedulePeriodicSync()
        } else {
            workManager.cancelUniqueWork("IoTLogicPeriodicSync")
        }
    }
}

/**
 * Sync statistics data class
 */
data class SyncStatistics(
    val pendingCommands: Int,
    val retryableCommands: Int,
    val lastSyncTime: Long?,
    val currentStatus: SyncStatus,
    val averageExecutionTime: Double?
)