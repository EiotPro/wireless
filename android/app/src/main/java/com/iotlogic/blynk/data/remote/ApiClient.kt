package com.iotlogic.blynk.data.remote

import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiClient @Inject constructor(
    private val apiService: ApiService
) {
    
    /**
     * Generic method to handle API responses with error handling
     */
    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    Result.success(body)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("API call failed with code: ${response.code()}, message: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Authentication methods
    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return safeApiCall {
            apiService.login(LoginRequest(username, password))
        }
    }
    
    suspend fun register(username: String, email: String, password: String): Result<RegisterResponse> {
        return safeApiCall {
            apiService.register(RegisterRequest(username, email, password))
        }
    }
    
    suspend fun verifyMfa(username: String, code: String): Result<MfaResponse> {
        return safeApiCall {
            apiService.verifyMfa(MfaRequest(username, code))
        }
    }
    
    // Device methods
    suspend fun getDevices(token: String): Result<DevicesResponse> {
        return safeApiCall {
            apiService.getDevices("Bearer $token")
        }
    }
    
    suspend fun getDeviceById(token: String, deviceId: String): Result<DeviceResponse> {
        return safeApiCall {
            apiService.getDeviceById("Bearer $token", deviceId)
        }
    }
    
    suspend fun createDevice(
        token: String,
        name: String,
        type: String,
        protocol: String,
        configuration: Map<String, String> = emptyMap()
    ): Result<DeviceResponse> {
        return safeApiCall {
            apiService.createDevice(
                "Bearer $token",
                CreateDeviceRequest(name, type, protocol, configuration)
            )
        }
    }
    
    suspend fun updateDevice(
        token: String,
        deviceId: String,
        name: String? = null,
        type: String? = null,
        status: String? = null,
        configuration: Map<String, String>? = null
    ): Result<DeviceResponse> {
        return safeApiCall {
            apiService.updateDevice(
                "Bearer $token",
                UpdateDeviceRequest(deviceId, name, type, status, configuration)
            )
        }
    }
    
    suspend fun deleteDevice(token: String, deviceId: String): Result<BasicResponse> {
        return safeApiCall {
            apiService.deleteDevice("Bearer $token", deviceId)
        }
    }
    
    // Telemetry methods
    suspend fun getTelemetry(
        token: String,
        deviceId: String? = null,
        sensorType: String? = null,
        startTime: Long? = null,
        endTime: Long? = null,
        limit: Int? = null
    ): Result<TelemetryResponse> {
        return safeApiCall {
            apiService.getTelemetry("Bearer $token", deviceId, sensorType, startTime, endTime, limit)
        }
    }
    
    suspend fun submitTelemetry(
        token: String,
        deviceToken: String,
        sensorType: String,
        value: Double,
        unit: String? = null,
        timestamp: Long = System.currentTimeMillis()
    ): Result<BasicResponse> {
        return safeApiCall {
            apiService.submitTelemetry(
                "Bearer $token",
                SubmitTelemetryRequest(deviceToken, sensorType, value, unit, timestamp)
            )
        }
    }
    
    suspend fun submitTelemetryBatch(
        token: String,
        deviceToken: String,
        telemetryData: List<TelemetryDataItem>
    ): Result<BasicResponse> {
        return safeApiCall {
            apiService.submitTelemetryBatch(
                "Bearer $token",
                SubmitTelemetryBatchRequest(deviceToken, telemetryData)
            )
        }
    }
    
    // Command methods
    suspend fun getCommands(token: String, deviceId: String): Result<CommandsResponse> {
        return safeApiCall {
            apiService.getCommands("Bearer $token", deviceId)
        }
    }
    
    suspend fun sendCommand(
        token: String,
        deviceId: String,
        command: String,
        parameters: Map<String, Any> = emptyMap()
    ): Result<CommandResponse> {
        return safeApiCall {
            apiService.sendCommand(
                "Bearer $token",
                SendCommandRequest(deviceId, command, parameters)
            )
        }
    }
    
    suspend fun updateCommandStatus(
        token: String,
        commandId: String,
        status: String,
        result: String? = null
    ): Result<BasicResponse> {
        return safeApiCall {
            apiService.updateCommandStatus(
                "Bearer $token",
                UpdateCommandStatusRequest(commandId, status, result)
            )
        }
    }
    
    // Real-time methods
    suspend fun getRealtimeData(token: String, deviceId: String? = null): Result<RealtimeDataResponse> {
        return safeApiCall {
            apiService.getRealtimeData("Bearer $token", deviceId)
        }
    }
    
    // Analytics methods
    suspend fun getAnalytics(
        token: String,
        deviceId: String? = null,
        metric: String? = null,
        period: String? = null
    ): Result<AnalyticsResponse> {
        return safeApiCall {
            apiService.getAnalytics("Bearer $token", deviceId, metric, period)
        }
    }
    
    // Notification methods
    suspend fun getNotifications(token: String, limit: Int? = null): Result<NotificationsResponse> {
        return safeApiCall {
            apiService.getNotifications("Bearer $token", limit)
        }
    }
    
    suspend fun markNotificationRead(token: String, notificationId: String): Result<BasicResponse> {
        return safeApiCall {
            apiService.markNotificationRead(
                "Bearer $token",
                MarkNotificationRequest(notificationId, true)
            )
        }
    }
    
    // Multi-protocol methods
    suspend fun getProtocolInfo(token: String, protocol: String): Result<ProtocolInfoResponse> {
        return safeApiCall {
            apiService.getProtocolInfo("Bearer $token", protocol)
        }
    }
    
    suspend fun configureProtocol(
        token: String,
        protocol: String,
        configuration: Map<String, Any>
    ): Result<BasicResponse> {
        return safeApiCall {
            apiService.configureProtocol(
                "Bearer $token",
                ConfigureProtocolRequest(protocol, configuration)
            )
        }
    }
    
    // RBAC methods
    suspend fun getUserRoles(token: String): Result<UserRolesResponse> {
        return safeApiCall {
            apiService.getUserRoles("Bearer $token")
        }
    }
    
    suspend fun updateUserRole(token: String, userId: String, role: String): Result<BasicResponse> {
        return safeApiCall {
            apiService.updateUserRole(
                "Bearer $token",
                UpdateUserRoleRequest(userId, role)
            )
        }
    }
    
    // Security methods
    suspend fun getSecurityEvents(token: String, limit: Int? = null): Result<SecurityEventsResponse> {
        return safeApiCall {
            apiService.getSecurityEvents("Bearer $token", limit)
        }
    }
    
    suspend fun reportSecurityEvent(
        token: String,
        eventType: String,
        description: String,
        metadata: Map<String, Any> = emptyMap()
    ): Result<BasicResponse> {
        return safeApiCall {
            apiService.reportSecurityEvent(
                "Bearer $token",
                ReportSecurityEventRequest(eventType, description, metadata)
            )
        }
    }
    
    // Dashboard methods
    suspend fun getDashboardData(token: String): Result<DashboardResponse> {
        return safeApiCall {
            apiService.getDashboardData("Bearer $token")
        }
    }
    
    suspend fun updateDashboardConfig(
        token: String,
        configuration: Map<String, Any>
    ): Result<BasicResponse> {
        return safeApiCall {
            apiService.updateDashboardConfig(
                "Bearer $token",
                UpdateDashboardConfigRequest(configuration)
            )
        }
    }
}