package com.bright.patientcareapp.ui.listing

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bright.patientcareapp.data.model.BmiStatus
import com.bright.patientcareapp.util.DateUtils
import java.util.*

/**
 * Patient Listing Screen (PDF Page 6) - Final destination for all flows
 * Displays: Patient Name | Age | Last BMI Status
 * Features: Date filtering, BMI status categorization
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientListingScreen(
    onNavigateToRegistration: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: PatientListingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val logoutState by viewModel.logoutState.collectAsState()



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Modern Page Header
//        PageHeader(
//            title = "Patient Listing",
//            subtitle = "Overview of all registered patients and their health status"
//        )

        LaunchedEffect(logoutState) {
            if (logoutState) {
                viewModel.resetLogoutState()
                onLogout() // Navigate to login screen
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Modern Page Header with logout
            PageHeaderWithLogout(
                title = "Patient Listing",
                subtitle = "Overview of all registered patients and their health status",
                onLogout = viewModel::logout
            )

            HealthCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Filter Section Title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            text = "Filter & Actions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Date Filter - FIXED VERSION
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = uiState.filterDate?.let { DateUtils.formatForDisplay(it) }
                                    ?: "",
                                onValueChange = { },
                                leadingIcon = {
                                    Icon(Icons.Default.DateRange, contentDescription = null)
                                },
                                trailingIcon = {
                                    Row {
                                        if (uiState.filterDate != null) {
                                            IconButton(onClick = viewModel::clearDateFilter) {
                                                Icon(
                                                    Icons.Default.Clear,
                                                    contentDescription = "Clear filter",
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
//                                    Icon(
//                                        Icons.Default.KeyboardArrowDown,
//                                        contentDescription = "Select date"
//                                    )
                                    }
                                },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Tap to filter by date") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(
                                        alpha = 0.5f
                                    )
                                ),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            // Invisible clickable overlay that captures all touch events
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable {
                                        showDatePicker(
                                            context = context,
                                            initialDate = uiState.filterDate ?: Date(),
                                            onDateSelected = viewModel::onDateFilterChange
                                        )
                                    }
                            )
                        }

                        // Add Patient Button
                        Button(
                            onClick = onNavigateToRegistration,
                            modifier = Modifier.height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            ),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Add Patient",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Filter Results Summary
                    if (uiState.filterDate != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                    androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(16.dp)
                            )
                            Text(
                                text = "Showing patients with visits on ${
                                    DateUtils.formatForDisplay(
                                        uiState.filterDate!!
                                    )
                                }",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Patient Statistics Card
            PatientStatsCard(
                totalPatients = uiState.patients.size,
                filteredCount = if (uiState.filterDate != null) uiState.patients.size else null
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Patient List Card
            HealthCard {
                Column {
                    // List Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Icon(
//                        Icons.Default.People,
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            text = "Patient Directory",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Refresh Button
                        IconButton(
                            onClick = viewModel::refreshData,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (uiState.isLoading) {
                        // Loading State
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Loading patient data...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    } else if (uiState.patients.isEmpty()) {
                        // Empty State
                        EmptyPatientState(
                            isFiltered = uiState.filterDate != null,
                            onAddPatient = onNavigateToRegistration,
                            onClearFilter = viewModel::clearDateFilter
                        )
                    } else {
                        // Table Header
                        PatientTableHeader()

                        Divider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Patient List
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 400.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            items(uiState.patients) { patient ->
                                PatientListItem(patient = patient)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

    }


}

@Composable
private fun PatientStatsCard(
    totalPatients: Int,
    filteredCount: Int?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
//                icon = Icons.Default.People,
                icon = Icons.Default.Person,
                label = if (filteredCount != null) "Filtered" else "Total Patients",
                value = filteredCount ?: totalPatients,
                color = MaterialTheme.colorScheme.secondary
            )

            if (filteredCount != null) {
                StatItem(
//                    icon = Icons.Default.PersonSearch,
                    icon = Icons.Default.Search,
                    label = "Total Registered",
                    value = totalPatients,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PatientTableHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Patient Name",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = "Age",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "BMI Status",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1.5f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PatientListItem(
    patient: PatientListItem
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Patient Name
        Column(
            modifier = Modifier.weight(2f)
        ) {
            Text(
                text = patient.fullName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "ID: ${patient.patientId}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        // Age
        Text(
            text = "${patient.age}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        // BMI Status
        BMIStatusChip(
            status = patient.bmiStatus,
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Composable
private fun BMIStatusChip(
    status: BmiStatus,
    modifier: Modifier = Modifier
) {
    val (color, backgroundColor) = when (status) {
        BmiStatus.UNDERWEIGHT -> Pair(
            Color(0xFF2196F3),
            Color(0xFF2196F3).copy(alpha = 0.15f)
        )

        BmiStatus.NORMAL -> Pair(
            Color(0xFF4CAF50),
            Color(0xFF4CAF50).copy(alpha = 0.15f)
        )

        BmiStatus.OVERWEIGHT -> Pair(
            Color(0xFFFF9800),
            Color(0xFFFF9800).copy(alpha = 0.15f)
        )
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        ) {
            Text(
                text = status.displayName,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = color,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun EmptyPatientState(
    isFiltered: Boolean,
    onAddPatient: () -> Unit,
    onClearFilter: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
//            if (isFiltered) Icons.Default.SearchOff else Icons.Default.PersonAdd,
            if (isFiltered) Icons.Default.Search else Icons.Default.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.size(64.dp)
        )

        Text(
            text = if (isFiltered) "No patients found" else "No patients registered",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Text(
            text = if (isFiltered)
                "No patients have visits on the selected date"
            else
                "Start by registering your first patient",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        if (isFiltered) {
            OutlinedButton(
                onClick = onClearFilter,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear Filter")
            }
        } else {
            Button(
                onClick = onAddPatient,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Icon(
//                    Icons.Default.PersonAdd,
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Register First Patient")
            }
        }
    }
}

// Helper Components
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
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
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
    ).apply {
        // Allow past and current dates for filtering
        datePicker.maxDate = System.currentTimeMillis()
    }.show()
}

// Extension for BmiStatus enum to have display names
private val BmiStatus.displayName: String
    get() = when (this) {
        BmiStatus.UNDERWEIGHT -> "Underweight"
        BmiStatus.NORMAL -> "Normal"
        BmiStatus.OVERWEIGHT -> "Overweight"
    }

@Composable
private fun PageHeaderWithLogout(
    title: String,
    subtitle: String,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
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
            }

            // Logout Button
            IconButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                        androidx.compose.foundation.shape.CircleShape
                    )
                    .size(48.dp)
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Logout",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to logout? You will need to sign in again to access the app.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancel")
                }
            },
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        )
    }
}