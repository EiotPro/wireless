# API Integration Guide

This guide covers the integration with IoT Logic backend services, including authentication, device management, telemetry data, and real-time communication.

## 🔗 Base Configuration

### API Endpoints

```kotlin
// Production
const val PROD_BASE_URL = "https://api.iotlogic.com/v1/"

// Staging  
const val STAGING_BASE_URL = "https://staging-api.iotlogic.com/v1/"

// Development
const val DEV_BASE_URL = "https://dev-api.iotlogic.com/v1/"
```

### Network Configuration

```kotlin
// Retrofit Configuration
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor())
            .addInterceptor(LoggingInterceptor())
            .build()
    }
}
```

## 🔐 Authentication API

### Login Endpoint

**POST** `/auth/login`

```kotlin
data class LoginRequest(
    val email: String,
    val password: String,
    val deviceId: String
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: UserDto
)
```

**Example Usage:**

```kotlin
class AuthService @Inject constructor(
    private val apiService: IoTLogicApiService
) {
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val request = LoginRequest(email, password, getDeviceId())
            val response = apiService.login(request)
            
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### Token Refresh

**POST** `/auth/refresh`

```kotlin
data class RefreshTokenRequest(
    val refreshToken: String,
    val deviceId: String
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String?,
    val expiresIn: Long
)
```

### Registration

**POST** `/auth/register`

```kotlin
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val deviceId: String
)
```

## 📱 Device Management API

### Get Devices

**GET** `/devices`

```kotlin
// Response
data class DeviceDto(
    val id: String,
    val name: String,
    val type: String,
    val protocol: String,
    val macAddress: String?,
    val ipAddress: String?,
    val port: Int?,
    val isOnline: Boolean,
    val lastSeen: Long,
    val location: String,
    val description: String,
    val batteryLevel: Int?,
    val firmwareVersion: String?,
    val configurations: Map<String, Any>,
    val capabilities: List<String>
)
```

### Add Device

**POST** `/devices`

```kotlin
data class CreateDeviceRequest(
    val name: String,
    val type: String,
    val protocol: String,
    val macAddress: String?,
    val ipAddress: String?,
    val port: Int?,
    val location: String,
    val description: String,
    val configurations: Map<String, Any> = emptyMap()
)
```

### Update Device

**PUT** `/devices/{deviceId}`

```kotlin
data class UpdateDeviceRequest(
    val name: String?,
    val location: String?,
    val description: String?,
    val configurations: Map<String, Any>?
)
```

### Delete Device

**DELETE** `/devices/{deviceId}`

### Device Commands

**POST** `/devices/{deviceId}/commands`

```kotlin
data class DeviceCommandRequest(
    val command: String,
    val parameters: Map<String, Any>,
    val timestamp: Long = System.currentTimeMillis()
)

data class CommandResponse(
    val commandId: String,
    val status: String,
    val result: String?,
    val executedAt: Long
)
```

**Command Examples:**

```kotlin
// Turn on/off device
val turnOnCommand = DeviceCommandRequest(
    command = "turn_on",
    parameters = emptyMap()
)

// Set brightness
val brightnessCommand = DeviceCommandRequest(
    command = "set_brightness",
    parameters = mapOf("value" to 75)
)

// Set color
val colorCommand = DeviceCommandRequest(
    command = "set_color",
    parameters = mapOf(
        "red" to 255,
        "green" to 128,
        "blue" to 0
    )
)
```

## 📊 Telemetry API

### Upload Telemetry Data

**POST** `/telemetry/batch`

```kotlin
data class TelemetryBatchRequest(
    val telemetryData: List<TelemetryUploadDto>
)

data class TelemetryUploadDto(
    val deviceId: String,
    val sensorType: String,
    val value: Double,
    val unit: String,
    val timestamp: Long,
    val quality: String = "good",
    val metadata: Map<String, Any> = emptyMap()
)

data class TelemetryBatchResponse(
    val success: Boolean,
    val processed: Int,
    val failed: Int,
    val batchId: String,
    val errors: List<String> = emptyList()
)
```

### Get Telemetry Data

**GET** `/telemetry/devices/{deviceId}`

**Query Parameters:**
- `sensorType`: Filter by sensor type
- `startTime`: Start timestamp (milliseconds)
- `endTime`: End timestamp (milliseconds)
- `limit`: Maximum number of records (default: 1000)
- `offset`: Pagination offset

```kotlin
data class TelemetryQueryParams(
    val sensorType: String? = null,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val limit: Int = 1000,
    val offset: Int = 0
)
```

### Telemetry Aggregation

**GET** `/telemetry/devices/{deviceId}/aggregate`

**Query Parameters:**
- `sensorType`: Sensor type to aggregate
- `interval`: Aggregation interval (hour, day, week, month)
- `function`: Aggregation function (avg, min, max, sum, count)
- `startTime`: Start timestamp
- `endTime`: End timestamp

```kotlin
data class AggregationResponse(
    val deviceId: String,
    val sensorType: String,
    val interval: String,
    val function: String,
    val data: List<AggregationDataPoint>
)

