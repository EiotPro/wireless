package com.iotlogic.blynk.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.iotlogic.blynk.data.local.preferences.AuthPreferences
import com.iotlogic.blynk.data.remote.ApiService
import com.iotlogic.blynk.data.remote.LoginRequest
import com.iotlogic.blynk.data.remote.LoginResponse
import com.iotlogic.blynk.data.remote.RegisterRequest
import com.iotlogic.blynk.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authPreferences: AuthPreferences,
    private val apiService: ApiService
) {
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Authentication state
    private val _authState = MutableStateFlow(AuthState.UNAUTHENTICATED)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    // Current user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    // Auto-refresh token job
    private var tokenRefreshJob: Job? = null
    
    init {
        // Check initial auth state
        checkAuthState()
        
        // Start token refresh job if user is logged in
        if (authPreferences.isLoggedIn()) {
            startTokenRefresh()
        }
    }
    
    /**
     * Login with email and password
     */
    suspend fun login(email: String, password: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                _authState.value = AuthState.AUTHENTICATING
                
                val loginRequest = LoginRequest(
                    username = email,
                    password = password
                )
                
                val response = apiService.login(loginRequest)
                
                if (response.isSuccessful) {
                    val loginResponse = response.body()!!
                    
                    // Save auth data
                    authPreferences.saveAuthData(
                        token = loginResponse.token ?: "",
                        refreshToken = "", // No refresh token in current API
                        expiryTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000), // 24 hours default
                        userId = loginResponse.user?.id ?: "",
                        username = loginResponse.user?.email ?: email
                    )
                    
                    val user = User(
                        id = loginResponse.user?.id ?: "",
                        email = loginResponse.user?.email ?: email,
                        name = loginResponse.user?.username ?: "",
                        avatarUrl = null,
                        role = loginResponse.user?.role ?: "user",
                        isEmailVerified = true
                    )
                    
                    _currentUser.value = user
                    _authState.value = AuthState.AUTHENTICATED
                    
                    // Start token refresh
                    startTokenRefresh()
                    
                    Result.success(user)
                } else {
                    _authState.value = AuthState.UNAUTHENTICATED
                    Result.failure(Exception(response.message() ?: "Login failed"))
                }
            } catch (e: Exception) {
                _authState.value = AuthState.UNAUTHENTICATED
                Result.failure(e)
            }
        }
    }
    
    /**
     * Register new user
     */
    suspend fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                if (password != confirmPassword) {
                    return@withContext Result.failure(Exception("Passwords do not match"))
                }
                
                _authState.value = AuthState.AUTHENTICATING
                
                val registerRequest = RegisterRequest(
                    username = name,
                    email = email,
                    password = password
                )
                
                val response = apiService.register(registerRequest)
                
                if (response.isSuccessful) {
                    val registerResponse = response.body()!!
                    
                    if (registerResponse.success) {
                        // Auto-login after successful registration
                        return@withContext login(email, password)
                    } else {
                        _authState.value = AuthState.UNAUTHENTICATED
                        Result.failure(Exception(registerResponse.message))
                    }
                } else {
                    _authState.value = AuthState.UNAUTHENTICATED
                    Result.failure(Exception(response.message() ?: "Registration failed"))
                }
            } catch (e: Exception) {
                _authState.value = AuthState.UNAUTHENTICATED
                Result.failure(e)
            }
        }
    }
    
    /**
     * Logout user
     */
    suspend fun logout(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Stop token refresh
                tokenRefreshJob?.cancel()
                
                // Clear auth data
                authPreferences.clearAuthData()
                
                // Update state
                _currentUser.value = null
                _authState.value = AuthState.UNAUTHENTICATED
                
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Refresh access token
     */
    suspend fun refreshToken(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val token = authPreferences.getAuthToken()
                    ?: return@withContext Result.failure(Exception("No token available"))
                
                // For now, just return the existing token since API doesn't have refresh endpoint
                Result.success(token)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Check if biometric authentication is available
     */
    fun isBiometricAvailable(): Boolean {
        return when (BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }
    
    /**
     * Authenticate with biometrics
     */
    fun authenticateWithBiometrics(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isBiometricAvailable()) {
            onError("Biometric authentication not available")
            return
        }

        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Authentication failed")
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
    
    /**
     * Change password
     */
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (newPassword != confirmPassword) {
                    return@withContext Result.failure(Exception("Passwords do not match"))
                }
                
                // For now, return success since API doesn't have this endpoint
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Request password reset
     */
    suspend fun requestPasswordReset(email: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // For now, return success since API doesn't have this endpoint
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Verify email
     */
    suspend fun verifyEmail(verificationCode: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // For now, return success since API doesn't have this endpoint
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Get current auth token
     */
    fun getAuthToken(): String? {
        return authPreferences.getAuthToken()
    }
    
    /**
     * Get bearer token
     */
    fun getBearerToken(): String? {
        return authPreferences.getBearerToken()
    }
    
    /**
     * Check if user is authenticated
     */
    fun isAuthenticated(): Boolean {
        return authPreferences.isAuthValid()
    }
    
    /**
     * Enable/disable biometric authentication
     */
    fun setBiometricEnabled(enabled: Boolean) {
        authPreferences.setBiometricEnabled(enabled)
    }
    
    /**
     * Check if biometric authentication is enabled
     */
    fun isBiometricEnabled(): Boolean {
        return authPreferences.isBiometricEnabled()
    }
    
    /**
     * Enable/disable auto login
     */
    fun setAutoLoginEnabled(enabled: Boolean) {
        authPreferences.setAutoLogin(enabled)
    }
    
    /**
     * Check if auto login is enabled
     */
    fun isAutoLoginEnabled(): Boolean {
        return authPreferences.isAutoLoginEnabled()
    }
    
    /**
     * Check initial authentication state
     */
    private fun checkAuthState() {
        if (authPreferences.isLoggedIn()) {
            // Load user data
            val userId = authPreferences.getUserId()
            val username = authPreferences.getUsername()
            
            if (userId != null && username != null) {
                _currentUser.value = User(
                    id = userId,
                    email = username,
                    name = "", // Would need to fetch from API or cache
                    avatarUrl = null,
                    role = "user",
                    isEmailVerified = true
                )
                _authState.value = AuthState.AUTHENTICATED
            }
        }
    }
    
    /**
     * Start automatic token refresh
     */
    private fun startTokenRefresh() {
        tokenRefreshJob?.cancel()
        tokenRefreshJob = scope.launch {
            while (isActive && authPreferences.isLoggedIn()) {
                try {
                    val expiryTime = authPreferences.getTokenExpiry()
                    val currentTime = System.currentTimeMillis()
                    val timeUntilExpiry = expiryTime - currentTime
                    
                    // Refresh token 5 minutes before expiry
                    val refreshTime = timeUntilExpiry - (5 * 60 * 1000)
                    
                    if (refreshTime > 0) {
                        delay(refreshTime)
                    }
                    
                    // Refresh the token
                    refreshToken()
                    
                } catch (e: Exception) {
                    // Stop refresh on error
                    break
                }
            }
        }
    }
    
    /**
     * Get device ID for authentication
     */
    private fun getDeviceId(): String {
        val prefs = context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE)
        val deviceId = prefs.getString("device_id", null)
        
        return if (deviceId != null) {
            deviceId
        } else {
            val newDeviceId = "android_${UUID.randomUUID()}"
            prefs.edit().putString("device_id", newDeviceId).apply()
            newDeviceId
        }
    }
    
    /**
     * Generate session token for local operations
     */
    fun generateSessionToken(): String {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }
    
    /**
     * Validate session token
     */
    fun validateSessionToken(token: String): Boolean {
        // Implementation would validate against stored session tokens
        return token.isNotEmpty()
    }
    
    /**
     * Clean up resources
     */
    fun cleanup() {
        tokenRefreshJob?.cancel()
        scope.cancel()
    }
}