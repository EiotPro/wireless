package com.iotlogic.blynk.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iotlogic.blynk.data.local.IoTLogicDatabase
import com.iotlogic.blynk.data.local.dao.*
import com.iotlogic.blynk.data.local.entities.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DatabaseIntegrationTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: IoTLogicDatabase
    private lateinit var deviceDao: DeviceDao
    private lateinit var telemetryDao: TelemetryDao
    private lateinit var configurationDao: ConfigurationDao
    private lateinit var commandQueueDao: CommandQueueDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            IoTLogicDatabase::class.java
        ).allowMainThreadQueries().build()

        deviceDao = database.deviceDao()
        telemetryDao = database.telemetryDao()
        configurationDao = database.configurationDao()
        commandQueueDao = database.commandQueueDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testDeviceCrudOperations() = runBlocking {
        // Given
        val device = DeviceEntity(
            id = "device1",
            name = "Test Device",
            type = "sensor",
            protocol = "BLE",
            macAddress = "AA:BB:CC:DD:EE:FF",
            ipAddress = "192.168.1.100",
            port = 8080,
            isOnline = true,
            lastSeen = System.currentTimeMillis(),
            location = "Living Room",
            description = "Test device for integration testing",
            batteryLevel = 85,
            firmwareVersion = "1.0.0"
        )

        // Test Insert
        deviceDao.insertDevice(device)
        val insertedDevice = deviceDao.getDeviceById("device1")
        assertNotNull(insertedDevice)
        assertEquals("Test Device", insertedDevice.name)
        assertEquals("BLE", insertedDevice.protocol)

        // Test Update
        val updatedDevice = device.copy(
            name = "Updated Device",
            isOnline = false,
            batteryLevel = 70
        )
        deviceDao.updateDevice(updatedDevice)
        val retrievedDevice = deviceDao.getDeviceById("device1")!!
        assertEquals("Updated Device", retrievedDevice.name)
        assertEquals(false, retrievedDevice.isOnline)
        assertEquals(70, retrievedDevice.batteryLevel)

        // Test Get All
        val allDevices = deviceDao.getAllDevices()
        assertEquals(1, allDevices.size)

        // Test Get Online Devices
        val onlineDevices = deviceDao.getOnlineDevices()
        assertEquals(0, onlineDevices.size) // Device is offline

        // Test Delete
        deviceDao.deleteDevice(device)
        val deletedDevice = deviceDao.getDeviceById("device1")
        assertNull(deletedDevice)
    }

    @Test
    fun testTelemetryCrudOperations() = runBlocking {
        // First create a device
        val device = DeviceEntity(
            id = "device1",
            name = "Test Device",
            type = "sensor",
            protocol = "BLE"
        )
        deviceDao.insertDevice(device)

        // Test telemetry operations
        val telemetry = TelemetryEntity(
            id = "tel1",
            deviceId = "device1",
            sensorType = "temperature",
            value = 25.5,
            unit = "°C",
            timestamp = System.currentTimeMillis(),
            quality = "good",
            metadata = mapOf("location" to "indoor")
        )

        // Test Insert
        telemetryDao.insertTelemetry(telemetry)
        val insertedTelemetry = telemetryDao.getTelemetryById("tel1")
        assertNotNull(insertedTelemetry)
        assertEquals(25.5, insertedTelemetry.value)
        assertEquals("temperature", insertedTelemetry.sensorType)

        // Test Get by Device
        val deviceTelemetry = telemetryDao.getTelemetryForDevice("device1")
        assertEquals(1, deviceTelemetry.size)

        // Test Get Latest
        val latestTelemetry = telemetryDao.getLatestTelemetryForDevice("device1", "temperature")
        assertNotNull(latestTelemetry)
        assertEquals("tel1", latestTelemetry.id)

        // Test Batch Insert
        val batchTelemetry = listOf(
            telemetry.copy(id = "tel2", value = 26.0, timestamp = System.currentTimeMillis() + 1000),
            telemetry.copy(id = "tel3", value = 26.5, timestamp = System.currentTimeMillis() + 2000)
        )
        telemetryDao.insertTelemetryBatch(batchTelemetry)

        val allTelemetry = telemetryDao.getTelemetryForDevice("device1")
        assertEquals(3, allTelemetry.size)

        // Test Get in Range
        val startTime = System.currentTimeMillis() - 5000
        val endTime = System.currentTimeMillis() + 5000
        val rangeTelemetry = telemetryDao.getTelemetryInRange("device1", startTime, endTime)
        assertEquals(3, rangeTelemetry.size)

        // Test Delete Old Telemetry
        val cutoffTime = System.currentTimeMillis() + 1500
        telemetryDao.deleteOldTelemetry(cutoffTime)
        val remainingTelemetry = telemetryDao.getTelemetryForDevice("device1")
        assertEquals(1, remainingTelemetry.size) // Only tel3 should remain
    }

    @Test
    fun testConfigurationCrudOperations() = runBlocking {
        // First create a device
        val device = DeviceEntity(
            id = "device1",
            name = "Test Device",
            type = "sensor",
            protocol = "BLE"
        )
        deviceDao.insertDevice(device)

        // Test configuration operations
        val configuration = ConfigurationEntity(
            id = "config1",
            deviceId = "device1",
            key = "updateInterval",
            value = "30",
            dataType = "integer",
            description = "Update interval in seconds",
            isEditable = true,
            category = "general"
        )

        // Test Insert
        configurationDao.insertConfiguration(configuration)
        val insertedConfig = configurationDao.getConfigurationById("config1")
        assertNotNull(insertedConfig)
        assertEquals("updateInterval", insertedConfig.key)
        assertEquals("30", insertedConfig.value)

        // Test Get by Device
        val deviceConfigs = configurationDao.getConfigurationsForDevice("device1")
        assertEquals(1, deviceConfigs.size)

        // Test Get by Category
        val generalConfigs = configurationDao.getConfigurationsByCategory("device1", "general")
        assertEquals(1, generalConfigs.size)

        // Test Update Value
        configurationDao.updateConfigurationValue("config1", "60")
        val updatedConfig = configurationDao.getConfigurationById("config1")!!
        assertEquals("60", updatedConfig.value)

        // Test Batch Insert
        val batchConfigs = listOf(
            configuration.copy(id = "config2", key = "threshold", value = "25.0", dataType = "float"),
            configuration.copy(id = "config3", key = "enabled", value = "true", dataType = "boolean")
        )
        configurationDao.insertConfigurationBatch(batchConfigs)

        val allConfigs = configurationDao.getConfigurationsForDevice("device1")
        assertEquals(3, allConfigs.size)

        // Test Delete
        configurationDao.deleteConfiguration(configuration)
        val remainingConfigs = configurationDao.getConfigurationsForDevice("device1")
        assertEquals(2, remainingConfigs.size)
    }

    @Test
    fun testCommandQueueOperations() = runBlocking {
        // Test command queue operations
        val command = CommandQueueEntity(
            id = "cmd1",
            deviceId = "device1",
            command = "device_control",
            parameters = mapOf(
                "action" to "turn_on",
                "brightness" to 80
            ),
            priority = 1,
            maxRetries = 3,
            retryCount = 0,
            status = "PENDING",
            createdAt = System.currentTimeMillis()
        )

        // Test Insert
        commandQueueDao.insertCommand(command)
        val insertedCommand = commandQueueDao.getCommandById("cmd1")
        assertNotNull(insertedCommand)
        assertEquals("device_control", insertedCommand.command)
        assertEquals("PENDING", insertedCommand.status)

        // Test Get Ready Commands
        val readyCommands = commandQueueDao.getReadyCommands(10)
        assertEquals(1, readyCommands.size)

        // Test Mark as Sent
        commandQueueDao.markCommandAsSent("cmd1")
        val sentCommand = commandQueueDao.getCommandById("cmd1")!!
        assertEquals("SENT", sentCommand.status)

        // Test Mark as Completed
        commandQueueDao.markCommandAsCompleted("cmd1", "Success")
        val completedCommand = commandQueueDao.getCommandById("cmd1")!!
        assertEquals("COMPLETED", completedCommand.status)
        assertEquals("Success", completedCommand.result)

        // Test Failed Command
        val failedCommand = command.copy(
            id = "cmd2",
            status = "PENDING"
        )
        commandQueueDao.insertCommand(failedCommand)
        commandQueueDao.markCommandAsFailed("cmd2", "Network error")
        
        val retrievedFailedCommand = commandQueueDao.getCommandById("cmd2")!!
        assertEquals("FAILED", retrievedFailedCommand.status)
        assertEquals("Network error", retrievedFailedCommand.errorMessage)
        assertEquals(1, retrievedFailedCommand.retryCount)

        // Test Get Retryable Commands
        val retryableCommands = commandQueueDao.getRetryableCommands()
        assertEquals(1, retryableCommands.size)

        // Test Retry Failed Command
        commandQueueDao.retryFailedCommand("cmd2")
        val retriedCommand = commandQueueDao.getCommandById("cmd2")!!
        assertEquals("PENDING", retriedCommand.status)

        // Test Get Pending Count
        val pendingCount = commandQueueDao.getPendingCommandCount()
        assertEquals(1, pendingCount)

        // Test Cancel Command
        commandQueueDao.cancelCommand("cmd2")
        val cancelledCommand = commandQueueDao.getCommandById("cmd2")!!
        assertEquals("CANCELLED", cancelledCommand.status)
    }

    @Test
    fun testDatabaseFlowOperations() = runBlocking {
        // Test Flow operations for reactive data
        val device = DeviceEntity(
            id = "device1",
            name = "Test Device",
            type = "sensor",
            protocol = "BLE"
        )

        // Test Device Flow
        val deviceFlow = deviceDao.observeAllDevices()
        deviceDao.insertDevice(device)
        
        val devices = deviceFlow.first()
        assertEquals(1, devices.size)
        assertEquals("Test Device", devices[0].name)

        // Test Online Devices Flow
        val onlineDevicesFlow = deviceDao.observeOnlineDevices()
        val onlineDevices = onlineDevicesFlow.first()
        assertEquals(1, onlineDevices.size) // Device is online by default

        // Update device to offline
        deviceDao.updateDevice(device.copy(isOnline = false))
        val offlineDevices = onlineDevicesFlow.first()
        assertEquals(0, offlineDevices.size)
    }

    @Test
    fun testDatabaseTransactions() = runBlocking {
        // Test database transactions
        val device = DeviceEntity(
            id = "device1",
            name = "Test Device",
            type = "sensor",
            protocol = "BLE"
        )

        val telemetry = TelemetryEntity(
            id = "tel1",
            deviceId = "device1",
            sensorType = "temperature",
            value = 25.5,
            unit = "°C",
            timestamp = System.currentTimeMillis()
        )

        val configuration = ConfigurationEntity(
            id = "config1",
            deviceId = "device1",
            key = "updateInterval",
            value = "30",
            dataType = "integer"
        )

        // Perform transaction
        database.runInTransaction {
            deviceDao.insertDevice(device)
            telemetryDao.insertTelemetry(telemetry)
            configurationDao.insertConfiguration(configuration)
        }

        // Verify all data was inserted
        val insertedDevice = deviceDao.getDeviceById("device1")
        val insertedTelemetry = telemetryDao.getTelemetryById("tel1")
        val insertedConfig = configurationDao.getConfigurationById("config1")

        assertNotNull(insertedDevice)
        assertNotNull(insertedTelemetry)
        assertNotNull(insertedConfig)
    }

    @Test
    fun testComplexQueries() = runBlocking {
        // Setup test data
        val devices = listOf(
            DeviceEntity(id = "device1", name = "Sensor 1", type = "sensor", protocol = "BLE", isOnline = true),
            DeviceEntity(id = "device2", name = "Sensor 2", type = "sensor", protocol = "WiFi", isOnline = false),
            DeviceEntity(id = "device3", name = "Actuator 1", type = "actuator", protocol = "BLE", isOnline = true)
        )

        devices.forEach { deviceDao.insertDevice(it) }

        val telemetryData = listOf(
            TelemetryEntity(id = "tel1", deviceId = "device1", sensorType = "temperature", value = 25.0, timestamp = System.currentTimeMillis() - 1000),
            TelemetryEntity(id = "tel2", deviceId = "device1", sensorType = "humidity", value = 60.0, timestamp = System.currentTimeMillis()),
            TelemetryEntity(id = "tel3", deviceId = "device2", sensorType = "temperature", value = 23.0, timestamp = System.currentTimeMillis() - 2000)
        )

        telemetryData.forEach { telemetryDao.insertTelemetry(it) }

        // Test complex queries
        val bleDevices = deviceDao.getDevicesByProtocol("BLE")
        assertEquals(2, bleDevices.size)

        val onlineDevices = deviceDao.getOnlineDevices()
        assertEquals(2, onlineDevices.size)

        val deviceWithLatestTelemetry = telemetryDao.getLatestTelemetryForDevice("device1", "temperature")
        assertNotNull(deviceWithLatestTelemetry)
        assertEquals(25.0, deviceWithLatestTelemetry.value)

        val temperatureReadings = telemetryDao.getTelemetryBySensorType("temperature")
        assertEquals(2, temperatureReadings.size)
    }
}