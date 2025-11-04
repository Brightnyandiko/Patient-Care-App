package com.bright.patientcareapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bright.patientcareapp.data.model.HealthStatus
import java.util.Date

/**
 * General Assessment Entity - For patients with BMI <= 25 (PDF Page 4)
 * Fields: Visit Date, General Health, Diet history, Comments
 */
@Entity(
    tableName = "general_assessments",
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
data class GeneralAssessmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val patientId: String,
    val visitDate: Date,
    val generalHealth: HealthStatus,  // Good or Poor
    val onDiet: Boolean,              // Have you been on a diet to lose weight?
    val comments: String
)