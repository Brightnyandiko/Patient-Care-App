package com.bright.patientcareapp.ui.registration

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bright.patientcareapp.data.model.Gender
import com.bright.patientcareapp.util.DateUtils
import java.util.*

/**
 * Modern Patient Registration Screen following Material 3 design principles
 * Implements the mockup design with enhanced UX for healthcare applications
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onNavigateToVitals: (String) -> Unit,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Modern Page Header
        PageHeader(
            title = "Patient Registration",
            subtitle = "Register a new patient in the system"
        )

        // Main Registration Form Card
        HealthCard {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Form Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        text = "Patient Registration Form",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Divider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Patient ID (Unique identifier)
                HealthTextField(
                    value = uiState.patientId,
                    onValueChange = viewModel::onPatientIdChange,
                    label = "Patient ID *",
                    leadingIcon = Icons.Default.Create,
                    isError = uiState.patientIdError != null,
                    errorMessage = uiState.patientIdError ?: "",
                    modifier = Modifier.fillMaxWidth()
                )

                // Helper text for Patient ID
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = "Unique identifier for the patient",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Registration Date (Auto-filled, read-only)
                HealthTextField(
                    value = DateUtils.formatForDisplay(uiState.registrationDate),
                    onValueChange = { },
                    label = "Registration Date",
                    leadingIcon = Icons.Default.DateRange,
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // First Name
                HealthTextField(
                    value = uiState.firstName,
                    onValueChange = viewModel::onFirstNameChange,
                    label = "First Name *",
                    leadingIcon = Icons.Default.Person,
                    isError = uiState.firstNameError != null,
                    errorMessage = uiState.firstNameError ?: "",
                    modifier = Modifier.fillMaxWidth()
                )

                // Last Name
                HealthTextField(
                    value = uiState.lastName,
                    onValueChange = viewModel::onLastNameChange,
                    label = "Last Name *",
                    leadingIcon = Icons.Default.Person,
                    isError = uiState.lastNameError != null,
                    errorMessage = uiState.lastNameError ?: "",
                    modifier = Modifier.fillMaxWidth()
                )

                // Date of Birth (Clickable date picker)
                Column {
                    OutlinedTextField(
                        value = uiState.dateOfBirth?.let { DateUtils.formatForDisplay(it) } ?: "",
                        onValueChange = { },
                        label = { Text("Date of Birth *") },
                        leadingIcon = {
                            Icon(Icons.Default.Add, contentDescription = null)
                        },
                        trailingIcon = {
                            Icon(Icons.Default.DateRange, contentDescription = "Select date")
                        },
                        readOnly = true,
                        isError = uiState.dateOfBirthError != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showDatePicker(
                                    context = context,
                                    initialDate = uiState.dateOfBirth ?: Date(),
                                    onDateSelected = viewModel::onDateOfBirthChange
                                )
                            },
                        placeholder = { Text("Tap to select date of birth") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    )

                    if (uiState.dateOfBirthError != null) {
                        Text(
                            text = uiState.dateOfBirthError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }

                // Gender Selection (Styled dropdown)
                var genderExpanded by remember { mutableStateOf(false) }

                Column {
                    Text(
                        text = "Gender *",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = genderExpanded,
                        onExpandedChange = { genderExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = uiState.gender.displayName,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Select Gender") },
                            leadingIcon = {
                                Icon(
                                    if (uiState.gender == Gender.MALE) Icons.Rounded.Person
                                    else if (uiState.gender == Gender.FEMALE) Icons.Default.Person
                                    else Icons.Default.Person,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            ),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = { genderExpanded = false },
                            modifier = Modifier.exposedDropdownSize()
                        ) {
                            Gender.values().forEach { gender ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                when(gender) {
                                                    Gender.MALE -> Icons.Default.Person
                                                    Gender.FEMALE -> Icons.Default.Person
                                                    else -> Icons.Default.Person
                                                },
                                                contentDescription = null,
                                                modifier = Modifier.padding(end = 8.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Text(gender.displayName)
                                        }
                                    },
                                    onClick = {
                                        viewModel.onGenderChange(gender)
                                        genderExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons Section
        HealthCard {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Save & Continue Button
                Button(
                    onClick = {
                        viewModel.savePatient { patientId ->
                            onNavigateToVitals(patientId)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ) {
                    if (uiState.isLoading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Saving Patient...",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Done,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Save & Continue to Vitals",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Form validation summary
                if (uiState.patientIdError != null ||
                    uiState.firstNameError != null ||
                    uiState.lastNameError != null ||
                    uiState.dateOfBirthError != null) {

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "Please fix the errors above to continue",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        // Bottom spacing for better scroll behavior
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// Enhanced helper components
@Composable
private fun PageHeader(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun HealthCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

@Composable
private fun HealthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    isError: Boolean = false,
    errorMessage: String = "",
    readOnly: Boolean = false
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = leadingIcon?.let {
                { Icon(it, contentDescription = null) }
            },
            isError = isError,
            readOnly = readOnly,
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            ),
            singleLine = true
        )
        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

/**
 * Helper to show Android DatePickerDialog with Material 3 styling
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
    ).apply {
        // Set maximum date to today (no future dates for DOB)
        datePicker.maxDate = System.currentTimeMillis()
    }.show()
}

// Extension for Gender enum to have display names
private val Gender.displayName: String
    get() = when(this) {
        Gender.MALE -> "Male"
        Gender.FEMALE -> "Female"
        Gender.OTHER -> "Other"
        // Add other cases as needed based on your Gender enum
        else -> this.name.lowercase().replaceFirstChar { it.uppercase() }
    }