package com.iotlogic.blynk.data.sync

import android.content.Context
import androidx.work.WorkManager
import com.iotlogic.blynk.data.local.dao.CommandQueueDao
import com.iotlogic.blynk.data.local.dao.DeviceDao
import com.iotlogic.blynk.data.local.dao.TelemetryDao
import com.iotlogic.blynk.data.local.entities.CommandQueueEntity
import com.iotlogic.blynk.data.local.entities.TelemetryEntity
import com.iotlogic.blynk.data.remote.api.IoTLogicApiService
import com.iotlogic.blynk.domain.model.SyncStatus
import com.iotlogic.blynk.hardware.HardwareManager
import com.iotlogic.blynk.utils.NetworkUtils
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SyncManagerTest {

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var commandQueueDao: CommandQueueDao

    @MockK
    private lateinit var deviceDao: DeviceDao

    @MockK
    private lateinit var telemetryDao: TelemetryDao

    @MockK
    private lateinit var apiService: IoTLogicApiService

    @MockK
    private lateinit var hardwareManager: HardwareManager

    @MockK
    private lateinit var networkUtils: NetworkUtils

    @MockK
    private lateinit var workManager: WorkManager

    private lateinit var syncManager: SyncManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        
        // Mock WorkManager
        mockkStatic(WorkManager::class)
        every { WorkManager.getInstance(context) } returns workManager
        every { workManager.enqueueUniquePeriodicWork(any(), any(), any()) } returns mockk()

        // Mock NetworkUtils
        every { networkUtils.networkAvailability } returns flowOf(true)
        every { networkUtils.isNetworkAvailable() } returns true

        // Mock DAOs with default responses
        every { commandQueueDao.getPendingCommandCount() } returns 0
        every { commandQueueDao.getRetryableCommandCount() } returns 0
        every { commandQueueDao.getAverageExecutionTime() } returns 1000.0

        syncManager = SyncManager(
            context = context,
            commandQueueDao = commandQueueDao,
            deviceDao = deviceDao,
            telemetryDao = telemetryDao,
            apiService = apiService,
            hardwareManager = hardwareManager,
            networkUtils = networkUtils
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `startFullSync should complete successfully when all operations succeed`() = runTest {
        // Given
        val token = "access_token"
        
        every { commandQueueDao.getReadyCommands(any()) } returns emptyList()
        every { telemetryDao.getPendingSyncTelemetry() } returns emptyList()
        every { deviceDao.getAllDevices() } returns emptyList()
        every { commandQueueDao.cleanupOldCommands(any()) } just Runs
        every { commandQueueDao.deleteExpiredCommands() } just Runs
        every { telemetryDao.deleteOldTelemetry(any()) } just Runs

        // When
        val result = syncManager.startFullSync(token)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(SyncStatus.COMPLETED, syncManager.syncStatus.value)
    }

    @Test
    fun `processPendingCommands should execute device control commands`() = runTest {
        // Given
        val token = "access_token"
        val command = CommandQueueEntity(
            id = "cmd123",
            deviceId = "device123",
            command = "device_control",
            parameters = mapOf(
                "deviceId" to "device123",
                "action" to "turn_on",
                "value" to true
            )
        )

        every { commandQueueDao.getReadyCommands(any()) } returns listOf(command)
        every { commandQueueDao.markCommandAsSent(any()) } just Runs
        every { commandQueueDao.markCommandAsCompleted(any(), any()) } just Runs
        every { hardwareManager.sendDeviceCommand(any(), any(), any()) } returns Result.success("OK")
        every { apiService.sendDeviceCommand(any(), any(), any()) } returns mockk {
            every { isSuccessful } returns true
        }

        // When
        val result = syncManager.processPendingCommands(token)

        // Then
        assertTrue(result.isSuccess)
        verify { commandQueueDao.markCommandAsSent("cmd123") }
        verify { commandQueueDao.markCommandAsCompleted("cmd123", any()) }
        verify { hardwareManager.sendDeviceCommand("device123", "turn_on", true) }
    }

    @Test
    fun `processPendingCommands should handle hardware command failure`() = runTest {
        // Given
        val token = "access_token"
        val command = CommandQueueEntity(
            id = "cmd123",
            deviceId = "device123",
            command = "device_control",
            parameters = mapOf(
                "deviceId" to "device123",
                "action" to "turn_on",
                "value" to true
            ),
            retryCount = 0,
            maxRetries = 3
        )

        every { commandQueueDao.getReadyCommands(any()) } returns listOf(command)
        every { commandQueueDao.markCommandAsSent(any()) } just Runs
        every { commandQueueDao.markCommandAsFailed(any(), any()) } just Runs
        every { hardwareManager.sendDeviceCommand(any(), any(), any()) } returns Result.failure(Exception("Hardware error"))

        // When
        val result = syncManager.processPendingCommands(token)

        // Then
        assertTrue(result.isFailure)
        verify { commandQueueDao.markCommandAsFailed("cmd123", "Hardware error") }
    }

    @Test
    fun `processPendingCommands should mark as permanently failed after max retries`() = runTest {
        // Given
        val token = "access_token"
        val command = CommandQueueEntity(
            id = "cmd123",
            deviceId = "device123",
            command = "device_control",
            parameters = mapOf(
                "deviceId" to "device123",
                "action" to "turn_on",
                "value" to true
            ),
            retryCount = 3,
            maxRetries = 3
        )

        every { commandQueueDao.getReadyCommands(any()) } returns listOf(command)
        every { commandQueueDao.markCommandAsSent(any()) } just Runs
        every { commandQueueDao.updateCommand(any()) } just Runs
        every { hardwareManager.sendDeviceCommand(any(), any(), any()) } returns Result.failure(Exception("Hardware error"))

        // When
        val result = syncManager.processPendingCommands(token)

        // Then
        assertTrue(result.isFailure)
        verify { 
            commandQueueDao.updateCommand(
                command.copy(
                    status = "FAILED",
                    errorMessage = "Max retries exceeded: Hardware error"
                )
            )
        }
    }

    @Test
    fun `processPendingCommands should handle configuration update commands`() = runTest {
        // Given
        val token = "access_token"
        val command = CommandQueueEntity(
            id = "cmd123",
            deviceId = "device123",
            command = "configuration_update",
            parameters = mapOf(
                "deviceId" to "device123",
                "configurations" to mapOf("setting1" to "value1")
            )
        )

        every { commandQueueDao.getReadyCommands(any()) } returns listOf(command)
        every { commandQueueDao.markCommandAsSent(any()) } just Runs
        every { commandQueueDao.markCommandAsCompleted(any(), any()) } just Runs
        every { apiService.updateDeviceConfiguration(any(), any(), any()) } returns mockk {
            every { isSuccessful } returns true
        }

        // When
        val result = syncManager.processPendingCommands(token)

        // Then
        assertTrue(result.isSuccess)
        verify { 
            apiService.updateDeviceConfiguration(
                "Bearer $token",
                "device123",
                mapOf("setting1" to "value1")
            )
        }
    }

    @Test
    fun `queueCommand should create and insert command entity`() = runTest {
        // Given
        val deviceId = "device123"
        val command = "device_control"
        val parameters = mapOf("action" to "turn_on")
        val priority = 1

        every { commandQueueDao.insertCommand(any()) } just Runs
        every { networkUtils.isNetworkAvailable() } returns false

        // When
        val result = syncManager.queueCommand(deviceId, command, parameters, priority)

        // Then
        assertTrue(result.isSuccess)
        verify { commandQueueDao.insertCommand(any()) }
    }

    @Test
    fun `queueCommand should attempt immediate execution when online`() = runTest {
        // Given
        val deviceId = "device123"
        val command = "device_control"
        val parameters = mapOf("action" to "turn_on")

        every { commandQueueDao.insertCommand(any()) } just Runs
        every { networkUtils.isNetworkAvailable() } returns true
        every { commandQueueDao.getReadyCommands(any()) } returns emptyList()

        // When
        val result = syncManager.queueCommand(deviceId, command, parameters)

        // Then
        assertTrue(result.isSuccess)
        verify { commandQueueDao.insertCommand(any()) }
    }

    @Test
    fun `cancelCommand should cancel queued command`() = runTest {
        // Given
        val commandId = "cmd123"

        every { commandQueueDao.cancelCommand(commandId) } just Runs

        // When
        val result = syncManager.cancelCommand(commandId)

        // Then
        assertTrue(result.isSuccess)
        verify { commandQueueDao.cancelCommand(commandId) }
    }

    @Test
    fun `retryFailedCommands should retry all retryable commands`() = runTest {
        // Given
        val command1 = CommandQueueEntity(id = "cmd1", deviceId = "device1", command = "test")
        val command2 = CommandQueueEntity(id = "cmd2", deviceId = "device2", command = "test")

        every { commandQueueDao.getRetryableCommands() } returns listOf(command1, command2)
        every { commandQueueDao.retryFailedCommand(any()) } just Runs

        // When
        val result = syncManager.retryFailedCommands()

        // Then
        assertTrue(result.isSuccess)
        verify { commandQueueDao.retryFailedCommand("cmd1") }
        verify { commandQueueDao.retryFailedCommand("cmd2") }
    }

    @Test
    fun `getSyncStatistics should return current statistics`() = runTest {
        // Given
        every { commandQueueDao.getPendingCommandCount() } returns 5
        every { commandQueueDao.getRetryableCommandCount() } returns 2
        every { commandQueueDao.getAverageExecutionTime() } returns 1500.0

        // When
        val stats = syncManager.getSyncStatistics()

        // Then
        assertEquals(5, stats.pendingCommands)
        assertEquals(2, stats.retryableCommands)
        assertEquals(1500.0, stats.averageExecutionTime)
        assertEquals(SyncStatus.COMPLETED, stats.currentStatus)
    }

    @Test
    fun `configureAutoSync should enable periodic sync when enabled`() = runTest {
        // Given
        every { workManager.enqueueUniquePeriodicWork(any(), any(), any()) } returns mockk()

        // When
        syncManager.configureAutoSync(enabled = true, intervalMinutes = 30L)

        // Then
        verify { workManager.enqueueUniquePeriodicWork(any(), any(), any()) }
    }

    @Test
    fun `configureAutoSync should cancel periodic sync when disabled`() = runTest {
        // Given
        every { workManager.cancelUniqueWork(any()) } returns mockk()

        // When
        syncManager.configureAutoSync(enabled = false)

        // Then
        verify { workManager.cancelUniqueWork("IoTLogicPeriodicSync") }
    }

    @Test
    fun `syncTelemetryData should upload telemetry in batches`() = runTest {
        // Given
        val token = "access_token"
        val telemetryList = (1..150).map { index ->
            TelemetryEntity(
                id = "tel$index",
                deviceId = "device1",
                sensorType = "temperature",
                value = 25.0,
                unit = "°C",
                timestamp = System.currentTimeMillis(),
                syncStatus = "PENDING"
            )
        }

        every { telemetryDao.getPendingSyncTelemetry() } returns telemetryList
        every { apiService.uploadTelemetryBatch(any(), any()) } returns mockk {
            every { isSuccessful } returns true
        }
        every { telemetryDao.updateTelemetry(any()) } just Runs

        // When
        val result = syncManager.startFullSync(token)

        // Then
        assertTrue(result.isSuccess)
        // Should be called twice for 150 items (100 + 50)
        verify(exactly = 2) { apiService.uploadTelemetryBatch(any(), any()) }
    }

    @Test
    fun `executeCommand should handle telemetry sync commands`() = runTest {
        // Given
        val token = "access_token"
        val command = CommandQueueEntity(
            id = "cmd123",
            deviceId = "device123",
            command = "telemetry_sync",
            parameters = mapOf(
                "telemetryData" to listOf(
                    mapOf("id" to "tel1", "value" to 25.0)
                )
            )
        )

        every { commandQueueDao.getReadyCommands(any()) } returns listOf(command)
        every { commandQueueDao.markCommandAsSent(any()) } just Runs
        every { commandQueueDao.markCommandAsCompleted(any(), any()) } just Runs
        every { apiService.uploadTelemetryBatch(any(), any()) } returns mockk {
            every { isSuccessful } returns true
        }

        // When
        val result = syncManager.processPendingCommands(token)

        // Then
        assertTrue(result.isSuccess)
        verify { apiService.uploadTelemetryBatch("Bearer $token", any()) }
    }

    @Test
    fun `executeCommand should fail for unknown command types`() = runTest {
        // Given
        val token = "access_token"
        val command = CommandQueueEntity(
            id = "cmd123",
            deviceId = "device123",
            command = "unknown_command",
            parameters = emptyMap()
        )

        every { commandQueueDao.getReadyCommands(any()) } returns listOf(command)
        every { commandQueueDao.markCommandAsSent(any()) } just Runs
        every { commandQueueDao.markCommandAsFailed(any(), any()) } just Runs

        // When
        val result = syncManager.processPendingCommands(token)

        // Then
        assertTrue(result.isFailure)
        verify { 
            commandQueueDao.markCommandAsFailed(
                "cmd123", 
                "Unknown command type: unknown_command"
            )
        }
    }
}