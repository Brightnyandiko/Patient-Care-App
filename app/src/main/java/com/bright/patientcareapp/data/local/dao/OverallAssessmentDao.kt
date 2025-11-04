package com.bright.patientcareapp.data.local.dao

import androidx.room.*
import com.bright.patientcareapp.data.local.entity.OverweightAssessmentEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface OverweightAssessmentDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAssessment(assessment: OverweightAssessmentEntity): Long

    @Query("SELECT * FROM overweight_assessments WHERE patientId = :patientId ORDER BY visitDate DESC")
    fun getAssessmentsForPatient(patientId: String): Flow<List<OverweightAssessmentEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM overweight_assessments WHERE patientId = :patientId AND visitDate = :visitDate)")
    suspend fun assessmentExistsForDate(patientId: String, visitDate: Date): Boolean
}