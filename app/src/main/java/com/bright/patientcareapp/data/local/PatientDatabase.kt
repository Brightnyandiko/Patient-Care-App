package com.bright.patientcareapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bright.patientcareapp.data.local.dao.*
import com.bright.patientcareapp.data.local.entity.*

/**
 * Room Database for Patient Care App
 * Stores all local data per PDF requirements (Page 6 - local storage on every save)
 */
@Database(
    entities = [
        PatientEntity::class,
        VitalsEntity::class,
        GeneralAssessmentEntity::class,
        OverweightAssessmentEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)  // Register converters for Date and enums
abstract class PatientDatabase : RoomDatabase() {

    abstract fun patientDao(): PatientDao
    abstract fun vitalsDao(): VitalsDao
    abstract fun generalAssessmentDao(): GeneralAssessmentDao
    abstract fun overweightAssessmentDao(): OverweightAssessmentDao
}