data class AggregationDataPoint(
    val timestamp: Long,
    val value: Double,
    val count: Int
)
```

## ⚙️ Configuration API

### Get Device Configuration

**GET** `/devices/{deviceId}/configuration`

```kotlin
data class DeviceConfigurationResponse(
    val deviceId: String,
    val configurations: Map<String, ConfigurationItem>,
    val lastUpdated: Long
)

data class ConfigurationItem(
    val key: String,
    val value: Any,
    val dataType: String,
    val description: String?,
    val isEditable: Boolean,
    val validationRules: ValidationRules?
)

data class ValidationRules(
    val required: Boolean = false,
    val minValue: Double? = null,
    val maxValue: Double? = null,
    val allowedValues: List<String>? = null,
    val pattern: String? = null
)
```

### Update Device Configuration

**PUT** `/devices/{deviceId}/configuration`

```kotlin
data class UpdateConfigurationRequest(
    val configurations: Map<String, Any>
)
```

## 🔔 Notifications API

### Register FCM Token

**POST** `/notifications/fcm/register`

```kotlin
data class FCMTokenRequest(
    val token: String,
    val deviceId: String,
    val preferences: NotificationPreferences
)

data class NotificationPreferences(
    val deviceAlerts: Boolean = true,
    val systemUpdates: Boolean = true,
    val geofenceAlerts: Boolean = true,
    val connectionAlerts: Boolean = false
)
```

### Update Notification Preferences

**PUT** `/notifications/preferences`

```kotlin
data class UpdatePreferencesRequest(
    val preferences: NotificationPreferences
)
```

## 🌐 Real-time Communication

### WebSocket Connection

**WebSocket** `/ws/devices`

```kotlin
class WebSocketManager @Inject constructor() {
    
    fun connect(token: String) {
        val request = Request.Builder()
            .url("wss://api.iotlogic.com/v1/ws/devices")
            .addHeader("Authorization", "Bearer $token")
            .build()
            
        webSocket = client.newWebSocket(request, webSocketListener)
    }
    
    private val webSocketListener = object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            handleMessage(text)
        }
    }
}
```

### WebSocket Message Types

```kotlin
// Device status update
data class DeviceStatusMessage(
    val type: String = "device_status",
    val deviceId: String,
    val isOnline: Boolean,
    val batteryLevel: Int?,
    val timestamp: Long
)

// Real-time telemetry
data class TelemetryMessage(
    val type: String = "telemetry",
    val deviceId: String,
    val sensorType: String,
    val value: Double,
    val unit: String,
    val timestamp: Long
)

// Command response
data class CommandResponseMessage(
    val type: String = "command_response",
    val commandId: String,
    val deviceId: String,
    val status: String,
    val result: String?,
    val timestamp: Long
)
```

## 🔄 Error Handling

### HTTP Status Codes

```kotlin
class ApiErrorHandler {
    fun handleError(response: Response<*>): ApiError {
        return when (response.code()) {
            400 -> ApiError.BadRequest(response.message())
            401 -> ApiError.Unauthorized("Invalid or expired token")
            403 -> ApiError.Forbidden("Insufficient permissions")
            404 -> ApiError.NotFound("Resource not found")
            429 -> ApiError.RateLimited("Too many requests")
            500 -> ApiError.ServerError("Internal server error")
            else -> ApiError.Unknown("Unknown error: ${response.code()}")
        }
    }
}

sealed class ApiError : Exception() {
    data class BadRequest(override val message: String) : ApiError()
    data class Unauthorized(override val message: String) : ApiError()
    data class Forbidden(override val message: String) : ApiError()
    data class NotFound(override val message: String) : ApiError()
    data class RateLimited(override val message: String) : ApiError()
    data class ServerError(override val message: String) : ApiError()
    data class NetworkError(override val message: String) : ApiError()
    data class Unknown(override val message: String) : ApiError()
}
```

### Error Response Format

```kotlin
data class ErrorResponse(
    val error: String,
    val message: String,
    val code: Int,
    val timestamp: Long,
    val details: Map<String, Any>? = null
)
```

## 🔒 Security Best Practices

### API Key Management

```kotlin
class AuthInterceptor @Inject constructor(
    private val authPreferences: AuthPreferences
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = authPreferences.getAuthToken()
        
        val authenticatedRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("User-Agent", getUserAgent())
            .build()
            
        val response = chain.proceed(authenticatedRequest)
        
        // Handle token refresh if needed
        if (response.code == 401) {
            return handleTokenRefresh(chain, request)
        }
        
        return response
    }
}
```

### Certificate Pinning

```kotlin
val certificatePinner = CertificatePinner.Builder()
    .add("api.iotlogic.com", "sha256/HASH_VALUE")
    .build()

