package com.bright.patientcareapp.data.auth

import com.bright.patientcareapp.data.remote.PatientApiService
import com.bright.patientcareapp.data.remote.model.AuthResult
import com.bright.patientcareapp.data.remote.model.AuthState
import com.bright.patientcareapp.data.remote.model.AuthToken
import com.bright.patientcareapp.data.remote.model.LoginRequest
import com.bright.patientcareapp.data.remote.model.SignupRequest
import com.bright.patientcareapp.data.remote.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val patientApiService: PatientApiService,
    private val tokenStore: SecureTokenStore
) {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    suspend fun initialize() {
        val token = tokenStore.getToken()
        _authState.value = if (token != null) {
            AuthState.Authenticated(
                UserData(
                    id = token.userId,
                    name = token.userName,
                    email = token.userEmail,
                    accessToken = "***", // Don't expose raw token
                    createdAt = "",
                    updatedAt = ""
                )
            )
        } else {
            AuthState.Unauthenticated
        }
    }

    suspend fun login(email: String, password: String): AuthResult<UserData> {
        return try {
            println("AuthRepository: Attempting login for $email") // Debug log
            val response = patientApiService.login(LoginRequest(email.trim(), password))

            println("AuthRepository: Response code ${response.code()}, success: ${response.body()?.success}") // Debug log

            if (response.isSuccessful && response.body()?.success == true) {
                val authResponse = response.body()!!
                val userData = authResponse.data

                // Save token securely
                val authToken = AuthToken(
                    token = userData.accessToken,
                    userId = userData.id,
                    userEmail = userData.email,
                    userName = userData.name
                )
                tokenStore.saveToken(authToken)

                // Update auth state
                _authState.value = AuthState.Authenticated(userData)

                println("AuthRepository: Login successful for ${userData.name}") // Debug log

                AuthResult.Success(userData)
            } else {
                val errorMsg = response.body()?.message ?: "Login failed"
                println("AuthRepository: Login failed - $errorMsg") // Debug log
                AuthResult.Error(errorMsg, response.code())
            }
        } catch (e: HttpException) {
            println("AuthRepository: HTTP Exception - ${e.message()}") // Debug log
            AuthResult.Error("Network error: ${e.message()}", e.code())
        } catch (e: Exception) {
            println("AuthRepository: Exception - ${e.message}") // Debug log
            AuthResult.Error("Login failed: ${e.message}")
        }
    }

    suspend fun signup(
        email: String,
        firstName: String,
        lastName: String,
        password: String
    ): AuthResult<String> {
        return try {
            val response = patientApiService.signup(
                SignupRequest(
                    email = email.trim(),
                    firstname = firstName.trim(),
                    lastname = lastName.trim(),
                    password = password
                )
            )

            if (response.isSuccessful && response.body()?.success == true) {
                val message = response.body()?.data?.toString() ?: "Account created successfully"
                AuthResult.Success(message)
            } else {
                val errorMsg = response.body()?.message ?: "Signup failed"
                AuthResult.Error(errorMsg, response.code())
            }
        } catch (e: HttpException) {
            AuthResult.Error("Network error: ${e.message()}", e.code())
        } catch (e: Exception) {
            AuthResult.Error("Signup failed: ${e.message}")
        }
    }

    suspend fun logout() {
        tokenStore.clearToken()
        _authState.value = AuthState.Unauthenticated
    }

    suspend fun getCurrentUser(): UserData? {
        return when (val state = _authState.value) {
            is AuthState.Authenticated -> state.user
            else -> null
        }
    }
}