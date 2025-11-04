package com.bright.patientcareapp.ui.vitals

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bright.patientcareapp.util.DateUtils
import java.util.*

/**
 * Vitals Screen (PDF Page 3)
 * Fields: Visit Date, Height, Weight, BMI (auto-calculated)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalsScreen(
    patientId: String,
    onNavigateToAssessment: (Boolean) -> Unit,  // true = General, false = Overweight
    viewModel: VitalsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Patient Vitals") },
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
                text = "Patient ID: $patientId",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Visit Date
            OutlinedTextField(
                value = DateUtils.formatForDisplay(uiState.visitDate),
                onValueChange = {},
                label = { Text("Visit Date *") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showDatePicker(
                            context = context,
                            initialDate = uiState.visitDate,
                            onDateSelected = viewModel::onVisitDateChange
                        )
                    }
            )

            // Height (in cm)
            OutlinedTextField(
                value = uiState.heightCm,
                onValueChange = viewModel::onHeightChange,
                label = { Text("Height (cm) *") },
                isError = uiState.heightError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            if (uiState.heightError != null) {
                Text(
                    text = uiState.heightError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Weight (in kg)
            OutlinedTextField(
                value = uiState.weightKg,
                onValueChange = viewModel::onWeightChange,
                label = { Text("Weight (kg) *") },
                isError = uiState.weightError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            if (uiState.weightError != null) {
                Text(
                    text = uiState.weightError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // BMI (Auto-calculated, read-only)
            OutlinedTextField(
                value = uiState.bmi,
                onValueChange = {},
                label = { Text("BMI") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    Text("Auto-calculated: Weight(kg) / Height(m)²")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Save Button
            Button(
                onClick = {
                    viewModel.saveVitals { shouldShowGeneral ->
                        onNavigateToAssessment(shouldShowGeneral)
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
                    Text("Save & Continue to Assessment")
                }
            }
        }
    }
}

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