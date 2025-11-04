package com.bright.patientcareapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Vitals Entity - Stores patient vitals with BMI calculation (PDF Page 3)
 * Fields: Visit Date, Height (cm), Weight (kg), BMI (auto-calculated)
 * Rule: Multiple entries per patient allowed, but on different dates
 */
@Entity(
    tableName = "vitals",
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
        Index(value = ["patientId", "visitDate"], unique = true)  // Prevent duplicate dates
    ]
)
data class VitalsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val patientId: String,
    val visitDate: Date,
    val heightCm: Double,  // Height in centimeters
    val weightKg: Double,  // Weight in kilograms
    val bmi: Double        // BMI = weight(kg) / (height(m))^2
)