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
import com.bright.patientcareapp.data.remote.model.AuthState
import com.bright.patientcareapp.ui.theme.PatientCareAppTheme
import com.bright.patientcareapp.ui.registration.RegistrationScreen
import com.bright.patientcareapp.ui.vitals.VitalsScreen
import com.bright.patientcareapp.ui.assessment.GeneralAssessmentScreen
import com.bright.patientcareapp.ui.assessment.OverweightAssessmentScreen
import com.bright.patientcareapp.ui.auth.LoginScreen
import com.bright.patientcareapp.ui.auth.SignupScreen
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

//    NavHost(
//        navController = navController,
//        startDestination = "patient_listing"  // Start with listing (healthcare dashboard)
//    ) {
//        // Patient Listing Screen - Central dashboard and start destination
//        composable("patient_listing") {
//            PatientListingScreen(
//                onNavigateToRegistration = {
//                    navController.navigate("registration")
//                }
//            )
//        }
//
//        // Patient Registration Screen - First step of patient workflow
//        composable("registration") {
//            RegistrationScreen(
//                onNavigateToVitals = { patientId ->
//                    // Navigate to vitals with patient ID
//                    navController.navigate("vitals/$patientId")
//                }
//            )
//        }
//
//        // Vitals Screen - Second step, includes BMI calculation and routing
//        composable(
//            route = "vitals/{patientId}",
//            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
//
//            VitalsScreen(
//                patientId = patientId,
//                onNavigateToAssessment = { shouldShowGeneral ->
//                    // BMI-based routing logic (PDF Page 3 requirement)
//                    if (shouldShowGeneral) {
//                        // BMI <= 25: Navigate to General Assessment
//                        navController.navigate("general_assessment/$patientId")
//                    } else {
//                        // BMI > 25: Navigate to Overweight Assessment
//                        navController.navigate("overweight_assessment/$patientId")
//                    }
//                },
//                onClose = {
//                    navController.popBackStack("patient_listing", false)
//                }
//            )
//        }
//
//        // General Assessment Screen - For patients with BMI <= 25
//        composable(
//            route = "general_assessment/{patientId}",
//            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
//
//            GeneralAssessmentScreen(
//                patientId = patientId,
//                onNavigateToListing = {
//                    // Navigate to patient listing and clear back stack
//                    navController.navigate("patient_listing") {
//                        popUpTo("patient_listing") { inclusive = true }
//                    }
//                },
//                onClose = {
//                    navController.popBackStack("patient_listing", false)
//                }
//            )
//        }
//
//        // Overweight Assessment Screen - For patients with BMI > 25
//        composable(
//            route = "overweight_assessment/{patientId}",
//            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
//
//            OverweightAssessmentScreen(
//                patientId = patientId,
//                onNavigateToListing = {
//                    // Navigate to patient listing and clear back stack
//                    navController.navigate("patient_listing") {
//                        popUpTo("patient_listing") { inclusive = true }
//                    }
//                },
//                onClose = {
//                    navController.popBackStack("patient_listing", false)
//                }
//            )
//        }
//    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Login Screen
        composable("login") {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate("signup")
                },
                onLoginSuccess = { navController.navigate("patient_listing") }
            )
        }

        // Signup Screen
        composable("signup") {
            SignupScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Patient Listing Screen - Central dashboard
        composable("patient_listing") {
            PatientListingScreen(
                onNavigateToRegistration = {
                    navController.navigate("registration")
                }
            )
        }

        // ... rest of existing routes remain the same
        composable("registration") {
            RegistrationScreen(
                onNavigateToVitals = { patientId ->
                    navController.navigate("vitals/$patientId")
                }
            )
        }

        composable(
            route = "vitals/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""

            VitalsScreen(
                patientId = patientId,
                onNavigateToAssessment = { shouldShowGeneral ->
                    if (shouldShowGeneral) {
                        navController.navigate("general_assessment/$patientId")
                    } else {
                        navController.navigate("overweight_assessment/$patientId")
                    }
                },
                onClose = {
                    navController.popBackStack("patient_listing", false)
                }
            )
        }

        composable(
            route = "general_assessment/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""

            GeneralAssessmentScreen(
                patientId = patientId,
                onNavigateToListing = {
                    navController.navigate("patient_listing") {
                        popUpTo("patient_listing") { inclusive = true }
                    }
                },
                onClose = {
                    navController.popBackStack("patient_listing", false)
                }
            )
        }

        composable(
            route = "overweight_assessment/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""

            OverweightAssessmentScreen(
                patientId = patientId,
                onNavigateToListing = {
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