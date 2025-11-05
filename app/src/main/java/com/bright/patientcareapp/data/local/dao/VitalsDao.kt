package com.bright.patientcareapp.data.local.dao

import androidx.room.*
import com.bright.patientcareapp.data.local.entity.VitalsEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface VitalsDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)  // Abort if duplicate date for same patient
    suspend fun insertVitals(vitals: VitalsEntity): Long

    @Query("SELECT * FROM vitals WHERE patientId = :patientId ORDER BY visitDate DESC")
    fun getVitalsForPatient(patientId: String): Flow<List<VitalsEntity>>

    @Query("SELECT * FROM vitals WHERE patientId = :patientId ORDER BY visitDate DESC LIMIT 1")
    suspend fun getLatestVitals(patientId: String): VitalsEntity?  // For BMI status in listing

    @Query("SELECT EXISTS(SELECT 1 FROM vitals WHERE patientId = :patientId AND visitDate = :visitDate)")
    suspend fun vitalsExistsForDate(patientId: String, visitDate: Date): Boolean

    @Query("SELECT DISTINCT patientId FROM vitals WHERE DATE(visitDate/1000, 'unixepoch') = DATE(:timestamp/1000, 'unixepoch')")
    suspend fun getPatientsWithVisitsOnDate(timestamp: Long): List<String>
}