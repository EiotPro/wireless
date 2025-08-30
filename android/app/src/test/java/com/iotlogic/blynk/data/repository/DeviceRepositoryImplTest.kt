package com.iotlogic.blynk.data.repository

import com.iotlogic.blynk.data.local.dao.DeviceDao
import com.iotlogic.blynk.data.local.entities.DeviceEntity
import com.iotlogic.blynk.data.remote.ApiClient
import com.iotlogic.blynk.domain.model.Device
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class DeviceRepositoryImplTest {
    
    @MockK
    private lateinit var deviceDao: DeviceDao
    
    @MockK
    private lateinit var apiClient: ApiClient
    
    private lateinit var deviceRepository: DeviceRepositoryImpl
    
    @Before
    fun setup() {
        MockKAnnotations.init(this)
        deviceRepository = DeviceRepositoryImpl(deviceDao, apiClient)
    }
    
    @After
    fun teardown() {
        unmockkAll()
    }
    
    @Test
    fun `getDevices returns flow of devices from local database`() = runTest {
        // Given
        val deviceEntities = listOf(
            DeviceEntity(
                id = "device1",
                name = "Test Device 1",
                type = "sensor",
                protocol = "BLE",
                ipAddress = "192.168.1.100",
                macAddress = "AA:BB:CC:DD:EE:FF",
                location = "Living Room",
                description = "Temperature sensor",
                isActive = true,
                lastSeen = System.currentTimeMillis(),
                latitude = 37.7749,
                longitude = -122.4194,
                createdAt = System.currentTimeMillis()
            ),
            DeviceEntity(
                id = "device2",
                name = "Test Device 2",
                type = "actuator",
                protocol = "WiFi",
                ipAddress = "192.168.1.101",
                macAddress = "FF:EE:DD:CC:BB:AA",
                location = "Kitchen",
                description = "Smart switch",
                isActive = false,
                lastSeen = System.currentTimeMillis() - 3600000,
                latitude = 37.7849,
                longitude = -122.4094,
                createdAt = System.currentTimeMillis()
            )
        )
        
        every { deviceDao.getAllDevicesFlow() } returns flowOf(deviceEntities)
        
        // When
        val devicesFlow = deviceRepository.getDevices()
        
        // Then
        devicesFlow.collect { devices ->
            assertEquals(2, devices.size)
            assertEquals("device1", devices[0].id)
            assertEquals("Test Device 1", devices[0].name)
            assertEquals("sensor", devices[0].type)
            assertEquals("device2", devices[1].id)
            assertEquals("Test Device 2", devices[1].name)
            assertEquals("actuator", devices[1].type)
        }
        
        verify { deviceDao.getAllDevicesFlow() }
    }
    
    @Test
    fun `getDeviceById returns correct device`() = runTest {
        // Given
        val deviceId = "device1"
        val deviceEntity = DeviceEntity(
            id = deviceId,
            name = "Test Device",
            type = "sensor",
            protocol = "BLE",
            ipAddress = "192.168.1.100",
            macAddress = "AA:BB:CC:DD:EE:FF",
            location = "Living Room",
            description = "Temperature sensor",
            isActive = true,
            lastSeen = System.currentTimeMillis(),
            latitude = 37.7749,
            longitude = -122.4194,
            createdAt = System.currentTimeMillis()
        )
        
        coEvery { deviceDao.getDeviceById(deviceId) } returns deviceEntity
        
        // When
        val result = deviceRepository.getDeviceById(deviceId)
        
        // Then
        assertTrue(result.isSuccess)
        val device = result.getOrNull()
        assertNotNull(device)
        assertEquals(deviceId, device?.id)
        assertEquals("Test Device", device?.name)
        assertEquals("sensor", device?.type)
        
        coVerify { deviceDao.getDeviceById(deviceId) }
    }
    
    @Test
    fun `getDeviceById returns failure when device not found`() = runTest {
        // Given
        val deviceId = "nonexistent"
        coEvery { deviceDao.getDeviceById(deviceId) } returns null
        
        // When
        val result = deviceRepository.getDeviceById(deviceId)
        
        // Then
        assertTrue(result.isFailure)
        
        coVerify { deviceDao.getDeviceById(deviceId) }
    }
    
    @Test
    fun `saveDevice inserts device successfully`() = runTest {
        // Given
        val device = Device(
            id = "device1",
            name = "Test Device",
            type = "sensor",
            protocol = "BLE",
            ipAddress = "192.168.1.100",
            macAddress = "AA:BB:CC:DD:EE:FF",
            location = "Living Room",
            description = "Temperature sensor",
            isActive = true,
            lastSeen = System.currentTimeMillis(),
            latitude = 37.7749,
            longitude = -122.4194
        )
        
        coEvery { deviceDao.insertDevice(any()) } just Runs
        
        // When
        val result = deviceRepository.saveDevice(device)
        
        // Then
        assertTrue(result.isSuccess)
        
        coVerify { deviceDao.insertDevice(any()) }
    }
    
    @Test
    fun `saveDevice handles database exception`() = runTest {
        // Given
        val device = Device(
            id = "device1",
            name = "Test Device",
            type = "sensor",
            protocol = "BLE",
            ipAddress = "192.168.1.100",
            macAddress = "AA:BB:CC:DD:EE:FF",
            location = "Living Room",
            description = "Temperature sensor",
            isActive = true,
            lastSeen = System.currentTimeMillis(),
            latitude = 37.7749,
            longitude = -122.4194
        )
        
        coEvery { deviceDao.insertDevice(any()) } throws Exception("Database error")
        
        // When
        val result = deviceRepository.saveDevice(device)
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Database error", result.exceptionOrNull()?.message)
        
        coVerify { deviceDao.insertDevice(any()) }
    }
    
    @Test
    fun `updateDevice updates device successfully`() = runTest {
        // Given
        val device = Device(
            id = "device1",
            name = "Updated Device",
            type = "sensor",
            protocol = "BLE",
            ipAddress = "192.168.1.100",
            macAddress = "AA:BB:CC:DD:EE:FF",
            location = "Living Room",
            description = "Updated temperature sensor",
            isActive = true,
            lastSeen = System.currentTimeMillis(),
            latitude = 37.7749,
            longitude = -122.4194
        )
        
        coEvery { deviceDao.updateDevice(any()) } just Runs
        
        // When
        val result = deviceRepository.updateDevice(device)
        
        // Then
        assertTrue(result.isSuccess)
        
        coVerify { deviceDao.updateDevice(any()) }
    }
    
    @Test
    fun `deleteDevice removes device successfully`() = runTest {
        // Given
        val deviceId = "device1"
        coEvery { deviceDao.deleteDevice(deviceId) } just Runs
        
        // When
        val result = deviceRepository.deleteDevice(deviceId)
        
        // Then
        assertTrue(result.isSuccess)
        
        coVerify { deviceDao.deleteDevice(deviceId) }
    }
    
    @Test
    fun `getDevicesByType returns filtered devices`() = runTest {
        // Given
        val deviceType = "sensor"
        val deviceEntities = listOf(
            DeviceEntity(
                id = "device1",
                name = "Sensor 1",
                type = "sensor",
                protocol = "BLE",
                ipAddress = "192.168.1.100",
                macAddress = "AA:BB:CC:DD:EE:FF",
                location = "Living Room",
                description = "Temperature sensor",
                isActive = true,
                lastSeen = System.currentTimeMillis(),
                latitude = 37.7749,
                longitude = -122.4194,
                createdAt = System.currentTimeMillis()
            )
        )
        
        every { deviceDao.getDevicesByType(deviceType) } returns flowOf(deviceEntities)
        
        // When
        val devicesFlow = deviceRepository.getDevicesByType(deviceType)
        
        // Then
        devicesFlow.collect { devices ->
            assertEquals(1, devices.size)
            assertEquals("sensor", devices[0].type)
        }
        
        verify { deviceDao.getDevicesByType(deviceType) }
    }
    
    @Test
    fun `getActiveDevices returns only active devices`() = runTest {
        // Given
        val activeDeviceEntities = listOf(
            DeviceEntity(
                id = "device1",
                name = "Active Device",
                type = "sensor",
                protocol = "BLE",
                ipAddress = "192.168.1.100",
                macAddress = "AA:BB:CC:DD:EE:FF",
                location = "Living Room",
                description = "Active sensor",
                isActive = true,
                lastSeen = System.currentTimeMillis(),
                latitude = 37.7749,
                longitude = -122.4194,
                createdAt = System.currentTimeMillis()
            )
        )
        
        every { deviceDao.getActiveDevices() } returns flowOf(activeDeviceEntities)
        
        // When
        val devicesFlow = deviceRepository.getActiveDevices()
        
        // Then
        devicesFlow.collect { devices ->
            assertEquals(1, devices.size)
            assertTrue(devices[0].isActive)
        }
        
        verify { deviceDao.getActiveDevices() }
    }
    
    @Test
    fun `updateDeviceStatus updates device status successfully`() = runTest {
        // Given
        val deviceId = "device1"
        val isActive = true
        val lastSeen = System.currentTimeMillis()
        
        coEvery { deviceDao.updateDeviceStatus(deviceId, isActive, lastSeen) } just Runs
        
        // When
        val result = deviceRepository.updateDeviceStatus(deviceId, isActive, lastSeen)
        
        // Then
        assertTrue(result.isSuccess)
        
        coVerify { deviceDao.updateDeviceStatus(deviceId, isActive, lastSeen) }
    }
}