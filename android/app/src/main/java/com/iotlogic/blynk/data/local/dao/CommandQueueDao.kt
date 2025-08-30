package com.iotlogic.blynk.data.local.dao

import androidx.room.*
import com.iotlogic.blynk.data.local.entities.CommandQueueEntity
import kotlinx.coroutines.flow.Flow

data class CommandStatistics(
    val totalCommands: Int,
    val status: String
)

@Dao
interface CommandQueueDao {
    
    @Query("SELECT * FROM command_queue WHERE status = 'PENDING' ORDER BY priority DESC, createdAt ASC")
    fun getPendingCommands(): Flow<List<CommandQueueEntity>>
    
    @Query("SELECT * FROM command_queue WHERE deviceId = :deviceId ORDER BY priority DESC, createdAt ASC")
    fun getCommandsForDevice(deviceId: String): Flow<List<CommandQueueEntity>>
    
    @Query("SELECT * FROM command_queue WHERE status = :status ORDER BY priority DESC, createdAt ASC")
    fun getCommandsByStatus(status: String): Flow<List<CommandQueueEntity>>
    
    @Query("SELECT * FROM command_queue WHERE id = :commandId")
    suspend fun getCommandById(commandId: String): CommandQueueEntity?
    
    @Query("SELECT * FROM command_queue WHERE status = 'PENDING' AND (scheduledAt IS NULL OR scheduledAt <= :currentTime) ORDER BY priority DESC, createdAt ASC LIMIT :limit")
    suspend fun getReadyCommands(currentTime: Long = System.currentTimeMillis(), limit: Int = 10): List<CommandQueueEntity>
    
    @Query("SELECT * FROM command_queue WHERE status = 'FAILED' AND retryCount < maxRetries ORDER BY priority DESC, createdAt ASC")
    suspend fun getRetryableCommands(): List<CommandQueueEntity>
    
