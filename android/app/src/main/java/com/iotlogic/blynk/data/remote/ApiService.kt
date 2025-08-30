package com.iotlogic.blynk.data.remote

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Authentication endpoints
    @POST("api/login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("api/register.php")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
    
    @POST("api/mfa.php")
    suspend fun verifyMfa(@Body request: MfaRequest): Response<MfaResponse>
    
    // Device endpoints
    @GET("api/devices.php")
    suspend fun getDevices(@Header("Authorization") token: String): Response<DevicesResponse>
    
    @GET("api/devices.php")
    suspend fun getDeviceById(
        @Header("Authorization") token: String,
        @Query("id") deviceId: String
    ): Response<DeviceResponse>
    
    @POST("api/devices.php")
    suspend fun createDevice(
        @Header("Authorization") token: String,
        @Body request: CreateDeviceRequest
    ): Response<DeviceResponse>
    
    @PUT("api/devices.php")
    suspend fun updateDevice(
        @Header("Authorization") token: String,
        @Body request: UpdateDeviceRequest
    ): Response<DeviceResponse>
    
    @DELETE("api/devices.php")
    suspend fun deleteDevice(
        @Header("Authorization") token: String,
        @Query("id") deviceId: String
    ): Response<BasicResponse>
    
    // Telemetry endpoints
    @GET("api/telemetry.php")
    suspend fun getTelemetry(
        @Header("Authorization") token: String,
        @Query("device_id") deviceId: String? = null,
        @Query("sensor_type") sensorType: String? = null,
        @Query("start_time") startTime: Long? = null,
        @Query("end_time") endTime: Long? = null,
        @Query("limit") limit: Int? = null
    ): Response<TelemetryResponse>
    
    @POST("api/telemetry.php")
    suspend fun submitTelemetry(
        @Header("Authorization") token: String,
        @Body request: SubmitTelemetryRequest
    ): Response<BasicResponse>
    
    @POST("api/telemetry.php")
    suspend fun submitTelemetryBatch(
        @Header("Authorization") token: String,
        @Body request: SubmitTelemetryBatchRequest
    ): Response<BasicResponse>
    
    // Command endpoints
    @GET("api/command.php")
    suspend fun getCommands(
        @Header("Authorization") token: String,
        @Query("device_id") deviceId: String
    ): Response<CommandsResponse>
    
    @POST("api/command.php")
    suspend fun sendCommand(
        @Header("Authorization") token: String,
        @Body request: SendCommandRequest
    ): Response<CommandResponse>
    
    @POST("api/device_command.php")
    suspend fun sendDeviceCommand(
        @Header("Authorization") token: String,
        @Query("deviceId") deviceId: String,
        @Body command: Map<String, Any>
    ): Response<BasicResponse>
    
    @PUT("api/command.php")
    suspend fun updateCommandStatus(
        @Header("Authorization") token: String,
        @Body request: UpdateCommandStatusRequest
    ): Response<BasicResponse>
    
    // Real-time endpoints
    @GET("api/realtime.php")
    suspend fun getRealtimeData(
        @Header("Authorization") token: String,
        @Query("device_id") deviceId: String? = null
    ): Response<RealtimeDataResponse>
    
    // Analytics endpoints
    @GET("api/analytics.php")
    suspend fun getAnalytics(
        @Header("Authorization") token: String,
        @Query("device_id") deviceId: String? = null,
        @Query("metric") metric: String? = null,
        @Query("period") period: String? = null
    ): Response<AnalyticsResponse>
    
    // Notifications endpoints
    @GET("api/notifications.php")
    suspend fun getNotifications(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int? = null
    ): Response<NotificationsResponse>
    
    @POST("api/notifications.php")
    suspend fun markNotificationRead(
        @Header("Authorization") token: String,
        @Body request: MarkNotificationRequest
    ): Response<BasicResponse>
    
    @POST("api/fcm_token.php")
    suspend fun registerFCMToken(
        @Query("token") token: String,
        @Query("deviceId") deviceId: String,
        @Query("platform") platform: String
    ): Response<BasicResponse>
    
    // Multi-protocol endpoints
    @GET("api/multiprotocol.php")
    suspend fun getProtocolInfo(
        @Header("Authorization") token: String,
        @Query("protocol") protocol: String
    ): Response<ProtocolInfoResponse>
    
    @POST("api/multiprotocol.php")
    suspend fun configureProtocol(
        @Header("Authorization") token: String,
        @Body request: ConfigureProtocolRequest
    ): Response<BasicResponse>
    
    // RBAC endpoints
    @GET("api/rbac.php")
    suspend fun getUserRoles(
        @Header("Authorization") token: String
    ): Response<UserRolesResponse>
    
    @POST("api/rbac.php")
    suspend fun updateUserRole(
        @Header("Authorization") token: String,
        @Body request: UpdateUserRoleRequest
    ): Response<BasicResponse>
    
    // Security endpoints
    @GET("api/security.php")
    suspend fun getSecurityEvents(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int? = null
    ): Response<SecurityEventsResponse>
    
    @POST("api/security.php")
    suspend fun reportSecurityEvent(
        @Header("Authorization") token: String,
        @Body request: ReportSecurityEventRequest
    ): Response<BasicResponse>
    
    // Dashboard endpoints
    @GET("api/dashboard.php")
    suspend fun getDashboardData(
        @Header("Authorization") token: String
    ): Response<DashboardResponse>
    
    @POST("api/dashboard.php")
    suspend fun updateDashboardConfig(
        @Header("Authorization") token: String,
        @Body request: UpdateDashboardConfigRequest
    ): Response<BasicResponse>
}