val client = OkHttpClient.Builder()
    .certificatePinner(certificatePinner)
    .build()
```

## 📝 Usage Examples

### Complete Device Workflow

```kotlin
class DeviceService @Inject constructor(
    private val apiService: IoTLogicApiService
) {
    
    suspend fun addAndConfigureDevice(): Result<Device> = withContext(Dispatchers.IO) {
        try {
            // 1. Add device
            val createRequest = CreateDeviceRequest(
                name = "Smart Thermostat",
                type = "actuator",
                protocol = "WiFi",
                ipAddress = "192.168.1.50",
                port = 8080,
                location = "Living Room",
                description = "Smart thermostat with temperature control"
            )
            
            val deviceResponse = apiService.createDevice(createRequest)
            if (!deviceResponse.isSuccessful) {
                return@withContext Result.failure(Exception("Failed to create device"))
            }
            
            val device = deviceResponse.body()!!
            
            // 2. Configure device
            val configRequest = UpdateConfigurationRequest(
                configurations = mapOf(
                    "targetTemperature" to 22.0,
                    "mode" to "auto",
                    "updateInterval" to 60
                )
            )
            
            val configResponse = apiService.updateDeviceConfiguration(device.id, configRequest)
            if (!configResponse.isSuccessful) {
                return@withContext Result.failure(Exception("Failed to configure device"))
            }
            
            // 3. Send initial command
            val commandRequest = DeviceCommandRequest(
                command = "set_temperature",
                parameters = mapOf("temperature" to 22.0)
            )
            
            apiService.sendDeviceCommand(device.id, commandRequest)
            
            Result.success(device.toDomainModel())
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### Batch Telemetry Upload

```kotlin
class TelemetryService @Inject constructor(
    private val apiService: IoTLogicApiService
) {
    
    suspend fun uploadTelemetryBatch(
        telemetryList: List<Telemetry>
    ): Result<TelemetryBatchResponse> = withContext(Dispatchers.IO) {
        try {
            val uploadData = telemetryList.map { telemetry ->
                TelemetryUploadDto(
                    deviceId = telemetry.deviceId,
                    sensorType = telemetry.sensorType,
                    value = telemetry.value,
                    unit = telemetry.unit,
                    timestamp = telemetry.timestamp,
                    quality = telemetry.quality,
                    metadata = telemetry.metadata
                )
            }
            
            val request = TelemetryBatchRequest(uploadData)
            val response = apiService.uploadTelemetryBatch(request)
            
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Upload failed: ${response.message()}"))
            }
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

## 🧪 Testing API Integration

### Mock API Responses

```kotlin
class MockApiService : IoTLogicApiService {
    
    override suspend fun getDevices(): Response<List<DeviceDto>> {
        val mockDevices = listOf(
            DeviceDto(
                id = "device1",
                name = "Test Sensor",
                type = "sensor",
                protocol = "BLE",
                isOnline = true,
                // ... other properties
            )
        )
        return Response.success(mockDevices)
    }
    
    // ... other mock implementations
}
```

### Integration Testing

```kotlin
@Test
fun testDeviceApiIntegration() = runTest {
    val mockWebServer = MockWebServer()
    mockWebServer.start()
    
    val response = """
        {
            "id": "device123",
            "name": "Test Device",
            "type": "sensor",
            "protocol": "BLE",
            "isOnline": true
        }
    """.trimIndent()
    
    mockWebServer.enqueue(
        MockResponse()
            .setResponseCode(200)
            .setBody(response)
    )
    
    val apiService = createTestApiService(mockWebServer.url("/"))
    val result = apiService.getDevice("device123")
    
    assertTrue(result.isSuccessful)
    assertEquals("Test Device", result.body()?.name)
    
    mockWebServer.shutdown()
}
```

## 📚 Additional Resources

- [Authentication Flow Diagram](../diagrams/auth-flow.png)
- [API Rate Limiting Guide](rate-limiting.md)
- [WebSocket Protocol Specification](websocket-spec.md)
- [Error Code Reference](error-codes.md)
- [Postman Collection](../postman/iot-logic-api.json)