package com.moto.tracker.ui.feature.vehicle.addedit

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
import com.moto.tracker.domain.model.FuelType
import com.moto.tracker.domain.model.VehicleType
import com.moto.tracker.ui.components.MotoTopBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddEditVehicleScreen(
    vehicleId: Long?,
    onBack: () -> Unit,
    viewModel: AddEditVehicleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                AddEditVehicleEvent.NavigateBack -> onBack()
            }
        }
    }

    Scaffold(
        topBar = {
            MotoTopBar(
                title = if (uiState.isEditMode) "Edit Vehicle" else "Add Vehicle",
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

            // Nickname
            OutlinedTextField(
                value = uiState.nickname,
                onValueChange = viewModel::onNicknameChange,
                label = { Text("Nickname (optional)") },
                placeholder = { Text("e.g. My Pulsar, Red Beauty") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Make & Model row
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = uiState.make,
                    onValueChange = viewModel::onMakeChange,
                    label = { Text("Make *") },
                    placeholder = { Text("Bajaj, Hero...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = uiState.makeError != null,
                    supportingText = uiState.makeError?.let { { Text(it) } }
                )
                OutlinedTextField(
                    value = uiState.model,
                    onValueChange = viewModel::onModelChange,
                    label = { Text("Model *") },
                    placeholder = { Text("Pulsar 150...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = uiState.modelError != null,
                    supportingText = uiState.modelError?.let { { Text(it) } }
                )
            }

            // Year & Odometer row
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = uiState.year,
                    onValueChange = viewModel::onYearChange,
                    label = { Text("Year *") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = uiState.yearError != null,
                    supportingText = uiState.yearError?.let { { Text(it) } }
                )
                OutlinedTextField(
                    value = uiState.currentOdometer,
                    onValueChange = viewModel::onOdometerChange,
                    label = { Text("Odometer (km) *") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = uiState.odometerError != null,
                    supportingText = uiState.odometerError?.let { { Text(it) } }
                )
            }

            // Vehicle Type dropdown
            DropdownMenuField(
                label = "Vehicle Type",
                options = VehicleType.values().toList(),
                selectedOption = uiState.type,
                onOptionSelected = viewModel::onTypeChange,
                optionLabel = { it.displayName },
                modifier = Modifier.fillMaxWidth()
            )

            // Fuel Type dropdown
            DropdownMenuField(
                label = "Fuel Type",
                options = FuelType.values().toList(),
                selectedOption = uiState.fuelType,
                onOptionSelected = viewModel::onFuelTypeChange,
                optionLabel = { it.displayName },
                modifier = Modifier.fillMaxWidth()
            )

            // License Plate & Color row
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = uiState.licensePlate,
                    onValueChange = viewModel::onLicensePlateChange,
                    label = { Text("License Plate") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.color,
                    onValueChange = viewModel::onColorChange,
                    label = { Text("Color") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // VIN
            OutlinedTextField(
                value = uiState.vin,
                onValueChange = viewModel::onVinChange,
                label = { Text("VIN / Chassis Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = viewModel::onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (uiState.isEditMode) "Save Changes" else "Add Vehicle")
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> DropdownMenuField(
    label: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    optionLabel: (T) -> String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = optionLabel(selectedOption),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
