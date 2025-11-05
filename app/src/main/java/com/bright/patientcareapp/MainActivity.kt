//package com.bright.patientcareapp
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.navArgument
//import com.bright.patientcareapp.ui.registration.RegistrationScreen
//import com.bright.patientcareapp.ui.theme.PatientCareAppTheme
//import com.bright.patientcareapp.ui.vitals.VitalsScreen
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint  // Enables Hilt injection in this Activity
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            PatientCareAppTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    PatientCareNavHost()  // Entry to nav graph
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun PatientCareNavHost() {
//    val navController = rememberNavController()
//
//    NavHost(
//        navController = navController,
//        startDestination = "registration"  // PDF flow starts here (Page 2)
//    ) {
//        // 1. Patient Registration Screen (Placeholder)
//        composable("registration") {
//            RegistrationScreen(
//                onSave = { /* Later: Save logic + nav to vitals */ navController.navigate("vitals") }
//            )
//        }
//
//        // 2. Vitals Screen (Placeholder; later BMI routes to assessment)
//        composable("vitals") {
//            VitalsScreen(
//                onSave = { bmiLe25 ->
//                    if (bmiLe25) {
//                        navController.navigate("general_assessment")
//                    } else {
//                        navController.navigate("overweight_assessment")
//                    }
//                }
//            )
//        }
//
//        // 3. General Assessment (BMI <=25 only)
//        composable("general_assessment") {
//            GeneralAssessmentScreen(
//                onSave = { navController.navigate("patient_listing") }
//            )
//        }
//
//        // 4. Overweight Assessment (BMI >25 only)
//        composable("overweight_assessment") {
//            OverweightAssessmentScreen(
//                onSave = { navController.navigate("patient_listing") }
//            )
//        }
//
//        // 5. Patient Listing (Final screen; accessible anytime)
//        composable("patient_listing") {
//            PatientListingScreen(
//                onFilter = { /* Later: Date filter logic */ }
//            )
//        }
//    }
//}
//
//// Placeholder Composable Screens (We'll replace these in later steps with real forms)
////@Composable
////fun RegistrationScreen(onSave: () -> Unit) {
////    Surface(modifier = Modifier.fillMaxSize()) {
////        Text("Registration Page\n(Fields: ID, Names, DOB, Gender)")
////        Button(onClick = onSave) { Text("Save & Next to Vitals") }
////    }
////}
////
////@Composable
////fun VitalsScreen(onSave: (Boolean) -> Unit) {
////    Surface(modifier = Modifier.fillMaxSize()) {
////        Text("Vitals Page\n(Fields: Visit Date, Height, Weight, BMI)")
////        Button(onClick = { onSave(true) }) { Text("Save (BMI <=25) → General") }  // Stub: Pass true/false
////        Button(onClick = { onSave(false) }) { Text("Save (BMI >25) → Overweight") }
////    }
////}
////
////@Composable
////fun GeneralAssessmentScreen(onSave: () -> Unit) {
////    Surface(modifier = Modifier.fillMaxSize()) {
////        Text("General Assessment\n(For BMI <=25)\n(Fields: Date, Health, Diet?, Comments)")
////        Button(onClick = onSave) { Text("Save → Listing") }
////    }
////}
////
////@Composable
////fun OverweightAssessmentScreen(onSave: () -> Unit) {
////    Surface(modifier = Modifier.fillMaxSize()) {
////        Text("Overweight Assessment\n(For BMI >=25)\n(Fields: Date, Health, Drugs?, Comments)")
////        Button(onClick = onSave) { Text("Save → Listing") }
////    }
////}
////
////@Composable
////fun PatientListingScreen(onFilter: (String) -> Unit) {
////    Surface(modifier = Modifier.fillMaxSize()) {
////        Text("Patient Listing\n(Name | Age | BMI Status)\n(With Date Filter)")
////        Button(onClick = { onFilter("2023-01-01") }) { Text("Apply Filter") }
////    }
////}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewScreens() {
//    PatientCareAppTheme {
//        RegistrationScreen(onSave = {})
//    }
//}

package com.bright.patientcareapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bright.patientcareapp.ui.theme.PatientCareAppTheme
import com.bright.patientcareapp.ui.registration.RegistrationScreen
import com.bright.patientcareapp.ui.vitals.VitalsScreen
import com.bright.patientcareapp.ui.assessment.GeneralAssessmentScreen
import com.bright.patientcareapp.ui.assessment.OverweightAssessmentScreen
import com.bright.patientcareapp.ui.listing.PatientListingScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity - Entry point for Patient Care App
 * Implements complete navigation flow as per PDF requirements:
 * Registration → Vitals → Assessment (BMI-based routing) → Patient Listing
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PatientCareAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PatientCareApp()
                }
            }
        }
    }
}

@Composable
fun PatientCareApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "patient_listing"  // Start with listing (healthcare dashboard)
    ) {
        // Patient Listing Screen - Central dashboard and start destination
        composable("patient_listing") {
            PatientListingScreen(
                onNavigateToRegistration = {
                    navController.navigate("registration")
                }
            )
        }

        // Patient Registration Screen - First step of patient workflow
        composable("registration") {
            RegistrationScreen(
                onNavigateToVitals = { patientId ->
                    // Navigate to vitals with patient ID
                    navController.navigate("vitals/$patientId")
                }
            )
        }

        // Vitals Screen - Second step, includes BMI calculation and routing
        composable(
            route = "vitals/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""

            VitalsScreen(
                patientId = patientId,
                onNavigateToAssessment = { shouldShowGeneral ->
                    // BMI-based routing logic (PDF Page 3 requirement)
                    if (shouldShowGeneral) {
                        // BMI <= 25: Navigate to General Assessment
                        navController.navigate("general_assessment/$patientId")
                    } else {
                        // BMI > 25: Navigate to Overweight Assessment
                        navController.navigate("overweight_assessment/$patientId")
                    }
                },
                onClose = {
                    navController.popBackStack("patient_listing", false)
                }
            )
        }

        // General Assessment Screen - For patients with BMI <= 25
        composable(
            route = "general_assessment/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""

            GeneralAssessmentScreen(
                patientId = patientId,
                onNavigateToListing = {
                    // Navigate to patient listing and clear back stack
                    navController.navigate("patient_listing") {
                        popUpTo("patient_listing") { inclusive = true }
                    }
                },
                onClose = {
                    navController.popBackStack("patient_listing", false)
                }
            )
        }

        // Overweight Assessment Screen - For patients with BMI > 25
        composable(
            route = "overweight_assessment/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""

            OverweightAssessmentScreen(
                patientId = patientId,
                onNavigateToListing = {
                    // Navigate to patient listing and clear back stack
                    navController.navigate("patient_listing") {
                        popUpTo("patient_listing") { inclusive = true }
                    }
                },
                onClose = {
                    navController.popBackStack("patient_listing", false)
                }
            )
        }
    }
}