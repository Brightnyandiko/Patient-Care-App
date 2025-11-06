package com.bright.patientcareapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bright.patientcareapp.data.auth.AuthRepository
//import com.bright.patientcareapp.data.auth.AuthResult
import com.bright.patientcareapp.data.remote.model.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = null
        )
    }

    fun onFirstNameChange(firstName: String) {
        _uiState.value = _uiState.value.copy(
            firstName = firstName,
            firstNameError = null
        )
    }

    fun onLastNameChange(lastName: String) {
        _uiState.value = _uiState.value.copy(
            lastName = lastName,
            lastNameError = null
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = null
        )
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = null
        )
    }

    fun signup() {
        val state = _uiState.value

        // Clear previous errors
        _uiState.value = state.copy(
            emailError = null,
            firstNameError = null,
            lastNameError = null,
            passwordError = null,
            confirmPasswordError = null,
            signupError = null
        )

        // Validation
        var hasError = false
        val currentState = _uiState.value

        if (!isValidEmail(state.email)) {
            _uiState.value = currentState.copy(emailError = "Please enter a valid email")
            hasError = true
        }

        if (state.firstName.isBlank()) {
            _uiState.value = _uiState.value.copy(firstNameError = "First name is required")
            hasError = true
        }

        if (state.lastName.isBlank()) {
            _uiState.value = _uiState.value.copy(lastNameError = "Last name is required")
            hasError = true
        }

        if (state.password.length < 6) {
            _uiState.value = _uiState.value.copy(passwordError = "Password must be at least 6 characters")
            hasError = true
        }

        if (state.password != state.confirmPassword) {
            _uiState.value = _uiState.value.copy(confirmPasswordError = "Passwords do not match")
            hasError = true
        }

        if (hasError) return

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            when (val result = authRepository.signup(
                email = state.email,
                firstName = state.firstName,
                lastName = state.lastName,
                password = state.password
            )) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        signupSuccess = true,
                        successMessage = result.data
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        signupError = result.message
                    )
                }
            }
        }
    }

    fun resetSignupSuccess() {
        _uiState.value = _uiState.value.copy(signupSuccess = false)
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

data class SignupUiState(
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val signupError: String? = null,
    val signupSuccess: Boolean = false,
    val successMessage: String = ""
)