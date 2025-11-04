package com.bright.patientcareapp.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bright.patientcareapp.data.local.entity.PatientEntity
import com.bright.patientcareapp.data.model.Gender
import com.bright.patientcareapp.data.repository.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel for Patient Registration Screen (PDF Page 2)
 * Handles validation and saves patient locally + API
 */
@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    fun onPatientIdChange(value: String) {
        _uiState.value = _uiState.value.copy(
            patientId = value,
            patientIdError = null
        )
    }

    fun onRegistrationDateChange(value: Date) {
        _uiState.value = _uiState.value.copy(registrationDate = value)
    }

    fun onFirstNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            firstName = value,
            firstNameError = null
        )
    }

    fun onLastNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            lastName = value,
            lastNameError = null
        )
    }

    fun onDateOfBirthChange(value: Date) {
        _uiState.value = _uiState.value.copy(
            dateOfBirth = value,
            dateOfBirthError = null
        )
    }

    fun onGenderChange(value: Gender) {
        _uiState.value = _uiState.value.copy(gender = value)
    }

    /**
     * Validate and save patient
     * PDF Rules: All fields mandatory, Patient ID must be unique
     */
    fun savePatient(onSuccess: (String) -> Unit) {
        val state = _uiState.value

        // Validation
        var hasError = false

        if (state.patientId.isBlank()) {
            _uiState.value = state.copy(patientIdError = "Patient ID is required")
            hasError = true
        }

        if (state.firstName.isBlank()) {
            _uiState.value = state.copy(firstNameError = "First name is required")
            hasError = true
        }

        if (state.lastName.isBlank()) {
            _uiState.value = state.copy(lastNameError = "Last name is required")
            hasError = true
        }

        if (state.dateOfBirth == null) {
            _uiState.value = state.copy(dateOfBirthError = "Date of birth is required")
            hasError = true
        }

        if (hasError) return

        _uiState.value = state.copy(isLoading = true)

        viewModelScope.launch {
            val patient = PatientEntity(
                patientId = state.patientId.trim(),
                registrationDate = state.registrationDate,
                firstName = state.firstName.trim(),
                lastName = state.lastName.trim(),
                dateOfBirth = state.dateOfBirth!!,
                gender = state.gender
            )

            val result = patientRepository.registerPatient(patient)

            result.fold(
                onSuccess = {
                    _uiState.value = state.copy(isLoading = false)
                    onSuccess(patient.patientId)  // Pass patientId for next screens
                },
                onFailure = { error ->
                    _uiState.value = state.copy(
                        isLoading = false,
                        patientIdError = error.message
                    )
                }
            )
        }
    }
}

/**
 * UI State for Registration Screen
 */
data class RegistrationUiState(
    val patientId: String = "",
    val registrationDate: Date = Date(),  // Default to today
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: Date? = null,
    val gender: Gender = Gender.MALE,
    val patientIdError: String? = null,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val dateOfBirthError: String? = null,
    val isLoading: Boolean = false
)