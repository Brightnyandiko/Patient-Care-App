package com.bright.patientcareapp.data.local.dao

import androidx.room.*
import com.bright.patientcareapp.data.local.entity.PatientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)  // Abort if duplicate patientId (unique constraint)
    suspend fun insertPatient(patient: PatientEntity): Long

    @Query("SELECT * FROM patients WHERE patientId = :patientId LIMIT 1")
    suspend fun getPatientByPatientId(patientId: String): PatientEntity?

    @Query("SELECT * FROM patients")
    fun getAllPatients(): Flow<List<PatientEntity>>  // Flow for reactive updates in UI

    @Query("SELECT EXISTS(SELECT 1 FROM patients WHERE patientId = :patientId)")
    suspend fun patientExists(patientId: String): Boolean  // Check uniqueness before insert
}