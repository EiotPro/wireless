package com.iotlogic.blynk.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val avatarUrl: String? = null,
    val role: String = "user",
    val isEmailVerified: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null
)