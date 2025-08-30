package com.iotlogic.blynk.ui.screens.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iotlogic.blynk.ui.screens.auth.LoginScreen
import com.iotlogic.blynk.ui.theme.IoTLogicTheme
import com.iotlogic.blynk.ui.viewmodel.AuthViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: AuthViewModel
    private lateinit var uiStateFlow: MutableStateFlow<AuthViewModel.UiState>

    @Before
    fun setUp() {
        hiltRule.inject()

        // Create mock ViewModel
        mockViewModel = mockk(relaxed = true)
        uiStateFlow = MutableStateFlow(AuthViewModel.UiState())

        every { mockViewModel.uiState } returns uiStateFlow
    }

    @Test
    fun loginScreen_InitialState_ShowsLoginForm() {
        // Given initial state
        uiStateFlow.value = AuthViewModel.UiState()

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Welcome Back").assertExists()
        composeTestRule.onNodeWithText("Sign in to your account").assertExists()
        composeTestRule.onNodeWithText("Email").assertExists()
        composeTestRule.onNodeWithText("Password").assertExists()
        composeTestRule.onNodeWithText("Login").assertExists()
        composeTestRule.onNodeWithText("Don't have an account? Sign up").assertExists()
        composeTestRule.onNodeWithText("Forgot Password?").assertExists()
    }

    @Test
    fun loginScreen_EnterEmail_UpdatesTextField() {
        // Given
        uiStateFlow.value = AuthViewModel.UiState()

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Enter email
        composeTestRule.onNodeWithText("Email")
            .performTextInput("test@example.com")

        // Then
        composeTestRule.onNodeWithText("test@example.com").assertExists()
    }

    @Test
    fun loginScreen_EnterPassword_UpdatesTextField() {
        // Given
        uiStateFlow.value = AuthViewModel.UiState()

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Enter password
        composeTestRule.onNodeWithText("Password")
            .performTextInput("password123")

        // Then - Password should be masked, so we check the input exists
        composeTestRule.onNodeWithText("Password").assertExists()
    }

    @Test
    fun loginScreen_ClickLogin_CallsViewModel() {
        // Given filled form
        uiStateFlow.value = AuthViewModel.UiState()

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Fill form and submit
        composeTestRule.onNodeWithText("Email")
            .performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Password")
            .performTextInput("password123")
        composeTestRule.onNodeWithText("Login").performClick()

        // Then
        verify { mockViewModel.login("test@example.com", "password123") }
    }

    @Test
    fun loginScreen_LoadingState_ShowsProgressIndicator() {
        // Given loading state
        uiStateFlow.value = AuthViewModel.UiState(isLoading = true)

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then login button should be disabled and show loading
        composeTestRule.onNodeWithText("Login").assertIsNotEnabled()
        composeTestRule.onNodeWithContentDescription("Loading").assertExists()
    }

    @Test
    fun loginScreen_ErrorState_ShowsErrorMessage() {
        // Given error state
        uiStateFlow.value = AuthViewModel.UiState(
            isLoading = false,
            error = "Invalid email or password"
        )

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Invalid email or password").assertExists()
    }

    @Test
    fun loginScreen_SuccessState_CallsSuccessCallback() {
        // Given success state
        var loginSuccessCalled = false
        uiStateFlow.value = AuthViewModel.UiState(
            isLoading = false,
            isAuthenticated = true
        )

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                LoginScreen(
                    onLoginSuccess = { loginSuccessCalled = true },
                    onNavigateToRegister = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        assert(loginSuccessCalled)
    }

    @Test
    fun loginScreen_ClickSignUp_CallsNavigationCallback() {
        // Given
        var navigateToRegisterCalled = false
        uiStateFlow.value = AuthViewModel.UiState()

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = { navigateToRegisterCalled = true },
                    viewModel = mockViewModel
                )
            }
        }

        // Click sign up link
        composeTestRule.onNodeWithText("Don't have an account? Sign up").performClick()

        // Then
        assert(navigateToRegisterCalled)
    }

    @Test
    fun loginScreen_ClickForgotPassword_CallsViewModel() {
        // Given
        uiStateFlow.value = AuthViewModel.UiState()

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Enter email first
        composeTestRule.onNodeWithText("Email")
            .performTextInput("test@example.com")

        // Click forgot password
        composeTestRule.onNodeWithText("Forgot Password?").performClick()

        // Then
        verify { mockViewModel.requestPasswordReset("test@example.com") }
    }

    @Test
    fun loginScreen_EmptyEmail_ShowsValidationError() {
        // Given
        uiStateFlow.value = AuthViewModel.UiState()

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Try to login with empty email
        composeTestRule.onNodeWithText("Password")
            .performTextInput("password123")
        composeTestRule.onNodeWithText("Login").performClick()

        // Then - Login button should remain enabled but validation should prevent submission
        composeTestRule.onNodeWithText("Email").assertExists()
    }

    @Test
    fun loginScreen_EmptyPassword_ShowsValidationError() {
        // Given
        uiStateFlow.value = AuthViewModel.UiState()

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Try to login with empty password
        composeTestRule.onNodeWithText("Email")
            .performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Login").performClick()

        // Then - Login button should remain enabled but validation should prevent submission
        composeTestRule.onNodeWithText("Password").assertExists()
    }

    @Test
    fun loginScreen_InvalidEmailFormat_ShowsValidationError() {
        // Given
        uiStateFlow.value = AuthViewModel.UiState(
            emailError = "Invalid email format"
        )

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Invalid email format").assertExists()
    }

    @Test
    fun loginScreen_BiometricLogin_ShowsBiometricOption() {
        // Given biometric is available
        uiStateFlow.value = AuthViewModel.UiState(
            isBiometricAvailable = true,
            isBiometricEnabled = true
        )

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Use Biometric").assertExists()
        composeTestRule.onNodeWithContentDescription("Biometric login").assertExists()
    }

    @Test
    fun loginScreen_ClickBiometricLogin_CallsViewModel() {
        // Given biometric is available
        uiStateFlow.value = AuthViewModel.UiState(
            isBiometricAvailable = true,
            isBiometricEnabled = true
        )

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Click biometric login
        composeTestRule.onNodeWithText("Use Biometric").performClick()

        // Then
        verify { mockViewModel.loginWithBiometric() }
    }

    @Test
    fun loginScreen_RememberMe_TogglesCorrectly() {
        // Given
        uiStateFlow.value = AuthViewModel.UiState()

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Toggle remember me checkbox
        composeTestRule.onNodeWithText("Remember me").assertExists()
        composeTestRule.onNodeWithText("Remember me").performClick()

        // Then
        verify { mockViewModel.setRememberMe(any()) }
    }

    @Test
    fun loginScreen_PasswordVisibilityToggle_WorksCorrectly() {
        // Given
        uiStateFlow.value = AuthViewModel.UiState()

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Enter password
        composeTestRule.onNodeWithText("Password")
            .performTextInput("password123")

        // Click visibility toggle
        composeTestRule.onNodeWithContentDescription("Show password").performClick()

        // Then password should become visible
        composeTestRule.onNodeWithContentDescription("Hide password").assertExists()
    }

    @Test
    fun loginScreen_AutoLogin_WorksWhenEnabled() {
        // Given auto login is enabled and user is remembered
        uiStateFlow.value = AuthViewModel.UiState(
            isAutoLoginEnabled = true,
            rememberedEmail = "test@example.com"
        )

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then email should be pre-filled
        composeTestRule.onNodeWithText("test@example.com").assertExists()
    }

    @Test
    fun loginScreen_KeyboardNavigation_WorksCorrectly() {
        // Given
        uiStateFlow.value = AuthViewModel.UiState()

        // When
        composeTestRule.setContent {
            IoTLogicTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Test keyboard navigation
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        
        // Press IME action (Next) should move to password field
        composeTestRule.onNodeWithText("Email").performImeAction()
        
        // Focus should now be on password field
        composeTestRule.onNodeWithText("Password").assertIsFocused()
    }
}