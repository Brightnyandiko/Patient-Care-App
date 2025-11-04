package com.bright.patientcareapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bright.patientcareapp.data.model.Gender
import java.util.Date

/**
 * Patient Entity - Stores patient registration data (PDF Page 2)
 * Fields: Patient ID (unique), Registration Date, Names, DOB, Gender
 */
@Entity(
    tableName = "patients",
    indices = [Index(value = ["patientId"], unique = true)]  // Enforce unique patient ID
)
data class PatientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,  // Auto-increment primary key for Room

    val patientId: String,  // Unique identifier from form (PDF: cannot be shared)
    val registrationDate: Date,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: Date,
    val gender: Gender
)