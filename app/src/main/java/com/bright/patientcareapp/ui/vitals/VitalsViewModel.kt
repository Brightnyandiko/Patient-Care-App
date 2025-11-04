package com.bright.patientcareapp.ui.vitals

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bright.patientcareapp.data.local.entity.VitalsEntity
import com.bright.patientcareapp.data.repository.VitalsRepository
import com.bright.patientcareapp.util.BmiUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel for Vitals Screen (PDF Page 3)
 * Calculates BMI and routes to correct assessment
 */
@HiltViewModel
class VitalsViewModel @Inject constructor(
    private val vitalsRepository: VitalsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val patientId: String = checkNotNull(savedStateHandle["patientId"])

    private val _uiState = MutableStateFlow(VitalsUiState())
    val uiState: StateFlow<VitalsUiState> = _uiState.asStateFlow()

    fun onVisitDateChange(value: Date) {
        _uiState.value = _uiState.value.copy(visitDate = value)
    }

    fun onHeightChange(value: String) {
        _uiState.value = _uiState.value.copy(
            heightCm = value,
            heightError = null
        )
        calculateBmi()
    }

    fun onWeightChange(value: String) {
        _uiState.value = _uiState.value.copy(
            weightKg = value,
            weightError = null
        )
        calculateBmi()
    }

    /**
     * Auto-calculate BMI when height/weight change
     * Formula: BMI = weight(kg) / (height(m))^2
     */
    private fun calculateBmi() {
        val state = _uiState.value
        val height = state.heightCm.toDoubleOrNull()
        val weight = state.weightKg.toDoubleOrNull()

        if (height != null && weight != null && height > 0) {
            val bmi = vitalsRepository.calculateBmi(height, weight)
            _uiState.value = state.copy(bmi = String.format("%.2f", bmi))
        } else {
            _uiState.value = state.copy(bmi = "")
        }
    }

    /**
     * Save vitals and route based on BMI
     * PDF Rule: BMI <= 25 → General, BMI > 25 → Overweight
     */
    fun saveVitals(onNavigate: (Boolean) -> Unit) {
        val state = _uiState.value

        // Validation
        var hasError = false

        val height = state.heightCm.toDoubleOrNull()
        if (height == null || height <= 0) {
            _uiState.value = state.copy(heightError = "Enter valid height in cm")
            hasError = true
        }

        val weight = state.weightKg.toDoubleOrNull()
        if (weight == null || weight <= 0) {
            _uiState.value = state.copy(weightError = "Enter valid weight in kg")
            hasError = true
        }

        if (hasError) return

        _uiState.value = state.copy(isLoading = true)

        viewModelScope.launch {
            val bmi = vitalsRepository.calculateBmi(height!!, weight!!)

            val vitals = VitalsEntity(
                patientId = patientId,
                visitDate = state.visitDate,
                heightCm = height,
                weightKg = weight,
                bmi = bmi
            )

            val result = vitalsRepository.saveVitals(vitals)

            result.fold(
                onSuccess = { savedBmi ->
                    _uiState.value = state.copy(isLoading = false)
                    // Route based on BMI (PDF Page 3 rule)
                    onNavigate(BmiUtils.shouldShowGeneralAssessment(savedBmi))
                },
                onFailure = { error ->
                    _uiState.value = state.copy(
                        isLoading = false,
                        heightError = error.message
                    )
                }
            )
        }
    }
}

data class VitalsUiState(
    val visitDate: Date = Date(),
    val heightCm: String = "",
    val weightKg: String = "",
    val bmi: String = "",
    val heightError: String? = null,
    val weightError: String? = null,
    val isLoading: Boolean = false
)