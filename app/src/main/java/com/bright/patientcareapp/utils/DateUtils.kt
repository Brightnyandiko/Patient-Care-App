package com.bright.patientcareapp.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun formatForDisplay(date: Date): String {
        return displayFormat.format(date)
    }

    fun formatForApi(date: Date): String {
        return apiFormat.format(date)
    }

    fun parseFromDisplay(dateString: String): Date? {
        return try {
            displayFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Calculate age from date of birth (for Patient Listing - PDF Page 6)
     */
    fun calculateAge(dateOfBirth: Date): Int {
        val today = Calendar.getInstance()
        val birthDate = Calendar.getInstance().apply { time = dateOfBirth }

        var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }
}