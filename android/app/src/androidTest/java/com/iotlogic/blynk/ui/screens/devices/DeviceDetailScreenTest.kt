package com.iotlogic.blynk.ui.screens.devices

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iotlogic.blynk.domain.model.Device
import com.iotlogic.blynk.domain.model.Telemetry
import com.iotlogic.blynk.ui.screens.devices.DeviceDetailScreen
import com.iotlogic.blynk.ui.theme.IoTLogicTheme
import com.iotlogic.blynk.ui.viewmodel.DeviceDetailViewModel
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
class DeviceDetailScreenTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: DeviceDetailViewModel
    private lateinit var deviceFlow: MutableStateFlow<Device?>
    private lateinit var telemetryFlow: MutableStateFlow<List<Telemetry>>
    private lateinit var uiStateFlow: MutableStateFlow<DeviceDetailViewModel.UiState>

    @Before
    fun setUp() {
        hiltRule.inject()

        // Create mock ViewModel
        mockViewModel = mockk(relaxed = true)
        deviceFlow = MutableStateFlow(null)
        telemetryFlow = MutableStateFlow(emptyList())
        uiStateFlow = MutableStateFlow(DeviceDetailViewModel.UiState())

        every { mockViewModel.device } returns deviceFlow
        every { mockViewModel.telemetryData } returns telemetryFlow
        every { mockViewModel.uiState } returns uiStateFlow
    }

    @Test
    fun deviceDetailScreen_LoadingState_ShowsProgressIndicator() {
        // Given loading state
        uiStateFlow.value = DeviceDetailViewModel.UiState(isLoading = true)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceDetailScreen(
                    deviceId = "device1",
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Loading").assertExists()
    }

    @Test
    fun deviceDetailScreen_WithDevice_ShowsDeviceInformation() {
        // Given device data
        val device = createTestDevice()
        deviceFlow.value = device
        uiStateFlow.value = DeviceDetailViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceDetailScreen(
                    deviceId = device.id,
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Smart Temperature Sensor").assertExists()
        composeTestRule.onNodeWithText("Temperature and humidity monitoring").assertExists()
        composeTestRule.onNodeWithText("BLE").assertExists()
        composeTestRule.onNodeWithText("Living Room").assertExists()
        composeTestRule.onNodeWithText("Online").assertExists()
        composeTestRule.onNodeWithText("85%").assertExists() // Battery level
        composeTestRule.onNodeWithText("v1.2.3").assertExists() // Firmware version
    }

    @Test
    fun deviceDetailScreen_ClickNavigateBack_CallsCallback() {
        // Given
        var navigateBackCalled = false
        val device = createTestDevice()
        deviceFlow.value = device
        uiStateFlow.value = DeviceDetailViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceDetailScreen(
                    deviceId = device.id,
                    onNavigateBack = { navigateBackCalled = true },
                    viewModel = mockViewModel
                )
            }
        }

        // Click back button
        composeTestRule.onNodeWithContentDescription("Navigate back").performClick()

        // Then
        assert(navigateBackCalled)
    }

    @Test
    fun deviceDetailScreen_ControlsTab_ShowsDeviceControls() {
        // Given controllable device
        val device = createTestDevice().copy(
            type = "actuator",
            capabilities = listOf("turn_on", "turn_off", "set_brightness", "set_color")
        )
        deviceFlow.value = device
        uiStateFlow.value = DeviceDetailViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceDetailScreen(
                    deviceId = device.id,
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Switch to controls tab
        composeTestRule.onNodeWithText("Controls").performClick()

        // Then
        composeTestRule.onNodeWithText("Turn On").assertExists()
        composeTestRule.onNodeWithText("Turn Off").assertExists()
        composeTestRule.onNodeWithText("Brightness").assertExists()
        composeTestRule.onNodeWithText("Color").assertExists()
    }

    @Test
    fun deviceDetailScreen_TurnOnDevice_CallsViewModel() {
        // Given controllable device
        val device = createTestDevice().copy(
            type = "actuator",
            capabilities = listOf("turn_on", "turn_off")
        )
        deviceFlow.value = device
        uiStateFlow.value = DeviceDetailViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceDetailScreen(
                    deviceId = device.id,
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Switch to controls tab and turn on device
        composeTestRule.onNodeWithText("Controls").performClick()
        composeTestRule.onNodeWithText("Turn On").performClick()

        // Then
        verify { mockViewModel.sendCommand("turn_on", any()) }
    }

    @Test
    fun deviceDetailScreen_AdjustBrightness_CallsViewModel() {
        // Given device with brightness control
        val device = createTestDevice().copy(
            type = "actuator",
            capabilities = listOf("set_brightness")
        )
        deviceFlow.value = device
        uiStateFlow.value = DeviceDetailViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceDetailScreen(
                    deviceId = device.id,
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Switch to controls tab and adjust brightness
        composeTestRule.onNodeWithText("Controls").performClick()
        composeTestRule.onNodeWithContentDescription("Brightness slider").performTouchInput {
            swipeRight()
        }

        // Then
        verify { mockViewModel.sendCommand("set_brightness", any()) }
    }

    @Test
    fun deviceDetailScreen_TelemetryTab_ShowsTelemetryData() {
        // Given device with telemetry
        val device = createTestDevice()
        val telemetryData = createMockTelemetryData(device.id)
        deviceFlow.value = device
        telemetryFlow.value = telemetryData
        uiStateFlow.value = DeviceDetailViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceDetailScreen(
                    deviceId = device.id,
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Switch to telemetry tab
        composeTestRule.onNodeWithText("Telemetry").performClick()

        // Then
        composeTestRule.onNodeWithTag("telemetry_chart").assertExists()
        composeTestRule.onNodeWithText("Temperature").assertExists()
        composeTestRule.onNodeWithText("25.5°C").assertExists()
        composeTestRule.onNodeWithText("Humidity").assertExists()
        composeTestRule.onNodeWithText("60.0%").assertExists()
    }

    @Test
    fun deviceDetailScreen_TelemetryTimeRange_UpdatesChart() {
        // Given device with telemetry
        val device = createTestDevice()
        val telemetryData = createMockTelemetryData(device.id)
        deviceFlow.value = device
        telemetryFlow.value = telemetryData
        uiStateFlow.value = DeviceDetailViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceDetailScreen(
                    deviceId = device.id,
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Switch to telemetry tab and change time range
        composeTestRule.onNodeWithText("Telemetry").performClick()
        composeTestRule.onNodeWithText("Last 24 Hours").performClick()
        composeTestRule.onNodeWithText("Last Week").performClick()

        // Then
        verify { mockViewModel.updateTimeRange(any()) }
    }

    @Test
    fun deviceDetailScreen_ConfigurationTab_ShowsSettings() {
        // Given device with configuration
        val device = createTestDevice().copy(
            configurations = mapOf(
                "updateInterval" to 30,
                "alertThreshold" to 25.0,
                "enableNotifications" to true
            )
        )
        deviceFlow.value = device
        uiStateFlow.value = DeviceDetailViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceDetailScreen(
                    deviceId = device.id,
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Switch to configuration tab
        composeTestRule.onNodeWithText("Settings").performClick()

        // Then
        composeTestRule.onNodeWithText("Update Interval").assertExists()
        composeTestRule.onNodeWithText("30").assertExists()
        composeTestRule.onNodeWithText("Alert Threshold").assertExists()
        composeTestRule.onNodeWithText("25.0").assertExists()
        composeTestRule.onNodeWithText("Enable Notifications").assertExists()
    }

    @Test
    fun deviceDetailScreen_UpdateConfiguration_CallsViewModel() {
        // Given device with configuration
        val device = createTestDevice().copy(
            configurations = mapOf("updateInterval" to 30)
        )
        deviceFlow.value = device
        uiStateFlow.value = DeviceDetailViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceDetailScreen(
                    deviceId = device.id,
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Switch to configuration tab and update setting
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("30").performTextClearance()
        composeTestRule.onNodeWithText("30").performTextInput("60")
        composeTestRule.onNodeWithText("Save").performClick()

        // Then
        verify { mockViewModel.updateConfiguration(any()) }
    }

    @Test
    fun deviceDetailScreen_OfflineDevice_ShowsOfflineIndicator() {
        // Given offline device
        val device = createTestDevice().copy(isOnline = false)
        deviceFlow.value = device
        uiStateFlow.value = DeviceDetailViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceDetailScreen(
                    deviceId = device.id,
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Offline").assertExists()
        composeTestRule.onNodeWithContentDescription("Device offline").assertExists()
    }

    @Test
    fun deviceDetailScreen_LowBattery_ShowsWarning() {
        // Given device with low battery
        val device = createTestDevice().copy(batteryLevel = 15)
        deviceFlow.value = device
        uiStateFlow.value = DeviceDetailViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceDetailScreen(
                    deviceId = device.id,
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("15%").assertExists()
        composeTestRule.onNodeWithContentDescription("Low battery warning").assertExists()
    }

    @Test
    fun deviceDetailScreen_ErrorState_ShowsErrorMessage() {
        // Given error state
        uiStateFlow.value = DeviceDetailViewModel.UiState(
            isLoading = false,
            error = "Failed to load device details"
        )

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceDetailScreen(
                    deviceId = "device1",
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Failed to load device details").assertExists()
        composeTestRule.onNodeWithText("Retry").assertExists()
    }

    @Test
    fun deviceDetailScreen_RefreshData_CallsViewModel() {
        // Given device data
        val device = createTestDevice()
        deviceFlow.value = device
        uiStateFlow.value = DeviceDetailViewModel.UiState(isLoading = false)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                DeviceDetailScreen(
                    deviceId = device.id,
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Pull to refresh
        composeTestRule.onRoot().performTouchInput {
            swipeDown()
        }

        // Then
        verify { mockViewModel.refreshDevice() }
    }

    // Helper functions
    private fun createTestDevice(): Device {
        return Device(
            id = "device1",
            name = "Smart Temperature Sensor",
            type = "sensor",
            protocol = "BLE",
            macAddress = "AA:BB:CC:DD:EE:FF",
            isOnline = true,
            lastSeen = System.currentTimeMillis(),
            location = "Living Room",
            description = "Temperature and humidity monitoring",
            batteryLevel = 85,
            firmwareVersion = "1.2.3",
            configurations = emptyMap(),
            capabilities = listOf("read_temperature", "read_humidity")
        )
    }

    private fun createMockTelemetryData(deviceId: String): List<Telemetry> {
        val baseTime = System.currentTimeMillis()
        return listOf(
            Telemetry(
                id = "tel1",
                deviceId = deviceId,
                sensorType = "temperature",
                value = 25.5,
                unit = "°C",
                timestamp = baseTime - 3600000, // 1 hour ago
                quality = "good"
            ),
            Telemetry(
                id = "tel2",
                deviceId = deviceId,
                sensorType = "humidity",
                value = 60.0,
                unit = "%",
                timestamp = baseTime - 3600000,
                quality = "good"
            ),
            Telemetry(
                id = "tel3",
                deviceId = deviceId,
                sensorType = "temperature",
                value = 26.0,
                unit = "°C",
                timestamp = baseTime - 1800000, // 30 minutes ago
                quality = "good"
            )
        )
    }
}