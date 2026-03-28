package com.moto.tracker.ui.feature.fuel.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moto.tracker.ui.components.MotoTopBar
import com.moto.tracker.ui.theme.MotoColors
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFuelLogScreen(
    vehicleId: Long,
    onBack: () -> Unit,
    viewModel: AddFuelLogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.fillDate)
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                AddFuelLogEvent.NavigateBack -> onBack()
            }
        }
    }

    Scaffold(
        topBar = { MotoTopBar(title = "Log Fill-up", onBack = onBack) }
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

            // Cost preview card
            if (uiState.totalCost > 0) {
                FillupCostCard(totalCost = uiState.totalCost, liters = uiState.liters.toDoubleOrNull())
            }

            // Date picker trigger
            OutlinedTextField(
                value = dateFormat.format(Date(uiState.fillDate)),
                onValueChange = {},
                readOnly = true,
                label = { Text("Fill-up Date") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    TextButton(onClick = { showDatePicker = true }) { Text("Change") }
                }
            )

            OutlinedTextField(
                value = uiState.odometer,
                onValueChange = viewModel::onOdometerChange,
                label = { Text("Odometer at Fill-up (km) *") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = uiState.odometerError != null,
                supportingText = uiState.odometerError?.let { { Text(it) } }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = uiState.liters,
                    onValueChange = viewModel::onLitersChange,
                    label = { Text("Liters *") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = uiState.litersError != null,
                    supportingText = uiState.litersError?.let { { Text(it) } }
                )
                OutlinedTextField(
                    value = uiState.pricePerLiter,
                    onValueChange = viewModel::onPricePerLiterChange,
                    label = { Text("₹ / Liter *") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = uiState.priceError != null,
                    supportingText = uiState.priceError?.let { { Text(it) } }
                )
            }

            OutlinedTextField(
                value = uiState.stationName,
                onValueChange = viewModel::onStationNameChange,
                label = { Text("Fuel Station (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Full tank toggle
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Full Tank", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            if (uiState.isTankFull) "km/L will be computed for this fill-up"
                            else "Partial fill — km/L not computed",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.isTankFull,
                        onCheckedChange = viewModel::onIsTankFullChange
                    )
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
                    Icon(Icons.Default.LocalGasStation, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Save Fill-up")
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
                    datePickerState.selectedDateMillis?.let { viewModel.onFillDateChange(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun FillupCostCard(totalCost: Double, liters: Double?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(listOf(MotoColors.FuelGradientStart, MotoColors.FuelGradientEnd)))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total Cost", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.75f))
                Text(
                    "₹%.0f".format(totalCost),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
            if (liters != null && liters > 0) {
                VerticalDivider(color = Color.White.copy(0.3f), modifier = Modifier.height(36.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Liters", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.75f))
                    Text(
                        "%.2fL".format(liters),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                }
            }
        }
    }
}
