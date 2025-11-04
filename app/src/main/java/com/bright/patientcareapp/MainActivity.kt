package com.bright.patientcareapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bright.patientcareapp.ui.theme.PatientCareAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint  // Enables Hilt injection in this Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PatientCareAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PatientCareNavHost()  // Entry to nav graph
                }
            }
        }
    }
}

@Composable
fun PatientCareNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "registration"  // PDF flow starts here (Page 2)
    ) {
        // 1. Patient Registration Screen (Placeholder)
        composable("registration") {
            RegistrationScreen(
                onSave = { /* Later: Save logic + nav to vitals */ navController.navigate("vitals") }
            )
        }

        // 2. Vitals Screen (Placeholder; later BMI routes to assessment)
        composable("vitals") {
            VitalsScreen(
                onSave = { bmiLe25 ->
                    if (bmiLe25) {
                        navController.navigate("general_assessment")
                    } else {
                        navController.navigate("overweight_assessment")
                    }
                }
            )
        }

        // 3. General Assessment (BMI <=25 only)
        composable("general_assessment") {
            GeneralAssessmentScreen(
                onSave = { navController.navigate("patient_listing") }
            )
        }

        // 4. Overweight Assessment (BMI >25 only)
        composable("overweight_assessment") {
            OverweightAssessmentScreen(
                onSave = { navController.navigate("patient_listing") }
            )
        }

        // 5. Patient Listing (Final screen; accessible anytime)
        composable("patient_listing") {
            PatientListingScreen(
                onFilter = { /* Later: Date filter logic */ }
            )
        }
    }
}

// Placeholder Composable Screens (We'll replace these in later steps with real forms)
@Composable
fun RegistrationScreen(onSave: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text("Registration Page\n(Fields: ID, Names, DOB, Gender)")
        Button(onClick = onSave) { Text("Save & Next to Vitals") }
    }
}

@Composable
fun VitalsScreen(onSave: (Boolean) -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text("Vitals Page\n(Fields: Visit Date, Height, Weight, BMI)")
        Button(onClick = { onSave(true) }) { Text("Save (BMI <=25) → General") }  // Stub: Pass true/false
        Button(onClick = { onSave(false) }) { Text("Save (BMI >25) → Overweight") }
    }
}

@Composable
fun GeneralAssessmentScreen(onSave: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text("General Assessment\n(For BMI <=25)\n(Fields: Date, Health, Diet?, Comments)")
        Button(onClick = onSave) { Text("Save → Listing") }
    }
}

@Composable
fun OverweightAssessmentScreen(onSave: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text("Overweight Assessment\n(For BMI >=25)\n(Fields: Date, Health, Drugs?, Comments)")
        Button(onClick = onSave) { Text("Save → Listing") }
    }
}

@Composable
fun PatientListingScreen(onFilter: (String) -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text("Patient Listing\n(Name | Age | BMI Status)\n(With Date Filter)")
        Button(onClick = { onFilter("2023-01-01") }) { Text("Apply Filter") }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScreens() {
    PatientCareAppTheme {
        RegistrationScreen(onSave = {})
    }
}