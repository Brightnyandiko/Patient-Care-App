package com.bright.patientcareapp.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class SignupRequest(
    val email: String,
    val firstname: String,
    val lastname: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val message: String,
    val success: Boolean,
    val code: Int,
    val data: UserData
)

@JsonClass(generateAdapter = true)
data class UserData(
    val id: Int,
    val name: String,
    val email: String,
    @Json(name = "access_token")
    val accessToken: String,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "updated_at")
    val updatedAt: String
)

data class AuthToken(
    val token: String,
    val userId: Int,
    val userEmail: String,
    val userName: String
)

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val user: UserData) : AuthState()
    object Unauthenticated : AuthState()
}

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String, val code: Int? = null) : AuthResult<Nothing>()
}