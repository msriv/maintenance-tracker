package com.moto.tracker.ui.feature.maintenance.addedit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moto.tracker.domain.model.MaintenanceTaskType
import com.moto.tracker.ui.components.MotoTopBar
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMaintenanceTaskScreen(
    vehicleId: Long,
    taskId: Long?,
    onBack: () -> Unit,
    viewModel: AddEditMaintenanceTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                AddEditTaskEvent.NavigateBack -> onBack()
            }
        }
    }

    Scaffold(
        topBar = {
            MotoTopBar(
                title = if (uiState.isEditMode) "Edit Task" else "Add Maintenance Task",
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

            // Task type dropdown
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = uiState.taskType.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Task Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    MaintenanceTaskType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayName) },
                            onClick = {
                                viewModel.onTaskTypeChange(type)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Custom name if CUSTOM type
            if (uiState.taskType == MaintenanceTaskType.CUSTOM) {
                OutlinedTextField(
                    value = uiState.customName,
                    onValueChange = viewModel::onCustomNameChange,
                    label = { Text("Custom Task Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Text("Service Intervals", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = uiState.thresholdDays,
                    onValueChange = viewModel::onThresholdDaysChange,
                    label = { Text("Every N days") },
                    placeholder = { Text("e.g. 90") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.thresholdKm,
                    onValueChange = viewModel::onThresholdKmChange,
                    label = { Text("Every N km") },
                    placeholder = { Text("e.g. 3000") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            Text("Last Service (optional)", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            OutlinedTextField(
                value = uiState.lastPerformedOdometer,
                onValueChange = viewModel::onLastPerformedOdometerChange,
                label = { Text("Odometer at last service (km)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Notes") },
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
                    Text(if (uiState.isEditMode) "Save Changes" else "Add Task")
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
