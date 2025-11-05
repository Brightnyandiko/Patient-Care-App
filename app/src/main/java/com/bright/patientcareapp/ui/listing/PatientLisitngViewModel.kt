package com.bright.patientcareapp.ui.listing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bright.patientcareapp.data.model.BmiStatus
import com.bright.patientcareapp.data.repository.PatientRepository
import com.bright.patientcareapp.data.repository.VitalsRepository
import com.bright.patientcareapp.util.BmiUtils
import com.bright.patientcareapp.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel for Patient Listing Screen (PDF Page 6)
 * Manages patient data display, age calculation, BMI status, and date filtering
 */
@HiltViewModel
class PatientListingViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val vitalsRepository: VitalsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientListingUiState())
    val uiState: StateFlow<PatientListingUiState> = _uiState.asStateFlow()

    init {
        loadPatients()
    }

    /**
     * Load and combine patient data with their latest vitals
     * Calculates age and BMI status for each patient
     */
    private fun loadPatients() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                patientRepository.getAllPatients()
                    .combine(_uiState.map { it.filterDate }) { patients, filterDate ->
                        patients.map { patient ->
                            // Calculate age from date of birth
                            val age = DateUtils.calculateAge(patient.dateOfBirth)

                            // Get latest vitals for BMI status
                            val latestVitals = vitalsRepository.getLatestVitals(patient.patientId)
                            val bmiStatus = latestVitals?.let { vitals ->
                                BmiUtils.getBmiStatus(vitals.bmi)
                            } ?: BmiStatus.NORMAL // Default if no vitals recorded

                            PatientListItem(
                                patientId = patient.patientId,
                                fullName = "${patient.firstName} ${patient.lastName}",
                                age = age,
                                bmiStatus = bmiStatus,
                                hasVisitOnDate = filterDate?.let { date ->
                                    // Check if patient has any visits (vitals/assessments) on filter date
                                    hasVisitOnDate(patient.patientId, date)
                                } ?: true
                            )
                        }.filter { patient ->
                            // Apply date filter if set
                            patient.hasVisitOnDate
                        }
                    }
                    .collect { patientList ->
                        _uiState.value = _uiState.value.copy(
                            patients = patientList,
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    patients = emptyList(),
                    isLoading = false
                )
            }
        }
    }

    /**
     * Check if patient has any visit (vitals or assessments) on specific date
     * This is a simplified version - in a real app you'd check all visit types
     */
    private suspend fun hasVisitOnDate(patientId: String, date: Date): Boolean {
        return try {
            // For now, we'll check vitals only
            // In a complete implementation, you'd also check assessment tables
            val vitals = vitalsRepository.getVitalsForPatient(patientId).first()
            val targetDateString = DateUtils.formatForApi(date)

            vitals.any { vital ->
                DateUtils.formatForApi(vital.visitDate) == targetDateString
            }
        } catch (e: Exception) {
            false
        }
    }

    fun onDateFilterChange(date: Date) {
        _uiState.value = _uiState.value.copy(filterDate = date)
        // Patients will be automatically refiltered due to the combine operation in loadPatients()
    }

    fun clearDateFilter() {
        _uiState.value = _uiState.value.copy(filterDate = null)
        // Patients will be automatically refreshed due to the combine operation
    }

    fun refreshData() {
        loadPatients()
    }
}

/**
 * UI State for Patient Listing Screen
 */
data class PatientListingUiState(
    val patients: List<PatientListItem> = emptyList(),
    val filterDate: Date? = null,
    val isLoading: Boolean = false
)

/**
 * Data class representing a patient in the listing
 * Combines patient info with calculated age and BMI status
 */
data class PatientListItem(
    val patientId: String,
    val fullName: String,
    val age: Int,
    val bmiStatus: BmiStatus,
    val hasVisitOnDate: Boolean = true
)