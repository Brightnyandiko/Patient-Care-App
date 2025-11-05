package com.bright.patientcareapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Patient Care App
 * Enables Hilt dependency injection throughout the app
 */
@HiltAndroidApp
class PatientCareApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Any app-wide initialization can go here
    }
}