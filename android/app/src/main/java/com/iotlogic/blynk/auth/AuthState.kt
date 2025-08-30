package com.iotlogic.blynk.auth

enum class AuthState {
    UNAUTHENTICATED,
    AUTHENTICATING,
    AUTHENTICATED,
    TOKEN_EXPIRED,
    ERROR
}