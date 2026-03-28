package com.moto.tracker.ui.feature.export

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    viewModel: ExportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showVehicleDropdown by remember { mutableStateOf(false) }
    var showFromDatePicker by remember { mutableStateOf(false) }
    var showToDatePicker by remember { mutableStateOf(false) }
    val fromDatePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.fromDate)
    val toDatePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.toDate)
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ExportEvent.ShareFile -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = event.mimeType
                        putExtra(Intent.EXTRA_STREAM, event.uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share Report"))
                }
                is ExportEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                "Export Report",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                "Generate a maintenance history or fuel log report to share or archive.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Vehicle picker
            ExposedDropdownMenuBox(
                expanded = showVehicleDropdown,
                onExpandedChange = { showVehicleDropdown = it }
            ) {
                OutlinedTextField(
                    value = uiState.vehicles.find { it.id == uiState.selectedVehicleId }?.displayName ?: "Select vehicle",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Vehicle *") },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showVehicleDropdown) }
                )
                ExposedDropdownMenu(
                    expanded = showVehicleDropdown,
                    onDismissRequest = { showVehicleDropdown = false }
                ) {
                    uiState.vehicles.forEach { vehicle ->
                        DropdownMenuItem(
                            text = { Text(vehicle.displayName) },
                            onClick = { viewModel.onVehicleSelected(vehicle.id); showVehicleDropdown = false }
                        )
                    }
                }
            }

            // Date range
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = dateFormat.format(Date(uiState.fromDate)),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("From") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = { TextButton(onClick = { showFromDatePicker = true }) { Text("Pick") } }
                )
                OutlinedTextField(
                    value = dateFormat.format(Date(uiState.toDate)),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("To") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = { TextButton(onClick = { showToDatePicker = true }) { Text("Pick") } }
                )
            }

            // Format selection
            Text("Format", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FormatCard(
                    label = "PDF",
                    description = "Rich report with layout",
                    icon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(28.dp)) },
                    selected = uiState.selectedFormat == ExportFormat.PDF,
                    onClick = { viewModel.onFormatSelected(ExportFormat.PDF) },
                    modifier = Modifier.weight(1f)
                )
                FormatCard(
                    label = "CSV",
                    description = "Spreadsheet-compatible",
                    icon = { Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(28.dp)) },
                    selected = uiState.selectedFormat == ExportFormat.CSV,
                    onClick = { viewModel.onFormatSelected(ExportFormat.CSV) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = viewModel::onExport,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isExporting && uiState.selectedVehicleId != null
            ) {
                if (uiState.isExporting) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Export & Share")
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    if (showFromDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showFromDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    fromDatePickerState.selectedDateMillis?.let { viewModel.onFromDateChange(it) }
                    showFromDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showFromDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = fromDatePickerState) }
    }

    if (showToDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showToDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    toDatePickerState.selectedDateMillis?.let { viewModel.onToDateChange(it) }
                    showToDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showToDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = toDatePickerState) }
    }
}

@Composable
private fun FormatCard(
    label: String,
    description: String,
    icon: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surfaceVariant

    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            icon()
            Text(label, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
            Text(description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
