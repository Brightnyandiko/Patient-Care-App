package com.bright.patientcareapp.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Patient Registration Request (POST /patients/register)
@JsonClass(generateAdapter = true)
data class PatientRegistrationRequest(
    @Json(name = "patient_id") val patientId: String,
    @Json(name = "registration_date") val registrationDate: String,  // ISO format: yyyy-MM-dd
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String,
    @Json(name = "date_of_birth") val dateOfBirth: String,
    @Json(name = "gender") val gender: String
)

// Vitals Request (POST /vitals/add)
@JsonClass(generateAdapter = true)
data class VitalsRequest(
    @Json(name = "patient_id") val patientId: String,
    @Json(name = "visit_date") val visitDate: String,
    @Json(name = "height_cm") val heightCm: Double,
    @Json(name = "weight_kg") val weightKg: Double,
    @Json(name = "bmi") val bmi: Double
)

// General Assessment Request (POST /visits/add)
@JsonClass(generateAdapter = true)
data class GeneralAssessmentRequest(
    @Json(name = "patient_id") val patientId: String,
    @Json(name = "visit_date") val visitDate: String,
    @Json(name = "visit_type") val visitType: String = "general",  // Form type identifier
    @Json(name = "general_health") val generalHealth: String,
    @Json(name = "on_diet") val onDiet: Boolean,
    @Json(name = "comments") val comments: String
)

// Overweight Assessment Request (POST /visits/add)
@JsonClass(generateAdapter = true)
data class OverweightAssessmentRequest(
    @Json(name = "patient_id") val patientId: String,
    @Json(name = "visit_date") val visitDate: String,
    @Json(name = "visit_type") val visitType: String = "overweight",
    @Json(name = "general_health") val generalHealth: String,
    @Json(name = "taking_drugs") val takingDrugs: Boolean,
    @Json(name = "comments") val comments: String
)

// Generic API Response (adjust based on actual Postman responses)
@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String?,
    @Json(name = "data") val data: T?
)