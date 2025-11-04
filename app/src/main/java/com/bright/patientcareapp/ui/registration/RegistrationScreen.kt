package com.bright.patientcareapp.ui.registration

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bright.patientcareapp.data.model.Gender
import com.bright.patientcareapp.util.DateUtils
import java.util.*

// Add missing import at top of file
import androidx.compose.foundation.clickable

/**
 * Patient Registration Screen (PDF Page 2)
 * Fields: Patient ID, Registration Date, First/Last Name, DOB, Gender
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onNavigateToVitals: (String) -> Unit,  // Pass patientId to next screen
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Patient Registration") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Enter patient details",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Patient ID (Unique)
            OutlinedTextField(
                value = uiState.patientId,
                onValueChange = viewModel::onPatientIdChange,
                label = { Text("Patient ID *") },
                supportingText = { Text("Unique identifier") },
                isError = uiState.patientIdError != null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            if (uiState.patientIdError != null) {
                Text(
                    text = uiState.patientIdError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Registration Date (Auto-filled with today)
            OutlinedTextField(
                value = DateUtils.formatForDisplay(uiState.registrationDate),
                onValueChange = {},
                label = { Text("Registration Date") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            // First Name
            OutlinedTextField(
                value = uiState.firstName,
                onValueChange = viewModel::onFirstNameChange,
                label = { Text("First Name *") },
                isError = uiState.firstNameError != null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            if (uiState.firstNameError != null) {
                Text(
                    text = uiState.firstNameError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Last Name
            OutlinedTextField(
                value = uiState.lastName,
                onValueChange = viewModel::onLastNameChange,
                label = { Text("Last Name *") },
                isError = uiState.lastNameError != null,
                modifier = Modifier.fillMaxSize(),
                singleLine = true
            )
            if (uiState.lastNameError != null) {
                Text(
                    text = uiState.lastNameError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Date of Birth (Date Picker)
            OutlinedTextField(
                value = uiState.dateOfBirth?.let { DateUtils.formatForDisplay(it) } ?: "",
                onValueChange = {},
                label = { Text("Date of Birth *") },
                readOnly = true,
                isError = uiState.dateOfBirthError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        Modifier.clickable {
                            showDatePicker(
                                context = context,
                                initialDate = uiState.dateOfBirth ?: Date(),
                                onDateSelected = viewModel::onDateOfBirthChange
                            )
                        }
                    ),
                placeholder = { Text("Tap to select date") }
            )
            if (uiState.dateOfBirthError != null) {
                Text(
                    text = uiState.dateOfBirthError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Gender (Dropdown)
            var genderExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = genderExpanded,
                onExpandedChange = { genderExpanded = it }
            ) {
                OutlinedTextField(
                    value = uiState.gender.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gender *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = { genderExpanded = false }
                ) {
                    Gender.values().forEach { gender ->
                        DropdownMenuItem(
                            text = { Text(gender.name) },
                            onClick = {
                                viewModel.onGenderChange(gender)
                                genderExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save Button
            Button(
                onClick = {
                    viewModel.savePatient { patientId ->
                        onNavigateToVitals(patientId)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save & Continue to Vitals")
                }
            }
        }
    }
}

/**
 * Helper to show Android DatePickerDialog
 */
private fun showDatePicker(
    context: android.content.Context,
    initialDate: Date,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance().apply { time = initialDate }

    DatePickerDialog(
        context,
        { _, year, month, day ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, day)
            }.time
            onDateSelected(selectedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

