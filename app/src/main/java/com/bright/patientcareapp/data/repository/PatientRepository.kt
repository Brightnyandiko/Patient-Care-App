package com.bright.patientcareapp.data.repository

import android.util.Log
import com.bright.patientcareapp.data.local.dao.PatientDao
import com.bright.patientcareapp.data.local.entity.PatientEntity
import com.bright.patientcareapp.data.remote.PatientApiService
import com.bright.patientcareapp.data.remote.model.PatientRegistrationRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PatientRepository @Inject constructor(
    private val patientDao: PatientDao,
    private val apiService: PatientApiService
) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * Register a new patient (local + API)
     * PDF Rule: Patient can only be registered once (unique ID)
     */
    suspend fun registerPatient(patient: PatientEntity): Result<Long> {
        return try {
            // Check if patient already exists
            if (patientDao.patientExists(patient.patientId)) {
                return Result.failure(Exception("Patient ID already exists"))
            }

            // Save locally first (offline-first approach)
            val id = patientDao.insertPatient(patient)

            // Fire-and-forget API call (PDF Page 6 requirement)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = PatientRegistrationRequest(
                        patientId = patient.patientId,
                        registrationDate = dateFormat.format(patient.registrationDate),
                        firstName = patient.firstName,
                        lastName = patient.lastName,
                        dateOfBirth = dateFormat.format(patient.dateOfBirth),
                        gender = patient.gender.name
                    )
                    val response = apiService.registerPatient(request)
                    if (!response.isSuccessful) {
                        Log.e("PatientRepo", "API registration failed: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("PatientRepo", "API error (non-blocking): ${e.message}")
                }
            }

            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPatient(patientId: String): PatientEntity? {
        return patientDao.getPatientByPatientId(patientId)
    }

    fun getAllPatients(): Flow<List<PatientEntity>> {
        return patientDao.getAllPatients()
    }
}