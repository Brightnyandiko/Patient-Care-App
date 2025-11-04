package com.bright.patientcareapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bright.patientcareapp.data.model.HealthStatus
import java.util.Date

/**
 * Overweight Assessment Entity - For patients with BMI > 25 (PDF Page 5)
 * Fields: Visit Date, General Health, Drug usage, Comments
 */
@Entity(
    tableName = "overweight_assessments",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["patientId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["patientId"]),
        Index(value = ["patientId", "visitDate"], unique = true)
    ]
)
data class OverweightAssessmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val patientId: String,
    val visitDate: Date,
    val generalHealth: HealthStatus,  // Good or Poor
    val takingDrugs: Boolean,         // Are you currently taking any drugs?
    val comments: String
)