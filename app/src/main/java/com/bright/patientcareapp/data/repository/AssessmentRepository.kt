package com.bright.patientcareapp.data.repository

import android.util.Log
import com.bright.patientcareapp.data.local.dao.GeneralAssessmentDao
import com.bright.patientcareapp.data.local.dao.OverweightAssessmentDao
import com.bright.patientcareapp.data.local.entity.GeneralAssessmentEntity
import com.bright.patientcareapp.data.local.entity.OverweightAssessmentEntity
import com.bright.patientcareapp.data.remote.PatientApiService
import com.bright.patientcareapp.data.remote.model.GeneralAssessmentRequest
import com.bright.patientcareapp.data.remote.model.OverweightAssessmentRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssessmentRepository @Inject constructor(
    private val generalDao: GeneralAssessmentDao,
    private val overweightDao: OverweightAssessmentDao,
    private val apiService: PatientApiService
) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * Save General Assessment (BMI <= 25)
     */
    suspend fun saveGeneralAssessment(assessment: GeneralAssessmentEntity): Result<Long> {
        return try {
            if (generalDao.assessmentExistsForDate(assessment.patientId, assessment.visitDate)) {
                return Result.failure(Exception("Assessment already exists for this date"))
            }

            val id = generalDao.insertAssessment(assessment)

            // Fire-and-forget API call
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = GeneralAssessmentRequest(
                        patientId = assessment.patientId,
                        visitDate = dateFormat.format(assessment.visitDate),
                        generalHealth = assessment.generalHealth.name,
                        onDiet = assessment.onDiet,
                        comments = assessment.comments
                    )
                    val response = apiService.addGeneralAssessment(request)
                    if (!response.isSuccessful) {
                        Log.e("AssessmentRepo", "API general assessment failed: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("AssessmentRepo", "API error: ${e.message}")
                }
            }

            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Save Overweight Assessment (BMI > 25)
     */
    suspend fun saveOverweightAssessment(assessment: OverweightAssessmentEntity): Result<Long> {
        return try {
            if (overweightDao.assessmentExistsForDate(assessment.patientId, assessment.visitDate)) {
                return Result.failure(Exception("Assessment already exists for this date"))
            }

            val id = overweightDao.insertAssessment(assessment)

            // Fire-and-forget API call
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = OverweightAssessmentRequest(
                        patientId = assessment.patientId,
                        visitDate = dateFormat.format(assessment.visitDate),
                        generalHealth = assessment.generalHealth.name,
                        takingDrugs = assessment.takingDrugs,
                        comments = assessment.comments
                    )
                    val response = apiService.addOverweightAssessment(request)
                    if (!response.isSuccessful) {
                        Log.e("AssessmentRepo", "API overweight assessment failed: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("AssessmentRepo", "API error: ${e.message}")
                }
            }

            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}