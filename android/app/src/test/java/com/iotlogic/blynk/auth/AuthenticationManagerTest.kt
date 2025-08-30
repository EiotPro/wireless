package com.iotlogic.blynk.auth

import android.content.Context
import androidx.biometric.BiometricManager
import com.iotlogic.blynk.data.local.preferences.AuthPreferences
import com.iotlogic.blynk.data.remote.api.IoTLogicApiService
import com.iotlogic.blynk.data.remote.dto.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthenticationManagerTest {

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var authPreferences: AuthPreferences

    @MockK
    private lateinit var apiService: IoTLogicApiService

    @MockK
    private lateinit var biometricManager: BiometricManager

    private lateinit var authenticationManager: AuthenticationManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        
        // Mock context
        every { context.getSharedPreferences(any(), any()) } returns mockk(relaxed = true)
        
        authenticationManager = AuthenticationManager(
            context = context,
            authPreferences = authPreferences,
            apiService = apiService
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `login success should save auth data and update state`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val userDto = UserDto(
            id = "user123",
            email = email,
            name = "Test User",
            avatarUrl = null,
            role = "user",
            isEmailVerified = true
        )
        val loginResponse = LoginResponse(
            accessToken = "access_token",
            refreshToken = "refresh_token",
            expiresIn = 3600,
            user = userDto
        )

        every { apiService.login(any()) } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns loginResponse
        }
        every { authPreferences.saveAuthData(any(), any(), any(), any(), any()) } just Runs
        every { authPreferences.isLoggedIn() } returns false

        // When
        val result = authenticationManager.login(email, password)

        // Then
        assertTrue(result.isSuccess)
        val user = result.getOrNull()!!
        assertEquals("user123", user.id)
        assertEquals(email, user.email)
        assertEquals("Test User", user.name)

        verify {
            authPreferences.saveAuthData(
                token = "access_token",
                refreshToken = "refresh_token",
                expiryTime = any(),
                userId = "user123",
                username = email
            )
        }
    }

    @Test
    fun `login failure should return error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "wrong_password"

        every { apiService.login(any()) } returns mockk {
            every { isSuccessful } returns false
            every { message() } returns "Invalid credentials"
        }
        every { authPreferences.isLoggedIn() } returns false

        // When
        val result = authenticationManager.login(email, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Invalid credentials", result.exceptionOrNull()?.message)
    }

    @Test
    fun `register success should save auth data`() = runTest {
        // Given
        val name = "Test User"
        val email = "test@example.com"
        val password = "password123"
        val confirmPassword = "password123"
        
        val userDto = UserDto(
            id = "user123",
            email = email,
            name = name,
            avatarUrl = null,
            role = "user",
            isEmailVerified = false
        )
        val loginResponse = LoginResponse(
            accessToken = "access_token",
            refreshToken = "refresh_token",
            expiresIn = 3600,
            user = userDto
        )

        every { apiService.register(any()) } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns loginResponse
        }
        every { authPreferences.saveAuthData(any(), any(), any(), any(), any()) } just Runs
        every { authPreferences.isLoggedIn() } returns false

        // When
        val result = authenticationManager.register(name, email, password, confirmPassword)

        // Then
        assertTrue(result.isSuccess)
        val user = result.getOrNull()!!
        assertEquals("user123", user.id)
        assertEquals(email, user.email)
        assertEquals(name, user.name)
        assertFalse(user.isEmailVerified)
    }

    @Test
    fun `register with mismatched passwords should fail`() = runTest {
        // Given
        val name = "Test User"
        val email = "test@example.com"
        val password = "password123"
        val confirmPassword = "different_password"

        every { authPreferences.isLoggedIn() } returns false

        // When
        val result = authenticationManager.register(name, email, password, confirmPassword)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Passwords do not match", result.exceptionOrNull()?.message)
    }

    @Test
    fun `logout should clear auth data and call API`() = runTest {
        // Given
        every { authPreferences.getAuthToken() } returns "access_token"
        every { apiService.logout(any()) } returns mockk(relaxed = true)
        every { authPreferences.clearAuthData() } just Runs
        every { authPreferences.isLoggedIn() } returns false

        // When
        val result = authenticationManager.logout()

        // Then
        assertTrue(result.isSuccess)
        verify { authPreferences.clearAuthData() }
        verify { apiService.logout("Bearer access_token") }
    }

    @Test
    fun `logout should succeed even if API call fails`() = runTest {
        // Given
        every { authPreferences.getAuthToken() } returns "access_token"
        every { apiService.logout(any()) } throws Exception("Network error")
        every { authPreferences.clearAuthData() } just Runs
        every { authPreferences.isLoggedIn() } returns false

        // When
        val result = authenticationManager.logout()

        // Then
        assertTrue(result.isSuccess)
        verify { authPreferences.clearAuthData() }
    }

    @Test
    fun `refreshToken success should update auth data`() = runTest {
        // Given
        val refreshToken = "refresh_token"
        val tokenResponse = TokenResponse(
            accessToken = "new_access_token",
            refreshToken = "new_refresh_token",
            expiresIn = 3600
        )

        every { authPreferences.getRefreshToken() } returns refreshToken
        every { apiService.refreshToken(any()) } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns tokenResponse
        }
        every { authPreferences.saveAuthToken(any()) } just Runs
        every { authPreferences.saveTokenExpiry(any()) } just Runs
        every { authPreferences.saveRefreshToken(any()) } just Runs
        every { authPreferences.isLoggedIn() } returns false

        // When
        val result = authenticationManager.refreshToken()

        // Then
        assertTrue(result.isSuccess)
        assertEquals("new_access_token", result.getOrNull())
        
        verify { authPreferences.saveAuthToken("new_access_token") }
        verify { authPreferences.saveRefreshToken("new_refresh_token") }
    }

    @Test
    fun `refreshToken failure should logout user`() = runTest {
        // Given
        val refreshToken = "invalid_refresh_token"

        every { authPreferences.getRefreshToken() } returns refreshToken
        every { apiService.refreshToken(any()) } returns mockk {
            every { isSuccessful } returns false
            every { message() } returns "Invalid refresh token"
        }
        every { authPreferences.getAuthToken() } returns null
        every { authPreferences.clearAuthData() } just Runs
        every { authPreferences.isLoggedIn() } returns false

        // When
        val result = authenticationManager.refreshToken()

        // Then
        assertTrue(result.isFailure)
        verify { authPreferences.clearAuthData() }
    }

    @Test
    fun `refreshToken without refresh token should fail`() = runTest {
        // Given
        every { authPreferences.getRefreshToken() } returns null
        every { authPreferences.isLoggedIn() } returns false

        // When
        val result = authenticationManager.refreshToken()

        // Then
        assertTrue(result.isFailure)
        assertEquals("No refresh token available", result.exceptionOrNull()?.message)
    }

    @Test
    fun `changePassword success should return success`() = runTest {
        // Given
        val currentPassword = "old_password"
        val newPassword = "new_password"
        val confirmPassword = "new_password"

        every { authPreferences.getBearerToken() } returns "Bearer access_token"
        every { apiService.changePassword(any(), any(), any()) } returns mockk {
            every { isSuccessful } returns true
        }
        every { authPreferences.isLoggedIn() } returns false

        // When
        val result = authenticationManager.changePassword(currentPassword, newPassword, confirmPassword)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `changePassword with mismatched passwords should fail`() = runTest {
        // Given
        val currentPassword = "old_password"
        val newPassword = "new_password"
        val confirmPassword = "different_password"

        every { authPreferences.isLoggedIn() } returns false

        // When
        val result = authenticationManager.changePassword(currentPassword, newPassword, confirmPassword)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Passwords do not match", result.exceptionOrNull()?.message)
    }

    @Test
    fun `requestPasswordReset success should return success`() = runTest {
        // Given
        val email = "test@example.com"

        every { apiService.requestPasswordReset(email) } returns mockk {
            every { isSuccessful } returns true
        }
        every { authPreferences.isLoggedIn() } returns false

        // When
        val result = authenticationManager.requestPasswordReset(email)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `verifyEmail success should update user verification status`() = runTest {
        // Given
        val verificationCode = "123456"

        every { authPreferences.getBearerToken() } returns "Bearer access_token"
        every { apiService.verifyEmail(any(), any()) } returns mockk {
            every { isSuccessful } returns true
        }
        every { authPreferences.isLoggedIn() } returns false

        // When
        val result = authenticationManager.verifyEmail(verificationCode)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `isAuthenticated should return auth preferences validity`() {
        // Given
        every { authPreferences.isAuthValid() } returns true

        // When
        val isAuthenticated = authenticationManager.isAuthenticated()

        // Then
        assertTrue(isAuthenticated)
        verify { authPreferences.isAuthValid() }
    }

    @Test
    fun `getAuthToken should return token from preferences`() {
        // Given
        val expectedToken = "access_token"
        every { authPreferences.getAuthToken() } returns expectedToken

        // When
        val token = authenticationManager.getAuthToken()

        // Then
        assertEquals(expectedToken, token)
    }

    @Test
    fun `setBiometricEnabled should update preferences`() {
        // Given
        every { authPreferences.setBiometricEnabled(any()) } just Runs

        // When
        authenticationManager.setBiometricEnabled(true)

        // Then
        verify { authPreferences.setBiometricEnabled(true) }
    }

    @Test
    fun `isBiometricEnabled should return preference value`() {
        // Given
        every { authPreferences.isBiometricEnabled() } returns true

        // When
        val isEnabled = authenticationManager.isBiometricEnabled()

        // Then
        assertTrue(isEnabled)
    }

    @Test
    fun `generateSessionToken should return non-empty token`() {
        // When
        val token = authenticationManager.generateSessionToken()

        // Then
        assertTrue(token.isNotEmpty())
    }

    @Test
    fun `validateSessionToken should validate non-empty tokens`() {
        // Given
        val validToken = "valid_token_123"
        val emptyToken = ""

        // When & Then
        assertTrue(authenticationManager.validateSessionToken(validToken))
        assertFalse(authenticationManager.validateSessionToken(emptyToken))
    }
}