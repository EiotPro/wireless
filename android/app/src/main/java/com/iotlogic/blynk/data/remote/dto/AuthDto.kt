package com.iotlogic.blynk.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Login request DTO
 */
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String,
    
    @SerializedName("device_id")
    val deviceId: String,
    
    @SerializedName("remember_me")
    val rememberMe: Boolean = false
)

/**
 * Login response DTO
 */
data class LoginResponse(
    @SerializedName("access_token")
    val accessToken: String,
    
    @SerializedName("refresh_token")
    val refreshToken: String,
    
    @SerializedName("token_type")
    val tokenType: String = "Bearer",
    
    @SerializedName("expires_in")
    val expiresIn: Long, // seconds
    
    @SerializedName("user")
    val user: UserDto
)

/**
 * User DTO
 */
data class UserDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    
    @SerializedName("role")
    val role: String,
    
    @SerializedName("is_email_verified")
    val isEmailVerified: Boolean,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("updated_at")
    val updatedAt: String
)

/**
 * Register request DTO
 */
data class RegisterRequest(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String,
    
    @SerializedName("password_confirmation")
    val passwordConfirmation: String = password,
    
    @SerializedName("device_id")
    val deviceId: String
)

/**
 * Refresh token request DTO
 */
data class RefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String,
    
    @SerializedName("device_id")
    val deviceId: String
)

/**
 * Refresh token response DTO
 */
data class RefreshTokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
    
    @SerializedName("refresh_token")
    val refreshToken: String?,
    
    @SerializedName("token_type")
    val tokenType: String = "Bearer",
    
    @SerializedName("expires_in")
    val expiresIn: Long // seconds
)

/**
 * Password reset request DTO
 */
data class PasswordResetRequest(
    @SerializedName("email")
    val email: String
)

/**
 * Password reset response DTO
 */
data class PasswordResetResponse(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("reset_token")
    val resetToken: String?
)

/**
 * Change password request DTO
 */
data class ChangePasswordRequest(
    @SerializedName("current_password")
    val currentPassword: String,
    
    @SerializedName("new_password")
    val newPassword: String,
    
    @SerializedName("new_password_confirmation")
    val newPasswordConfirmation: String = newPassword
)

/**
 * Email verification request DTO
 */
data class EmailVerificationRequest(
    @SerializedName("verification_code")
    val verificationCode: String
)

/**
 * Logout response DTO
 */
data class LogoutResponse(
    @SerializedName("message")
    val message: String
)

/**
 * FCM token registration DTO
 */
data class FCMTokenRequest(
    @SerializedName("token")
    val token: String,
    
    @SerializedName("device_id")
    val deviceId: String,
    
    @SerializedName("platform")
    val platform: String = "android"
)

/**
 * Notification preferences DTO
 */
data class NotificationPreferencesRequest(
    @SerializedName("device_alerts")
    val deviceAlerts: Boolean,
    
    @SerializedName("system_updates")
    val systemUpdates: Boolean,
    
    @SerializedName("geofence_alerts")
    val geofenceAlerts: Boolean,
    
    @SerializedName("connection_alerts")
    val connectionAlerts: Boolean
)

/**
 * API error response DTO
 */
data class ApiErrorResponse(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("errors")
    val errors: Map<String, List<String>>? = null,
    
    @SerializedName("code")
    val code: String? = null
)

/**
 * Success response DTO
 */
data class ApiSuccessResponse(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: Any? = null
)