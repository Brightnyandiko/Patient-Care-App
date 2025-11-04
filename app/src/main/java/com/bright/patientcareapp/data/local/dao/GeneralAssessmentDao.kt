package com.bright.patientcareapp.data.local.dao

import androidx.room.*
import com.bright.patientcareapp.data.local.entity.GeneralAssessmentEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface GeneralAssessmentDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAssessment(assessment: GeneralAssessmentEntity): Long

    @Query("SELECT * FROM general_assessments WHERE patientId = :patientId ORDER BY visitDate DESC")
    fun getAssessmentsForPatient(patientId: String): Flow<List<GeneralAssessmentEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM general_assessments WHERE patientId = :patientId AND visitDate = :visitDate)")
    suspend fun assessmentExistsForDate(patientId: String, visitDate: Date): Boolean
}