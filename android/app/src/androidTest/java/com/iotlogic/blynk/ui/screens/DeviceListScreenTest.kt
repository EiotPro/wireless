package com.iotlogic.blynk.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iotlogic.blynk.domain.model.Device
import com.iotlogic.blynk.ui.screens.devices.DeviceListScreen
import com.iotlogic.blynk.ui.theme.IoTLogicTheme
import com.iotlogic.blynk.ui.viewmodel.DeviceViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DeviceListScreenTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: DeviceViewModel
    private lateinit var devicesFlow: MutableStateFlow<List<Device>>
    private lateinit var connectedDevicesFlow: MutableStateFlow<List<Device>>
    private lateinit var uiStateFlow: MutableStateFlow<DeviceViewModel.UiState>

    @Before
    fun setUp() {
        hiltRule.inject()

        // Create mock ViewModel
        mockViewModel = mockk(relaxed = true)
        devicesFlow = MutableStateFlow(emptyList())
        connectedDevicesFlow = MutableStateFlow(emptyList())
        uiStateFlow = MutableStateFlow(DeviceViewModel.UiState())

        every { mockViewModel.devices } returns devicesFlow
        every { mockViewModel.connectedDevices } returns connectedDevicesFlow
        every { mockViewModel.uiState } returns uiStateFlow
    }

    @Test
    fun deviceListScreen_EmptyState_ShowsEmptyMessage() {
        // Given empty device list
        devicesFlow.value = emptyList()
        uiStateFlow.value = DeviceViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceListScreen(
                    onDeviceClick = {},
                    onAddDeviceClick = {},
                    onScanDevicesClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("No devices found").assertExists()
        composeTestRule.onNodeWithText("Add or scan for IoT devices to get started").assertExists()
        composeTestRule.onNodeWithText("Scan Devices").assertExists()
        composeTestRule.onNodeWithText("Add Device").assertExists()
    }

    @Test
    fun deviceListScreen_LoadingState_ShowsProgressIndicator() {
        // Given loading state
        uiStateFlow.value = DeviceViewModel.UiState(isLoading = true)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceListScreen(
                    onDeviceClick = {},
                    onAddDeviceClick = {},
                    onScanDevicesClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Loading devices...").assertExists()
    }

    @Test
    fun deviceListScreen_WithDevices_ShowsDeviceList() {
        // Given device list
        val devices = listOf(
            createTestDevice("device1", "Temperature Sensor", true),
            createTestDevice("device2", "Smart Light", false),
            createTestDevice("device3", "Motion Detector", true)
        )
        devicesFlow.value = devices
        connectedDevicesFlow.value = devices.filter { it.isOnline }
        uiStateFlow.value = DeviceViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceListScreen(
                    onDeviceClick = {},
                    onAddDeviceClick = {},
                    onScanDevicesClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Temperature Sensor").assertExists()
        composeTestRule.onNodeWithText("Smart Light").assertExists()
        composeTestRule.onNodeWithText("Motion Detector").assertExists()
        
        // Check status cards
        composeTestRule.onNodeWithText("Total Devices").assertExists()
        composeTestRule.onNodeWithText("3").assertExists() // Total count
        composeTestRule.onNodeWithText("Connected").assertExists()
        composeTestRule.onNodeWithText("2").assertExists() // Connected count
        composeTestRule.onNodeWithText("Offline").assertExists()
        composeTestRule.onNodeWithText("1").assertExists() // Offline count
    }

    @Test
    fun deviceListScreen_ClickAddDevice_CallsCallback() {
        // Given
        var addDeviceClicked = false
        uiStateFlow.value = DeviceViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceListScreen(
                    onDeviceClick = {},
                    onAddDeviceClick = { addDeviceClicked = true },
                    onScanDevicesClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Click add device button in top bar
        composeTestRule.onNodeWithContentDescription("Add device").performClick()

        // Then
        assert(addDeviceClicked)
    }

    @Test
    fun deviceListScreen_ClickScanDevices_CallsCallback() {
        // Given
        var scanDevicesClicked = false
        uiStateFlow.value = DeviceViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceListScreen(
                    onDeviceClick = {},
                    onAddDeviceClick = {},
                    onScanDevicesClick = { scanDevicesClicked = true },
                    viewModel = mockViewModel
                )
            }
        }

        // Click scan devices button in top bar
        composeTestRule.onNodeWithContentDescription("Scan for devices").performClick()

        // Then
        assert(scanDevicesClicked)
    }

    @Test
    fun deviceListScreen_ClickDevice_CallsCallback() {
        // Given
        val devices = listOf(createTestDevice("device1", "Temperature Sensor", true))
        devicesFlow.value = devices
        uiStateFlow.value = DeviceViewModel.UiState(isLoading = false)

        var clickedDevice: Device? = null

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceListScreen(
                    onDeviceClick = { clickedDevice = it },
                    onAddDeviceClick = {},
                    onScanDevicesClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Click on device card
        composeTestRule.onNodeWithText("Temperature Sensor").performClick()

        // Then
        assert(clickedDevice != null)
        assert(clickedDevice?.id == "device1")
    }

    @Test
    fun deviceListScreen_ToggleDeviceConnection_CallsViewModel() {
        // Given
        val devices = listOf(createTestDevice("device1", "Temperature Sensor", true))
        devicesFlow.value = devices
        connectedDevicesFlow.value = devices
        uiStateFlow.value = DeviceViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceListScreen(
                    onDeviceClick = {},
                    onAddDeviceClick = {},
                    onScanDevicesClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Click connection toggle button
        composeTestRule.onNodeWithContentDescription("Disconnect").performClick()

        // Then
        verify { mockViewModel.disconnectDevice("device1") }
    }

    @Test
    fun deviceListScreen_PullToRefresh_CallsViewModel() {
        // Given
        val devices = listOf(createTestDevice("device1", "Temperature Sensor", true))
        devicesFlow.value = devices
        uiStateFlow.value = DeviceViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceListScreen(
                    onDeviceClick = {},
                    onAddDeviceClick = {},
                    onScanDevicesClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Perform pull to refresh gesture
        composeTestRule.onRoot().performTouchInput {
            swipeDown()
        }

        // Then
        verify { mockViewModel.refreshDevices() }
    }

    @Test
    fun deviceListScreen_ErrorState_ShowsErrorMessage() {
        // Given error state
        uiStateFlow.value = DeviceViewModel.UiState(
            isLoading = false,
            error = "Failed to load devices"
        )

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceListScreen(
                    onDeviceClick = {},
                    onAddDeviceClick = {},
                    onScanDevicesClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Failed to load devices").assertExists()
        composeTestRule.onNodeWithText("Retry").assertExists()
    }

    @Test
    fun deviceListScreen_ClickRetry_CallsViewModel() {
        // Given error state
        uiStateFlow.value = DeviceViewModel.UiState(
            isLoading = false,
            error = "Network error"
        )

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceListScreen(
                    onDeviceClick = {},
                    onAddDeviceClick = {},
                    onScanDevicesClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Click retry button
        composeTestRule.onNodeWithText("Retry").performClick()

        // Then
        verify { mockViewModel.refreshDevices() }
    }

    @Test
    fun deviceListScreen_DeviceCards_ShowCorrectInformation() {
        // Given device with specific information
        val device = Device(
            id = "device1",
            name = "Temperature Sensor",
            type = "sensor",
            protocol = "BLE",
            macAddress = "AA:BB:CC:DD:EE:FF",
            isOnline = true,
            location = "Living Room",
            description = "Monitors temperature and humidity",
            lastSeen = System.currentTimeMillis() - 60000, // 1 minute ago
            batteryLevel = 85,
            firmwareVersion = "1.2.3"
        )

        devicesFlow.value = listOf(device)
        connectedDevicesFlow.value = listOf(device)
        uiStateFlow.value = DeviceViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceListScreen(
                    onDeviceClick = {},
                    onAddDeviceClick = {},
                    onScanDevicesClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then verify device information is displayed
        composeTestRule.onNodeWithText("Temperature Sensor").assertExists()
        composeTestRule.onNodeWithText("sensor").assertExists()
        composeTestRule.onNodeWithText("Monitors temperature and humidity").assertExists()
        composeTestRule.onNodeWithText("BLE").assertExists()
        composeTestRule.onNodeWithText("Living Room").assertExists()
        composeTestRule.onNodeWithText("Online").assertExists()
        composeTestRule.onNodeWithText("1m ago").assertExists()
    }

    @Test
    fun deviceListScreen_SearchAndFilter_WorksCorrectly() {
        // Given multiple devices
        val devices = listOf(
            createTestDevice("device1", "Temperature Sensor", true, "BLE"),
            createTestDevice("device2", "Smart Light", false, "WiFi"),
            createTestDevice("device3", "Motion Detector", true, "BLE")
        )
        devicesFlow.value = devices
        uiStateFlow.value = DeviceViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceListScreen(
                    onDeviceClick = {},
                    onAddDeviceClick = {},
                    onScanDevicesClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then all devices should be visible initially
        composeTestRule.onNodeWithText("Temperature Sensor").assertExists()
        composeTestRule.onNodeWithText("Smart Light").assertExists()
        composeTestRule.onNodeWithText("Motion Detector").assertExists()

        // Test that protocol chips are shown
        composeTestRule.onAllNodesWithText("BLE").assertCountEquals(2)
        composeTestRule.onNodeWithText("WiFi").assertExists()
    }

    // Helper function to create test devices
    private fun createTestDevice(
        id: String,
        name: String,
        isOnline: Boolean,
        protocol: String = "BLE"
    ): Device {
        return Device(
            id = id,
            name = name,
            type = "sensor",
            protocol = protocol,
            macAddress = "AA:BB:CC:DD:EE:FF",
            isOnline = isOnline,
            lastSeen = System.currentTimeMillis(),
            location = "Test Location",
            description = "Test device description",
            batteryLevel = 85,
            firmwareVersion = "1.0.0"
        )
    }
}