// Request/Response data classes
data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: UserInfo?,
    val mfaRequired: Boolean = false
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val userId: String?
)

data class MfaRequest(
    val username: String,
    val code: String
)

data class MfaResponse(
    val success: Boolean,
    val message: String,
    val token: String?
)

data class UserInfo(
    val id: String,
    val username: String,
    val email: String,
    val role: String,
    val createdAt: String
)

data class DevicesResponse(
    val success: Boolean,
    val message: String,
    val devices: List<DeviceDto>
)

data class DeviceResponse(
    val success: Boolean,
    val message: String,
    val device: DeviceDto?
)

data class CreateDeviceRequest(
    val name: String,
    val type: String,
    val protocol: String,
    val configuration: Map<String, String> = emptyMap()
)

data class UpdateDeviceRequest(
    val id: String,
    val name: String?,
    val type: String?,
    val status: String?,
    val configuration: Map<String, String>? = null
)

data class DeviceDto(
    val id: String,
    val name: String,
    val type: String,
    val protocol: String,
    val status: String,
    val token: String,
    val userId: String,
    val macAddress: String?,
    val ipAddress: String?,
    val lastSeen: String,
    val batteryLevel: Int?,
    val signalStrength: Int?,
    val location: String?,
    val isOnline: Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class TelemetryResponse(
    val success: Boolean,
    val message: String,
    val telemetry: List<TelemetryDto>
)

data class SubmitTelemetryRequest(
    val deviceToken: String,
    val sensorType: String,
    val value: Double,
    val unit: String?,
    val timestamp: Long = System.currentTimeMillis()
)

data class SubmitTelemetryBatchRequest(
    val deviceToken: String,
    val telemetryData: List<TelemetryDataItem>
)

data class TelemetryDataItem(
    val sensorType: String,
    val value: Double,
    val unit: String?,
    val timestamp: Long
)

data class TelemetryDto(
    val id: String,
    val deviceId: String,
    val sensorType: String,
    val value: Double,
    val unit: String?,
    val timestamp: String,
    val quality: String?
)

data class CommandsResponse(
    val success: Boolean,
    val message: String,
    val commands: List<CommandDto>
)

data class SendCommandRequest(
    val deviceId: String,
    val command: String,
    val parameters: Map<String, Any> = emptyMap()
)

data class CommandResponse(
    val success: Boolean,
    val message: String,
    val commandId: String?
)

data class UpdateCommandStatusRequest(
    val commandId: String,
    val status: String,
    val result: String?
)

data class CommandDto(
    val id: String,
    val deviceId: String,
    val command: String,
    val parameters: String?,
    val status: String,
    val createdAt: String,
    val executedAt: String?
)

data class RealtimeDataResponse(
    val success: Boolean,
    val message: String,
    val data: Map<String, Any>
)

data class AnalyticsResponse(
    val success: Boolean,
    val message: String,
    val analytics: Map<String, Any>
)

data class NotificationsResponse(
    val success: Boolean,
    val message: String,
    val notifications: List<NotificationDto>
)

data class MarkNotificationRequest(
    val notificationId: String,
    val isRead: Boolean = true
)

data class NotificationDto(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: String
)

data class ProtocolInfoResponse(
    val success: Boolean,
    val message: String,
    val protocolInfo: Map<String, Any>
)

data class ConfigureProtocolRequest(
    val protocol: String,
    val configuration: Map<String, Any>
)

data class UserRolesResponse(
    val success: Boolean,
    val message: String,
    val roles: List<String>
)

data class UpdateUserRoleRequest(
    val userId: String,
    val role: String
)

data class SecurityEventsResponse(
    val success: Boolean,
    val message: String,
    val events: List<SecurityEventDto>
)

data class ReportSecurityEventRequest(
    val eventType: String,
    val description: String,
    val metadata: Map<String, Any> = emptyMap()
)

data class SecurityEventDto(
    val id: String,
    val eventType: String,
    val description: String,
    val userId: String?,
    val ipAddress: String,
    val timestamp: String
)

data class DashboardResponse(
    val success: Boolean,
    val message: String,
    val dashboard: DashboardData
)

data class UpdateDashboardConfigRequest(
    val configuration: Map<String, Any>
)

data class DashboardData(
    val deviceCount: Int,
    val onlineDevices: Int,
    val recentTelemetry: List<TelemetryDto>,
    val alerts: List<NotificationDto>,
    val analytics: Map<String, Any>
)

data class BasicResponse(
    val success: Boolean,
    val message: String
)