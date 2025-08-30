package com.iotlogic.blynk.ui.viewmodel

import com.iotlogic.blynk.domain.model.Device
import com.iotlogic.blynk.domain.repository.DeviceRepository
import com.iotlogic.blynk.hardware.HardwareManager
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class DeviceViewModelTest {
    
    @MockK
    private lateinit var deviceRepository: DeviceRepository
    
    @MockK
    private lateinit var hardwareManager: HardwareManager
    
    private lateinit var deviceViewModel: DeviceViewModel
    
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        
        // Mock repository to return empty flow by default
        every { deviceRepository.getDevices() } returns flowOf(emptyList())
        every { deviceRepository.getActiveDevices() } returns flowOf(emptyList())
        
        deviceViewModel = DeviceViewModel(deviceRepository, hardwareManager)
    }
    
    @After
    fun teardown() {
        Dispatchers.resetMain()
        unmockkAll()
    }
    
    @Test
    fun `initial state is correct`() {
        // Then
        val uiState = deviceViewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertNull(uiState.error)
        assertNull(uiState.message)
    }
    
    @Test
    fun `devices flow emits repository data`() = runTest {
        // Given
        val testDevices = listOf(
            Device(
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
                longitude = -122.4194
            ),
            Device(
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
                longitude = -122.4094
            )
        )
        
        every { deviceRepository.getDevices() } returns flowOf(testDevices)
        
        // When
        val newViewModel = DeviceViewModel(deviceRepository, hardwareManager)
        advanceUntilIdle()
        
        // Then
        val devices = newViewModel.devices.value
        assertEquals(2, devices.size)
        assertEquals("device1", devices[0].id)
        assertEquals("device2", devices[1].id)
    }
    
    @Test
    fun `selectDevice updates selected device`() = runTest {
        // Given
        val deviceId = "device1"
        val testDevice = Device(
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
            longitude = -122.4194
        )
        
        coEvery { deviceRepository.getDeviceById(deviceId) } returns Result.success(testDevice)
        
        // When
        deviceViewModel.selectDevice(deviceId)
        advanceUntilIdle()
        
        // Then
        val selectedDevice = deviceViewModel.selectedDevice.value
        assertNotNull(selectedDevice)
        assertEquals(deviceId, selectedDevice?.id)
        assertEquals("Test Device", selectedDevice?.name)
        
        coVerify { deviceRepository.getDeviceById(deviceId) }
    }
    
    @Test
    fun `selectDevice handles device not found`() = runTest {
        // Given
        val deviceId = "nonexistent"
        coEvery { deviceRepository.getDeviceById(deviceId) } returns Result.failure(Exception("Device not found"))
        
        // When
        deviceViewModel.selectDevice(deviceId)
        advanceUntilIdle()
        
        // Then
        val selectedDevice = deviceViewModel.selectedDevice.value
        assertNull(selectedDevice)
        
        val uiState = deviceViewModel.uiState.value
        assertEquals("Device not found", uiState.error)
        
        coVerify { deviceRepository.getDeviceById(deviceId) }
    }
    
    @Test
    fun `addDevice saves device successfully`() = runTest {
        // Given
        val testDevice = Device(
            id = "device1",
            name = "New Device",
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
        
        coEvery { deviceRepository.saveDevice(testDevice) } returns Result.success(Unit)
        
        // When
        deviceViewModel.addDevice(testDevice)
        advanceUntilIdle()
        
        // Then
        val uiState = deviceViewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertEquals("Device added successfully", uiState.message)
        
        coVerify { deviceRepository.saveDevice(testDevice) }
    }
    
    @Test
    fun `addDevice handles save error`() = runTest {
        // Given
        val testDevice = Device(
            id = "device1",
            name = "New Device",
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
        
        coEvery { deviceRepository.saveDevice(testDevice) } returns Result.failure(Exception("Save failed"))
        
        // When
        deviceViewModel.addDevice(testDevice)
        advanceUntilIdle()
        
        // Then
        val uiState = deviceViewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertEquals("Save failed", uiState.error)
        
        coVerify { deviceRepository.saveDevice(testDevice) }
    }
    
    @Test
    fun `updateDevice updates device successfully`() = runTest {
        // Given
        val testDevice = Device(
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
        
        coEvery { deviceRepository.updateDevice(testDevice) } returns Result.success(Unit)
        
        // When
        deviceViewModel.updateDevice(testDevice)
        advanceUntilIdle()
        
        // Then
        val uiState = deviceViewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertEquals("Device updated successfully", uiState.message)
        
        coVerify { deviceRepository.updateDevice(testDevice) }
    }
    
    @Test
    fun `deleteDevice removes device successfully`() = runTest {
        // Given
        val deviceId = "device1"
        coEvery { deviceRepository.deleteDevice(deviceId) } returns Result.success(Unit)
        
        // When
        deviceViewModel.deleteDevice(deviceId)
        advanceUntilIdle()
        
        // Then
        val uiState = deviceViewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertEquals("Device deleted successfully", uiState.message)
        
        coVerify { deviceRepository.deleteDevice(deviceId) }
    }
    
    @Test
    fun `connectDevice attempts hardware connection`() = runTest {
        // Given
        val deviceId = "device1"
        coEvery { hardwareManager.connectToDevice(deviceId) } returns Result.success(Unit)
        coEvery { deviceRepository.updateDeviceStatus(deviceId, true, any()) } returns Result.success(Unit)
        
        // When
        deviceViewModel.connectDevice(deviceId)
        advanceUntilIdle()
        
        // Then
        val uiState = deviceViewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertEquals("Device connected successfully", uiState.message)
        
        coVerify { hardwareManager.connectToDevice(deviceId) }
        coVerify { deviceRepository.updateDeviceStatus(deviceId, true, any()) }
    }
    
    @Test
    fun `connectDevice handles connection failure`() = runTest {
        // Given
        val deviceId = "device1"
        coEvery { hardwareManager.connectToDevice(deviceId) } returns Result.failure(Exception("Connection failed"))
        
        // When
        deviceViewModel.connectDevice(deviceId)
        advanceUntilIdle()
        
        // Then
        val uiState = deviceViewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertEquals("Connection failed", uiState.error)
        
        coVerify { hardwareManager.connectToDevice(deviceId) }
        coVerify(exactly = 0) { deviceRepository.updateDeviceStatus(any(), any(), any()) }
    }
    
    @Test
    fun `disconnectDevice attempts hardware disconnection`() = runTest {
        // Given
        val deviceId = "device1"
        coEvery { hardwareManager.disconnectFromDevice(deviceId) } returns Result.success(Unit)
        coEvery { deviceRepository.updateDeviceStatus(deviceId, false, any()) } returns Result.success(Unit)
        
        // When
        deviceViewModel.disconnectDevice(deviceId)
        advanceUntilIdle()
        
        // Then
        val uiState = deviceViewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertEquals("Device disconnected successfully", uiState.message)
        
        coVerify { hardwareManager.disconnectFromDevice(deviceId) }
        coVerify { deviceRepository.updateDeviceStatus(deviceId, false, any()) }
    }
    
    @Test
    fun `sendDeviceCommand sends command through hardware manager`() = runTest {
        // Given
        val deviceId = "device1"
        val command = "turn_on"
        val value = true
        
        coEvery { hardwareManager.sendDeviceCommand(deviceId, command, value) } returns Result.success("Command sent")
        
        // When
        deviceViewModel.sendDeviceCommand(deviceId, command, value)
        advanceUntilIdle()
        
        // Then
        val uiState = deviceViewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertEquals("Command sent successfully", uiState.message)
        
        coVerify { hardwareManager.sendDeviceCommand(deviceId, command, value) }
    }
    
    @Test
    fun `refreshDevices triggers repository refresh`() = runTest {
        // Given
        coEvery { deviceRepository.refreshDevices() } returns Result.success(Unit)
        
        // When
        deviceViewModel.refreshDevices()
        advanceUntilIdle()
        
        // Then
        val uiState = deviceViewModel.uiState.value
        assertFalse(uiState.isLoading)
        
        coVerify { deviceRepository.refreshDevices() }
    }
    
    @Test
    fun `clearError clears error state`() {
        // Given
        deviceViewModel.clearError()
        
        // Then
        val uiState = deviceViewModel.uiState.value
        assertNull(uiState.error)
    }
    
    @Test
    fun `clearMessage clears message state`() {
        // Given
        deviceViewModel.clearMessage()
        
        // Then
        val uiState = deviceViewModel.uiState.value
        assertNull(uiState.message)
    }
}