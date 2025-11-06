package com.bright.patientcareapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.bright.patientcareapp.data.auth.AuthRepository
import com.bright.patientcareapp.data.remote.model.AuthResult
//import com.bright.patientcareapp.data.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = null
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = null
        )
    }

    fun login() {
        val state = _uiState.value

        // Validation (keep existing validation code)
        if (!isValidEmail(state.email)) {
            _uiState.value = state.copy(emailError = "Please enter a valid email")
            return
        }

        if (state.password.isBlank()) {
            _uiState.value = state.copy(passwordError = "Password is required")
            return
        }

        _uiState.value = state.copy(isLoading = true, loginError = null)

        viewModelScope.launch {
            when (val result = authRepository.login(state.email, state.password)) {
                is AuthResult.Success -> {
                    _uiState.value = state.copy(
                        isLoading = false,
                        loginSuccess = true // Add this
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = state.copy(
                        isLoading = false,
                        loginError = result.message
                    )
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun resetLoginSuccess() {
        val _uiState = MutableStateFlow(LoginUiState())
        _uiState.value = _uiState.value.copy(loginSuccess = false)
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val loginError: String? = null,
    val loginSuccess: Boolean = false
)

