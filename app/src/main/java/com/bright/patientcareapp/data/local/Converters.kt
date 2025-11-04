package com.bright.patientcareapp.data.local

import androidx.room.TypeConverter
import com.bright.patientcareapp.data.model.Gender
import com.bright.patientcareapp.data.model.HealthStatus
import java.util.Date

/**
 * Room TypeConverters for complex types (dates, enums)
 * Needed for storing Date and enum fields in SQLite
 */
class Converters {

    // Date conversions (for registration dates, visit dates, DOB)
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // Gender enum conversions
    @TypeConverter
    fun fromGender(value: Gender?): String? {
        return value?.name
    }

    @TypeConverter
    fun toGender(value: String?): Gender? {
        return value?.let { Gender.valueOf(it) }
    }

    // HealthStatus enum conversions
    @TypeConverter
    fun fromHealthStatus(value: HealthStatus?): String? {
        return value?.name
    }

    @TypeConverter
    fun toHealthStatus(value: String?): HealthStatus? {
        return value?.let { HealthStatus.valueOf(it) }
    }
}