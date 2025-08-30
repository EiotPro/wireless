package com.iotlogic.blynk.ui.e2e

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iotlogic.blynk.MainActivity
import com.iotlogic.blynk.domain.model.Device
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EndToEndUserFlowTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun completeUserFlow_LoginToDeviceManagement() {
        // Step 1: User sees login screen on app launch
        composeTestRule.onNodeWithText("Welcome Back").assertExists()
        composeTestRule.onNodeWithText("Sign in to your account").assertExists()

        // Step 2: User enters credentials and logs in
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Login").performClick()

        // Step 3: After successful login, user should see main dashboard
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithText("IoT Devices").fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Step 4: User sees device list (initially empty)
        composeTestRule.onNodeWithText("IoT Devices").assertExists()
        composeTestRule.onNodeWithText("No devices found").assertExists()

        // Step 5: User taps "Scan Devices" to discover devices
        composeTestRule.onNodeWithText("Scan Devices").performClick()

        // Step 6: Wait for scan to complete and devices to appear
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                composeTestRule.onAllNodesWithTag("device_card").fetchSemanticsNodes().isNotEmpty()
            } catch (e: AssertionError) {
                false
            }
        }

        // Step 7: User selects a discovered device
        composeTestRule.onAllNodesWithTag("device_card").onFirst().performClick()

        // Step 8: User should see device details screen
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText("Device Details").fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Step 9: User configures the device
        composeTestRule.onNodeWithText("Configure").performClick()
        
        // Step 10: User updates device settings
        composeTestRule.onNodeWithText("Update Interval").assertExists()
        composeTestRule.onNodeWithText("30").performTextClearance()
        composeTestRule.onNodeWithText("30").performTextInput("60")
        
        // Step 11: User saves configuration
        composeTestRule.onNodeWithText("Save").performClick()

        // Step 12: User navigates back to device list
        composeTestRule.onNodeWithContentDescription("Navigate up").performClick()

        // Step 13: User should see the configured device in the list
        composeTestRule.onNodeWithText("IoT Devices").assertExists()
        composeTestRule.onAllNodesWithTag("device_card").onFirst().assertExists()
    }

    @Test
    fun deviceControlFlow_TurnDeviceOnOff() {
        // Assume user is already logged in and has devices
        simulateLoggedInState()

        // Step 1: User taps on a controllable device
        composeTestRule.onNodeWithText("Smart Light").performClick()

        // Step 2: User sees device control screen
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText("Device Controls").fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Step 3: User turns device on
        composeTestRule.onNodeWithText("Turn On").performClick()

        // Step 4: User should see confirmation
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText("Device turned on").fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Step 5: User adjusts brightness
        composeTestRule.onNodeWithContentDescription("Brightness slider").performTouchInput {
            swipeRight()
        }

        // Step 6: User changes color
        composeTestRule.onNodeWithText("Color").performClick()
        composeTestRule.onNodeWithContentDescription("Red color").performClick()
        composeTestRule.onNodeWithText("Apply").performClick()

        // Step 7: User turns device off
        composeTestRule.onNodeWithText("Turn Off").performClick()

        // Step 8: User should see confirmation
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText("Device turned off").fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    @Test
    fun addDeviceManuallyFlow() {
        // Assume user is already logged in
        simulateLoggedInState()

        // Step 1: User taps "Add Device" button
        composeTestRule.onNodeWithContentDescription("Add device").performClick()

        // Step 2: User sees add device options
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText("Add Device").fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Step 3: User selects "Manual Setup"
        composeTestRule.onNodeWithText("Manual Setup").performClick()

        // Step 4: User fills in device information
        composeTestRule.onNodeWithText("Device Name").performTextInput("My Custom Sensor")
        composeTestRule.onNodeWithText("Device Type").performClick()
        composeTestRule.onNodeWithText("Sensor").performClick()
        
        composeTestRule.onNodeWithText("Protocol").performClick()
        composeTestRule.onNodeWithText("WiFi").performClick()
        
        composeTestRule.onNodeWithText("IP Address").performTextInput("192.168.1.100")
        composeTestRule.onNodeWithText("Port").performTextInput("8080")

        // Step 5: User saves the device
        composeTestRule.onNodeWithText("Add Device").performClick()

        // Step 6: User should see success message
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithText("Device added successfully").fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Step 7: User should see the new device in the list
        composeTestRule.onNodeWithText("My Custom Sensor").assertExists()
    }

    @Test
    fun qrCodeScanningFlow() {
        // Assume user is already logged in
        simulateLoggedInState()

        // Step 1: User taps "Add Device" button
        composeTestRule.onNodeWithContentDescription("Add device").performClick()

        // Step 2: User selects "Scan QR Code"
        composeTestRule.onNodeWithText("Scan QR Code").performClick()

        // Step 3: User sees camera permission request
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText("Camera Permission Required").fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Step 4: User grants camera permission
        composeTestRule.onNodeWithText("Grant Permission").performClick()

        // Step 5: User sees QR scanner screen
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText("Scan QR Code").fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Step 6: Simulate QR code detection
        // In a real test, this would involve pointing camera at QR code
        simulateQRCodeDetection()

        // Step 7: User sees device information parsed from QR code
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithText("Device Found").fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Step 8: User confirms adding the device
        composeTestRule.onNodeWithText("Add Device").performClick()

        // Step 9: User should see success message
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithText("Device added successfully").fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    @Test
    fun telemetryViewingFlow() {
        // Assume user is logged in and has devices with telemetry
        simulateLoggedInStateWithTelemetry()

        // Step 1: User selects a device with telemetry data
        composeTestRule.onNodeWithText("Temperature Sensor").performClick()

        // Step 2: User navigates to telemetry tab
        composeTestRule.onNodeWithText("Telemetry").performClick()

        // Step 3: User sees telemetry charts
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithTag("telemetry_chart").fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Step 4: User changes time range
        composeTestRule.onNodeWithText("Last 24 Hours").performClick()
        composeTestRule.onNodeWithText("Last Week").performClick()

        // Step 5: User sees updated chart data
        composeTestRule.onNodeWithTag("telemetry_chart").assertExists()

        // Step 6: User switches between different sensor types
        composeTestRule.onNodeWithText("Temperature").assertExists()
        composeTestRule.onNodeWithText("Humidity").performClick()

        // Step 7: User sees humidity chart
        composeTestRule.onNodeWithTag("telemetry_chart").assertExists()

        // Step 8: User exports data
        composeTestRule.onNodeWithContentDescription("Export data").performClick()
        composeTestRule.onNodeWithText("Export as CSV").performClick()

        // Step 9: User sees export confirmation
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText("Data exported successfully").fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    @Test
    fun settingsAndProfileFlow() {
        // Assume user is logged in
        simulateLoggedInState()

        // Step 1: User opens navigation drawer
        composeTestRule.onNodeWithContentDescription("Open navigation drawer").performClick()

        // Step 2: User navigates to settings
        composeTestRule.onNodeWithText("Settings").performClick()

        // Step 3: User sees settings screen
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText("Settings").fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Step 4: User updates notification preferences
        composeTestRule.onNodeWithText("Notifications").performClick()
        composeTestRule.onNodeWithText("Device Alerts").assertExists()
        composeTestRule.onNodeWithTag("device_alerts_switch").performClick()

        // Step 5: User configures quiet hours
        composeTestRule.onNodeWithText("Quiet Hours").performClick()
        composeTestRule.onNodeWithText("Enable Quiet Hours").performClick()
        composeTestRule.onNodeWithText("Start Time").performClick()
        composeTestRule.onNodeWithText("22:00").performClick()

        // Step 6: User saves notification settings
        composeTestRule.onNodeWithText("Save").performClick()

        // Step 7: User navigates to profile section
        composeTestRule.onNodeWithContentDescription("Navigate up").performClick()
        composeTestRule.onNodeWithText("Profile").performClick()

        // Step 8: User updates profile information
        composeTestRule.onNodeWithText("Edit Profile").performClick()
        composeTestRule.onNodeWithText("Display Name").performTextClearance()
        composeTestRule.onNodeWithText("Display Name").performTextInput("Updated Name")
        composeTestRule.onNodeWithText("Save").performClick()

        // Step 9: User sees success message
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText("Profile updated successfully").fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    @Test
    fun offlineToOnlineFlow() {
        // Assume user is logged in
        simulateLoggedInState()

        // Step 1: Simulate network disconnect
        simulateNetworkDisconnect()

        // Step 2: User tries to control a device
        composeTestRule.onNodeWithText("Smart Light").performClick()
        composeTestRule.onNodeWithText("Turn On").performClick()

        // Step 3: User sees offline mode indicator
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                composeTestRule.onNodeWithText("Command queued for offline execution").fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Step 4: User performs multiple actions while offline
        composeTestRule.onNodeWithText("Turn Off").performClick()
        composeTestRule.onNodeWithText("Turn On").performClick()

        // Step 5: User sees pending commands indicator
        composeTestRule.onNodeWithText("3 commands pending").assertExists()

        // Step 6: Simulate network reconnect
        simulateNetworkReconnect()

        // Step 7: User sees sync progress
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithText("Syncing...").fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Step 8: User sees sync completion
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                composeTestRule.onNodeWithText("All commands synchronized").fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    // Helper functions to simulate app states
    private fun simulateLoggedInState() {
        // This would typically involve setting up mock data or using test hooks
        // to put the app in a logged-in state with sample devices
    }

    private fun simulateLoggedInStateWithTelemetry() {
        // Setup state with devices that have telemetry data
    }

    private fun simulateQRCodeDetection() {
        // This would trigger the QR code detection mechanism
        // In a real implementation, this might involve injecting test data
    }

    private fun simulateNetworkDisconnect() {
        // This would simulate network connectivity loss
    }

    private fun simulateNetworkReconnect() {
        // This would simulate network connectivity restoration
    }
}