    @Query("SELECT * FROM command_queue WHERE expiresAt IS NOT NULL AND expiresAt < :currentTime")
    suspend fun getExpiredCommands(currentTime: Long = System.currentTimeMillis()): List<CommandQueueEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommand(command: CommandQueueEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommands(commands: List<CommandQueueEntity>)
    
    @Update
    suspend fun updateCommand(command: CommandQueueEntity)
    
    @Update
    suspend fun updateCommands(commands: List<CommandQueueEntity>)
    
    @Delete
    suspend fun deleteCommand(command: CommandQueueEntity)
    
    @Query("DELETE FROM command_queue WHERE id = :commandId")
    suspend fun deleteCommandById(commandId: String)
    
    @Query("DELETE FROM command_queue WHERE deviceId = :deviceId")
    suspend fun deleteCommandsForDevice(deviceId: String)
    
    @Query("DELETE FROM command_queue WHERE status = :status")
    suspend fun deleteCommandsByStatus(status: String)
    
    @Query("DELETE FROM command_queue WHERE expiresAt IS NOT NULL AND expiresAt < :currentTime")
    suspend fun deleteExpiredCommands(currentTime: Long = System.currentTimeMillis()): Int
    
    @Query("UPDATE command_queue SET status = :newStatus WHERE id = :commandId")
    suspend fun updateCommandStatus(commandId: String, newStatus: String)
    
    @Query("UPDATE command_queue SET status = :newStatus, sentAt = :sentAt WHERE id = :commandId")
    suspend fun markCommandAsSent(commandId: String, newStatus: String = "SENT", sentAt: Long = System.currentTimeMillis())
    
    @Query("UPDATE command_queue SET status = :newStatus, completedAt = :completedAt, result = :result WHERE id = :commandId")
    suspend fun markCommandAsCompleted(
        commandId: String, 
        newStatus: String = "COMPLETED", 
        completedAt: Long = System.currentTimeMillis(),
        result: String? = null
    )
    
    @Query("UPDATE command_queue SET status = :newStatus, retryCount = retryCount + 1, errorMessage = :errorMessage WHERE id = :commandId")
    suspend fun markCommandAsFailed(commandId: String, newStatus: String = "FAILED", errorMessage: String? = null)
    
    @Query("UPDATE command_queue SET status = 'CANCELLED' WHERE id = :commandId")
    suspend fun cancelCommand(commandId: String)
    
    @Query("UPDATE command_queue SET status = 'CANCELLED' WHERE deviceId = :deviceId AND status = 'PENDING'")
    suspend fun cancelPendingCommandsForDevice(deviceId: String)
    
    @Query("SELECT COUNT(*) FROM command_queue WHERE status = 'PENDING'")
    suspend fun getPendingCommandCount(): Int
    
    @Query("SELECT COUNT(*) FROM command_queue WHERE deviceId = :deviceId AND status = 'PENDING'")
    suspend fun getPendingCommandCountForDevice(deviceId: String): Int
    
    @Query("SELECT COUNT(*) FROM command_queue WHERE status = 'FAILED' AND retryCount < maxRetries")
    suspend fun getRetryableCommandCount(): Int
    
    @Query("SELECT AVG(completedAt - createdAt) FROM command_queue WHERE status = 'COMPLETED' AND completedAt IS NOT NULL")
    suspend fun getAverageExecutionTime(): Double?
    
    @Query("SELECT COUNT(*) as totalCommands, status FROM command_queue GROUP BY status")
    suspend fun getCommandStatistics(): List<CommandStatistics>
    
    @Query("""
        SELECT * FROM command_queue 
        WHERE deviceId = :deviceId 
        AND status IN ('PENDING', 'SENT') 
        ORDER BY priority DESC, createdAt ASC
    """)
    suspend fun getActiveCommandsForDevice(deviceId: String): List<CommandQueueEntity>
    
    @Query("""
        SELECT * FROM command_queue 
        WHERE createdAt BETWEEN :startTime AND :endTime 
        ORDER BY createdAt DESC
    """)
    suspend fun getCommandsInTimeRange(startTime: Long, endTime: Long): List<CommandQueueEntity>
    
    @Query("SELECT DISTINCT deviceId FROM command_queue WHERE status = 'PENDING'")
    suspend fun getDevicesWithPendingCommands(): List<String>
    
    @Query("""
        UPDATE command_queue 
        SET priority = :newPriority 
        WHERE id = :commandId
    """)
    suspend fun updateCommandPriority(commandId: String, newPriority: Int)
    
    @Query("""
        UPDATE command_queue 
        SET scheduledAt = :scheduledAt 
        WHERE id = :commandId
    """)
    suspend fun scheduleCommand(commandId: String, scheduledAt: Long)
    
    @Query("""
        DELETE FROM command_queue 
        WHERE status IN ('COMPLETED', 'CANCELLED') 
        AND completedAt < :beforeTime
    """)
    suspend fun cleanupOldCommands(beforeTime: Long): Int
    
    @Transaction
    suspend fun retryFailedCommand(commandId: String) {
        val command = getCommandById(commandId)
        if (command != null && command.retryCount < command.maxRetries) {
            updateCommand(
                command.copy(
                    status = "PENDING",
                    retryCount = command.retryCount + 1,
                    errorMessage = null
                )
            )
        }
    }
    
    @Transaction
    suspend fun batchUpdateCommandStatus(commandIds: List<String>, newStatus: String) {
        commandIds.forEach { commandId ->
            updateCommandStatus(commandId, newStatus)
        }
    }
    
    @Query("""
        SELECT * FROM command_queue 
        WHERE status = 'PENDING' 
        AND deviceId = :deviceId 
        ORDER BY priority DESC, createdAt ASC 
        LIMIT 1
    """)
    suspend fun getNextCommandForDevice(deviceId: String): CommandQueueEntity?
    
    @Query("SELECT MAX(priority) FROM command_queue WHERE deviceId = :deviceId")
    suspend fun getMaxPriorityForDevice(deviceId: String): Int?
}