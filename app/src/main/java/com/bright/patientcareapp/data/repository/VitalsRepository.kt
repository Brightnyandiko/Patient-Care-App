package com.bright.patientcareapp.data.repository


import android.util.Log
import com.bright.patientcareapp.data.local.dao.VitalsDao
import com.bright.patientcareapp.data.local.entity.VitalsEntity
import com.bright.patientcareapp.data.remote.PatientApiService
import com.bright.patientcareapp.data.remote.model.VitalsRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VitalsRepository @Inject constructor(
    private val vitalsDao: VitalsDao,
    private val apiService: PatientApiService
) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * Save vitals (local + API)
     * PDF Rule: Multiple entries allowed per patient, but on different dates
     * Returns BMI for routing logic (General vs Overweight assessment)
     */
    suspend fun saveVitals(vitals: VitalsEntity): Result<Double> {
        return try {
            // Check for duplicate date
            if (vitalsDao.vitalsExistsForDate(vitals.patientId, vitals.visitDate)) {
                return Result.failure(Exception("Vitals already recorded for this date"))
            }

            // Save locally
            vitalsDao.insertVitals(vitals)

            // Fire-and-forget API call
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = VitalsRequest(
                        patientId = vitals.patientId,
                        visitDate = dateFormat.format(vitals.visitDate),
                        heightCm = vitals.heightCm,
                        weightKg = vitals.weightKg,
                        bmi = vitals.bmi
                    )
                    val response = apiService.addVitals(request)
                    if (!response.isSuccessful) {
                        Log.e("VitalsRepo", "API vitals failed: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("VitalsRepo", "API error: ${e.message}")
                }
            }

            Result.success(vitals.bmi)  // Return BMI for routing
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLatestVitals(patientId: String): VitalsEntity? {
        return vitalsDao.getLatestVitals(patientId)
    }

    fun getVitalsForPatient(patientId: String): Flow<List<VitalsEntity>> {
        return vitalsDao.getVitalsForPatient(patientId)
    }

    /**
     * Calculate BMI from height and weight
     * Formula: BMI = weight(kg) / (height(m))^2
     */
    fun calculateBmi(heightCm: Double, weightKg: Double): Double {
        val heightM = heightCm / 100.0
        return weightKg / (heightM * heightM)
    }
}