package com.example.pasienapi.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class ApiErrorResponse(
    val success: Boolean? = null,
    val message: String? = null
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData?
)

data class LoginData(
    val token: String,
    val user: User
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String? = null,
    val is_active: Boolean? = null
)
