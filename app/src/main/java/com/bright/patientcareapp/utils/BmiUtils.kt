package com.bright.patientcareapp.util

import com.bright.patientcareapp.data.model.BmiStatus

object BmiUtils {

    /**
     * Determine BMI status category (PDF Page 6)
     * - Underweight: BMI < 18.5
     * - Normal: 18.5 <= BMI < 25
     * - Overweight: BMI >= 25
     */
    fun getBmiStatus(bmi: Double): BmiStatus {
        return when {
            bmi < 18.5 -> BmiStatus.UNDERWEIGHT
            bmi < 25.0 -> BmiStatus.NORMAL
            else -> BmiStatus.OVERWEIGHT
        }
    }

    /**
     * Check if BMI requires General Assessment (<=25) vs Overweight Assessment (>25)
     * PDF Page 3 routing rule
     */
    fun shouldShowGeneralAssessment(bmi: Double): Boolean {
        return bmi <= 25.0
    }
}