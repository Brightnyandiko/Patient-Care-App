package com.bright.patientcareapp.data.model

// Gender options for Patient Registration (PDF Page 2)
enum class Gender {
    MALE,
    FEMALE,
    OTHER
}

// General health status for assessments (PDF Pages 4-5)
enum class HealthStatus {
    GOOD,
    POOR
}

// BMI status categories for Patient Listing (PDF Page 6)
enum class BmiStatus {
    UNDERWEIGHT,  // BMI < 18.5
    NORMAL,       // 18.5 <= BMI < 25
    OVERWEIGHT    // BMI >= 25
}