package com.iotlogic.blynk.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        "auth_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_LAST_LOGIN = "last_login"
        private const val KEY_AUTO_LOGIN = "auto_login"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    }
    
    /**
     * Save authentication token
     */
    fun saveAuthToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_AUTH_TOKEN, token)
        editor.apply()
    }
    
    /**
     * Get authentication token
     */
    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }
    
    /**
     * Save refresh token
     */
    fun saveRefreshToken(refreshToken: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_REFRESH_TOKEN, refreshToken)
        editor.apply()
    }
    
    /**
     * Get refresh token
     */
    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }
    
    /**
     * Save token expiry time
     */
    fun saveTokenExpiry(expiryTime: Long) {
        val editor = sharedPreferences.edit()
        editor.putLong(KEY_TOKEN_EXPIRY, expiryTime)
        editor.apply()
    }
    
    /**
     * Get token expiry time
     */
    fun getTokenExpiry(): Long {
        return sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0)
    }
    
    /**
     * Check if token is expired
     */
    fun isTokenExpired(): Boolean {
        val expiryTime = getTokenExpiry()
        return expiryTime > 0 && System.currentTimeMillis() >= expiryTime
    }
    
    /**
     * Save user ID
     */
    fun saveUserId(userId: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER_ID, userId)
        editor.apply()
    }
    
    /**
     * Get user ID
     */
    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }
    
    /**
     * Save username
     */
    fun saveUsername(username: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USERNAME, username)
        editor.apply()
    }
    
    /**
     * Get username
     */
    fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }
    
    /**
     * Set login status
     */
    fun setLoggedIn(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.putLong(KEY_LAST_LOGIN, if (isLoggedIn) System.currentTimeMillis() else 0)
        editor.apply()
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false) && !isTokenExpired()
    }
    
    /**
     * Get last login time
     */
    fun getLastLoginTime(): Long {
        return sharedPreferences.getLong(KEY_LAST_LOGIN, 0)
    }
    
    /**
     * Enable/disable auto login
     */
    fun setAutoLogin(enabled: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_AUTO_LOGIN, enabled)
        editor.apply()
    }
    
    /**
     * Check if auto login is enabled
     */
    fun isAutoLoginEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_AUTO_LOGIN, false)
    }
    
    /**
     * Enable/disable biometric authentication
     */
    fun setBiometricEnabled(enabled: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_BIOMETRIC_ENABLED, enabled)
        editor.apply()
    }
    
    /**
     * Check if biometric authentication is enabled
     */
    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }
    
    /**
     * Save complete authentication data
     */
    fun saveAuthData(
        token: String,
        refreshToken: String,
        expiryTime: Long,
        userId: String,
        username: String
    ) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_AUTH_TOKEN, token)
        editor.putString(KEY_REFRESH_TOKEN, refreshToken)
        editor.putLong(KEY_TOKEN_EXPIRY, expiryTime)
        editor.putString(KEY_USER_ID, userId)
        editor.putString(KEY_USERNAME, username)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putLong(KEY_LAST_LOGIN, System.currentTimeMillis())
        editor.apply()
    }
    
    /**
     * Clear all authentication data
     */
    fun clearAuthData() {
        val editor = sharedPreferences.edit()
        editor.remove(KEY_AUTH_TOKEN)
        editor.remove(KEY_REFRESH_TOKEN)
        editor.remove(KEY_TOKEN_EXPIRY)
        editor.remove(KEY_USER_ID)
        editor.remove(KEY_USERNAME)
        editor.putBoolean(KEY_IS_LOGGED_IN, false)
        editor.putLong(KEY_LAST_LOGIN, 0)
        editor.apply()
    }
    
    /**
     * Get auth token with Bearer prefix
     */
    fun getBearerToken(): String? {
        val token = getAuthToken()
        return if (token != null) "Bearer $token" else null
    }
    
    /**
     * Check if authentication is valid (not expired and has token)
     */
    fun isAuthValid(): Boolean {
        return getAuthToken() != null && !isTokenExpired()
    }
}