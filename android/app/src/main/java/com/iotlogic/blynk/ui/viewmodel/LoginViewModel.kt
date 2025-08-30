package com.iotlogic.blynk.ui.viewmodel

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iotlogic.blynk.auth.AuthState
import com.iotlogic.blynk.auth.AuthenticationManager
import com.iotlogic.blynk.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authManager: AuthenticationManager
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    // Auth state from AuthenticationManager
    val authState: StateFlow<AuthState> = authManager.authState
    val currentUser: StateFlow<User?> = authManager.currentUser
    
    init {
        // Check biometric availability
        checkBiometricAvailability()
        
        // Check if auto-login is enabled and user was previously logged in
        checkAutoLogin()
    }
    
    /**
     * Login with email and password
     */
    fun login(email: String, password: String, rememberMe: Boolean = false) {
        if (!validateLoginInput(email, password)) {
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                loadingMessage = "Signing in..."
            )
            
            val result = authManager.login(email, password)
            
            if (result.isSuccess) {
                // Set auto-login preference
                authManager.setAutoLoginEnabled(rememberMe)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoginSuccessful = true,
                    error = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Login failed"
                )
            }
        }
    }
    
    /**
     * Authenticate with biometrics
     */
    fun authenticateWithBiometrics() {
        val activity = context as? FragmentActivity
        if (activity == null) {
            _uiState.value = _uiState.value.copy(
                error = "Biometric authentication not available"
            )
            return
        }
        
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            loadingMessage = "Authenticating with biometrics..."
        )
        
        authManager.authenticateWithBiometrics(
            activity = activity,
            onSuccess = {
                viewModelScope.launch {
                    // Auto-login with saved credentials
                    performAutoLogin()
                }
            },
            onError = { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error
                )
            }
        )
    }
    
    /**
     * Login as guest (demo mode)
     */
    fun loginAsGuest() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                loadingMessage = "Setting up guest access..."
            )
            
            // Simulate guest login
            kotlinx.coroutines.delay(1000)
            
            // For demo purposes, create a guest user
            val guestResult = authManager.login("guest@iotlogic.com", "guest123")
            
            if (guestResult.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoginSuccessful = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Guest access not available at the moment"
                )
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * Reset login success state
     */
    fun resetLoginSuccess() {
        _uiState.value = _uiState.value.copy(isLoginSuccessful = false)
    }
    
    /**
     * Validate login input
     */
    private fun validateLoginInput(email: String, password: String): Boolean {
        when {
            email.isBlank() -> {
                _uiState.value = _uiState.value.copy(error = "Email is required")
                return false
            }
            !isValidEmail(email) -> {
                _uiState.value = _uiState.value.copy(error = "Please enter a valid email address")
                return false
            }
            password.isBlank() -> {
                _uiState.value = _uiState.value.copy(error = "Password is required")
                return false
            }
            password.length < 6 -> {
                _uiState.value = _uiState.value.copy(error = "Password must be at least 6 characters")
                return false
            }
            else -> return true
        }
    }
    
    /**
     * Check if email is valid
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * Check biometric availability
     */
    private fun checkBiometricAvailability() {
        val isBiometricAvailable = authManager.isBiometricAvailable()
        val isBiometricEnabled = authManager.isBiometricEnabled()
        
        _uiState.value = _uiState.value.copy(
            isBiometricAvailable = isBiometricAvailable,
            isBiometricEnabled = isBiometricEnabled && isBiometricAvailable
        )
    }
    
    /**
     * Check for auto-login
     */
    private fun checkAutoLogin() {
        if (authManager.isAutoLoginEnabled() && authManager.isAuthenticated()) {
            viewModelScope.launch {
                performAutoLogin()
            }
        }
    }
    
    /**
     * Perform auto-login
     */
    private suspend fun performAutoLogin() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            loadingMessage = "Signing in automatically..."
        )
        
        // Check if token is still valid
        if (authManager.isAuthenticated()) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isLoginSuccessful = true
            )
        } else {
            // Try to refresh token
            val refreshResult = authManager.refreshToken()
            if (refreshResult.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoginSuccessful = true
                )
            } else {
                // Auto-login failed, show login form
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Session expired. Please sign in again."
                )
            }
        }
    }
    
    /**
     * Enable biometric authentication
     */
    fun enableBiometricAuth() {
        if (authManager.isBiometricAvailable()) {
            authManager.setBiometricEnabled(true)
            _uiState.value = _uiState.value.copy(isBiometricEnabled = true)
        }
    }
    
    /**
     * Disable biometric authentication
     */
    fun disableBiometricAuth() {
        authManager.setBiometricEnabled(false)
        _uiState.value = _uiState.value.copy(isBiometricEnabled = false)
    }
    
    /**
     * Check if user is already authenticated
     */
    fun isAlreadyAuthenticated(): Boolean {
        return authManager.isAuthenticated()
    }
    
    /**
     * Get authentication token for API calls
     */
    fun getAuthToken(): String? {
        return authManager.getAuthToken()
    }
    
    /**
     * Get bearer token for API calls
     */
    fun getBearerToken(): String? {
        return authManager.getBearerToken()
    }
}

/**
 * UI state for login screen
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val error: String? = null,
    val loadingMessage: String? = null,
    val isBiometricAvailable: Boolean = false,
    val isBiometricEnabled: Boolean = false
)