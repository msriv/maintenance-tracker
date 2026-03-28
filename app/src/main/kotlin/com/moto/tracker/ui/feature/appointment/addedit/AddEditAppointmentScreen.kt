package com.moto.tracker.ui.feature.appointment.addedit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moto.tracker.ui.components.MotoTopBar
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

private val REMINDER_OPTIONS = listOf(
    30 to "30 minutes before",
    60 to "1 hour before",
    180 to "3 hours before",
    360 to "6 hours before",
    720 to "12 hours before",
    1440 to "1 day before",
    2880 to "2 days before"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAppointmentScreen(
    vehicleId: Long,
    appointmentId: Long?,
    onBack: () -> Unit,
    viewModel: AddEditAppointmentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    var showReminderDropdown by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.appointmentDate)
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                AddEditAppointmentEvent.NavigateBack -> onBack()
            }
        }
    }

    Scaffold(
        topBar = {
            MotoTopBar(
                title = if (uiState.isEditMode) "Edit Appointment" else "New Appointment",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Title *") },
                placeholder = { Text("e.g., Annual Service, Oil Change") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.titleError != null,
                supportingText = uiState.titleError?.let { { Text(it) } }
            )

            OutlinedTextField(
                value = dateFormat.format(Date(uiState.appointmentDate)),
                onValueChange = {},
                readOnly = true,
                label = { Text("Appointment Date") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                trailingIcon = {
                    TextButton(onClick = { showDatePicker = true }) { Text("Pick") }
                }
            )

            OutlinedTextField(
                value = uiState.serviceCenterName,
                onValueChange = viewModel::onServiceCenterNameChange,
                label = { Text("Service Center (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.serviceCenterPhone,
                onValueChange = viewModel::onServiceCenterPhoneChange,
                label = { Text("Phone Number (optional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.estimatedCost,
                onValueChange = viewModel::onEstimatedCostChange,
                label = { Text("Estimated Cost ₹ (optional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            // Reminder card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Reminder", style = MaterialTheme.typography.titleSmall)
                        Switch(
                            checked = uiState.reminderEnabled,
                            onCheckedChange = viewModel::onReminderEnabledChange
                        )
                    }

                    if (uiState.reminderEnabled) {
                        Spacer(Modifier.height(8.dp))
                        ExposedDropdownMenuBox(
                            expanded = showReminderDropdown,
                            onExpandedChange = { showReminderDropdown = it }
                        ) {
                            OutlinedTextField(
                                value = REMINDER_OPTIONS.find { it.first == uiState.reminderMinutesBefore }?.second
                                    ?: "1 day before",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Remind me") },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showReminderDropdown) }
                            )
                            ExposedDropdownMenu(
                                expanded = showReminderDropdown,
                                onDismissRequest = { showReminderDropdown = false }
                            ) {
                                REMINDER_OPTIONS.forEach { (minutes, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            viewModel.onReminderMinutesBeforeChange(minutes)
                                            showReminderDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = viewModel::onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(if (uiState.isEditMode) "Update Appointment" else "Schedule Appointment")
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.onAppointmentDateChange(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
