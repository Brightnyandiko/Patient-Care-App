package com.bright.patientcareapp.ui.assessment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bright.patientcareapp.data.local.entity.OverweightAssessmentEntity
import com.bright.patientcareapp.data.model.HealthStatus
//import com.bright.patientcareapp.data.model.UsageStatus
import com.bright.patientcareapp.data.repository.AssessmentRepository
import com.bright.patientcareapp.data.repository.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel for Overweight Assessment Screen (PDF Page 5)
 * Handles form validation and saves assessment data
 * Only shown for patients with BMI > 25
 */
@HiltViewModel
class OverweightAssessmentViewModel @Inject constructor(
    private val assessmentRepository: AssessmentRepository,
    private val patientRepository: PatientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OverweightAssessmentUiState())
    val uiState: StateFlow<OverweightAssessmentUiState> = _uiState.asStateFlow()

    private var currentPatientId: String = ""

    /**
     * Initialize with patient data
     */
    fun initializePatient(patientId: String) {
        currentPatientId = patientId

        viewModelScope.launch {
            // Get patient name for display
            val patient = patientRepository.getPatient(patientId)
            patient?.let {
                _uiState.value = _uiState.value.copy(
                    patientName = "${it.firstName} ${it.lastName}"
                )
            }
        }
    }

    fun onVisitDateChange(value: Date) {
        _uiState.value = _uiState.value.copy(visitDate = value)
    }

    fun onGeneralHealthChange(value: HealthStatus) {
        _uiState.value = _uiState.value.copy(generalHealth = value)
    }

    fun onDrugUsageChange(value: Boolean) {
        _uiState.value = _uiState.value.copy(takingDrugs = value)
    }

    fun onCommentsChange(value: String) {
        _uiState.value = _uiState.value.copy(
            comments = value,
            commentsError = null
        )
    }

    /**
     * Validate and save overweight assessment
     * PDF Requirements: All fields are mandatory
     */
    fun saveAssessment(onSuccess: () -> Unit) {
        val state = _uiState.value

        // Validation - All fields are mandatory per PDF
        var hasError = false

        if (state.comments.isBlank()) {
            _uiState.value = state.copy(commentsError = "Comments are required")
            hasError = true
        }

//        if (state.takingDrugs == null) {
//            // This shouldn't happen with radio buttons, but good to check
//            hasError = true
//        }

        if (hasError) return

        _uiState.value = state.copy(isLoading = true)

        viewModelScope.launch {
            val assessment = OverweightAssessmentEntity(
                patientId = currentPatientId,
                visitDate = state.visitDate,
                generalHealth = state.generalHealth,
                takingDrugs = state.takingDrugs!!,
                comments = state.comments.trim()
            )

            val result = assessmentRepository.saveOverweightAssessment(assessment)

            result.fold(
                onSuccess = {
                    _uiState.value = state.copy(isLoading = false)
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.value = state.copy(
                        isLoading = false,
                        commentsError = error.message
                    )
                }
            )
        }
    }
}

/**
 * UI State for Overweight Assessment Screen
 */
data class OverweightAssessmentUiState(
    val patientName: String = "",
    val visitDate: Date = Date(),
    val generalHealth: HealthStatus = HealthStatus.GOOD,
    val takingDrugs: Boolean? = null,  // Nullable until user selects
    val comments: String = "",
    val commentsError: String? = null,
    val isLoading: Boolean = false,
//    val drugUsage: UsageStatus = UsageStatus.Yes
)