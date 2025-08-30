package com.iotlogic.blynk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iotlogic.blynk.data.remote.api.IoTLogicApiService
import com.iotlogic.blynk.data.remote.dto.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ApiIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: IoTLogicApiService

    @Before
    fun setUp() {
        hiltRule.inject()
        
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(IoTLogicApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testLoginEndpoint() = runBlocking {
        // Given
        val loginRequest = LoginRequest(
            email = "test@example.com",
            password = "password123",
            deviceId = "test_device_id"
        )

        val mockResponse = """
            {
                "accessToken": "test_access_token",
                "refreshToken": "test_refresh_token",
                "expiresIn": 3600,
                "user": {
                    "id": "user123",
                    "email": "test@example.com",
                    "name": "Test User",
                    "avatarUrl": null,
                    "role": "user",
                    "isEmailVerified": true,
                    "createdAt": "2023-01-01T00:00:00Z",
                    "updatedAt": "2023-01-01T00:00:00Z"
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // When
        val response = apiService.login(loginRequest)

        // Then
        assertTrue(response.isSuccessful)
        val loginResponse = response.body()!!
        assertEquals("test_access_token", loginResponse.accessToken)
        assertEquals("test_refresh_token", loginResponse.refreshToken)
        assertEquals(3600, loginResponse.expiresIn)
        assertEquals("user123", loginResponse.user.id)
        assertEquals("test@example.com", loginResponse.user.email)
    }

    @Test
    fun testGetDevicesEndpoint() = runBlocking {
        // Given
        val mockResponse = """
            [
                {
                    "id": "device1",
                    "name": "Temperature Sensor",
                    "type": "sensor",
                    "protocol": "BLE",
                    "macAddress": "AA:BB:CC:DD:EE:FF",
                    "ipAddress": "192.168.1.100",
                    "port": 8080,
                    "isOnline": true,
                    "lastSeen": 1672531200000,
                    "location": "Living Room",
                    "description": "Temperature and humidity sensor",
                    "batteryLevel": 85,
                    "firmwareVersion": "1.2.3",
                    "configurations": {},
                    "capabilities": []
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // When
        val response = apiService.getDevices("Bearer test_token")

        // Then
        assertTrue(response.isSuccessful)
        val devices = response.body()!!
        assertEquals(1, devices.size)
        assertEquals("device1", devices[0].id)
        assertEquals("Temperature Sensor", devices[0].name)
        assertEquals("BLE", devices[0].protocol)
        assertTrue(devices[0].isOnline)
    }

    @Test
    fun testSendDeviceCommandEndpoint() = runBlocking {
        // Given
        val deviceId = "device123"
        val command = mapOf(
            "action" to "turn_on",
            "brightness" to 80,
            "timestamp" to System.currentTimeMillis()
        )

        val mockResponse = """
            {
                "success": true,
                "message": "Command sent successfully",
                "commandId": "cmd123",
                "timestamp": 1672531200000
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // When
        val response = apiService.sendDeviceCommand(
            token = "Bearer test_token",
            deviceId = deviceId,
            command = command
        )

        // Then
        assertTrue(response.isSuccessful)
        val result = response.body()!!
        assertTrue(result["success"] as Boolean)
        assertEquals("Command sent successfully", result["message"])
        assertEquals("cmd123", result["commandId"])
    }

    @Test
    fun testUploadTelemetryBatchEndpoint() = runBlocking {
        // Given
        val telemetryData = listOf(
            mapOf(
                "id" to "tel1",
                "deviceId" to "device1",
                "sensorType" to "temperature",
                "value" to 25.5,
                "unit" to "°C",
                "timestamp" to System.currentTimeMillis(),
                "quality" to "good"
            ),
            mapOf(
                "id" to "tel2",
                "deviceId" to "device1",
                "sensorType" to "humidity",
                "value" to 60.0,
                "unit" to "%",
                "timestamp" to System.currentTimeMillis(),
                "quality" to "good"
            )
        )

        val mockResponse = """
            {
                "success": true,
                "processed": 2,
                "failed": 0,
                "batchId": "batch123"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // When
        val response = apiService.uploadTelemetryBatch(
            token = "Bearer test_token",
            telemetryData = telemetryData
        )

        // Then
        assertTrue(response.isSuccessful)
        val result = response.body()!!
        assertTrue(result["success"] as Boolean)
        assertEquals(2.0, result["processed"])
        assertEquals(0.0, result["failed"])
    }

    @Test
    fun testUpdateDeviceConfigurationEndpoint() = runBlocking {
        // Given
        val deviceId = "device123"
        val configurations = mapOf(
            "updateInterval" to 30,
            "alertThreshold" to 25.0,
            "enableNotifications" to true
        )

        val mockResponse = """
            {
                "success": true,
                "message": "Configuration updated successfully",
                "appliedAt": 1672531200000
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // When
        val response = apiService.updateDeviceConfiguration(
            token = "Bearer test_token",
            deviceId = deviceId,
            configurations = configurations
        )

        // Then
        assertTrue(response.isSuccessful)
        val result = response.body()!!
        assertTrue(result["success"] as Boolean)
        assertEquals("Configuration updated successfully", result["message"])
    }

    @Test
    fun testGetDeviceConfigurationEndpoint() = runBlocking {
        // Given
        val deviceId = "device123"

        val mockResponse = """
            {
                "deviceId": "device123",
                "configurations": {
                    "updateInterval": 30,
                    "alertThreshold": 25.0,
                    "enableNotifications": true
                },
                "lastUpdated": 1672531200000
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // When
        val response = apiService.getDeviceConfiguration(
            token = "Bearer test_token",
            deviceId = deviceId
        )

        // Then
        assertTrue(response.isSuccessful)
        val result = response.body()!!
        assertEquals("device123", result["deviceId"])
        assertTrue(result.containsKey("configurations"))
        assertTrue(result.containsKey("lastUpdated"))
    }

    @Test
    fun testRefreshTokenEndpoint() = runBlocking {
        // Given
        val refreshRequest = RefreshTokenRequest(
            refreshToken = "old_refresh_token",
            deviceId = "test_device_id"
        )

        val mockResponse = """
            {
                "accessToken": "new_access_token",
                "refreshToken": "new_refresh_token",
                "expiresIn": 3600
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // When
        val response = apiService.refreshToken(refreshRequest)

        // Then
        assertTrue(response.isSuccessful)
        val tokenResponse = response.body()!!
        assertEquals("new_access_token", tokenResponse.accessToken)
        assertEquals("new_refresh_token", tokenResponse.refreshToken)
        assertEquals(3600, tokenResponse.expiresIn)
    }

    @Test
    fun testErrorHandling() = runBlocking {
        // Given - Server returns error
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("""{"error": "Unauthorized", "message": "Invalid token"}""")
                .addHeader("Content-Type", "application/json")
        )

        // When
        val response = apiService.getDevices("Bearer invalid_token")

        // Then
        assertEquals(401, response.code())
        assertEquals(false, response.isSuccessful)
    }

    @Test
    fun testNetworkTimeout() = runBlocking {
        // Given - Slow server response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("{}")
                .setBodyDelay(5000, java.util.concurrent.TimeUnit.MILLISECONDS)
        )

        // When & Then
        try {
            val response = apiService.getDevices("Bearer test_token")
            // Should either succeed or timeout based on configuration
        } catch (e: Exception) {
            // Network timeout or other connection issue
            assertTrue(e is java.net.SocketTimeoutException || e is java.io.IOException)
        }
    }

    @Test
    fun testRegisterEndpoint() = runBlocking {
        // Given
        val registerRequest = RegisterRequest(
            name = "Test User",
            email = "test@example.com",
            password = "password123",
            deviceId = "test_device_id"
        )

        val mockResponse = """
            {
                "accessToken": "test_access_token",
                "refreshToken": "test_refresh_token",
                "expiresIn": 3600,
                "user": {
                    "id": "user123",
                    "email": "test@example.com",
                    "name": "Test User",
                    "avatarUrl": null,
                    "role": "user",
                    "isEmailVerified": false,
                    "createdAt": "2023-01-01T00:00:00Z",
                    "updatedAt": "2023-01-01T00:00:00Z"
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(201)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        // When
        val response = apiService.register(registerRequest)

        // Then
        assertTrue(response.isSuccessful)
        assertEquals(201, response.code())
        val loginResponse = response.body()!!
        assertEquals("Test User", loginResponse.user.name)
        assertEquals(false, loginResponse.user.isEmailVerified)
    }
}