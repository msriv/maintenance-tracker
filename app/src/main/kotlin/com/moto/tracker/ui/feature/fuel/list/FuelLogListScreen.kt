package com.moto.tracker.ui.feature.fuel.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moto.tracker.domain.model.FuelLog
import com.moto.tracker.ui.components.EmptyState
import com.moto.tracker.ui.components.MotoTopBar
import com.moto.tracker.ui.theme.MotoColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FuelLogListScreen(
    vehicleId: Long,
    onBack: () -> Unit,
    onAddFuelLog: () -> Unit,
    viewModel: FuelLogListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = { MotoTopBar(title = "Fuel Log", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddFuelLog,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Fill-up")
            }
        }
    ) { innerPadding ->
        if (uiState.fuelLogs.isEmpty()) {
            EmptyState(
                title = "No fuel logs",
                message = "Log fill-ups to track your fuel efficiency and expenses.",
                actionLabel = "Add Fill-up",
                onAction = onAddFuelLog,
                modifier = Modifier.padding(innerPadding)
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Summary header
            item {
                FuelSummaryCard(
                    avgKmPerLiter = uiState.averageKmPerLiter,
                    totalCost = uiState.totalFuelCost
                )
                Spacer(Modifier.height(8.dp))
            }

            items(uiState.fuelLogs, key = { it.id }) { log ->
                FuelLogCard(
                    log = log,
                    dateFormat = dateFormat,
                    onDelete = { viewModel.onDeleteLog(log) }
                )
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun FuelSummaryCard(avgKmPerLiter: Double?, totalCost: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(listOf(MotoColors.FuelGradientStart, MotoColors.FuelGradientEnd)))
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            FuelStatItem("Avg Efficiency", avgKmPerLiter?.let { "%.1f km/L".format(it) } ?: "—")
            VerticalDivider(color = Color.White.copy(alpha = 0.3f), modifier = Modifier.height(40.dp))
            FuelStatItem("Total Spent", "₹%.0f".format(totalCost))
        }
    }
}

@Composable
private fun FuelStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.7f))
        Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
    }
}

@Composable
private fun FuelLogCard(log: FuelLog, dateFormat: SimpleDateFormat, onDelete: () -> Unit) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("₹%.0f".format(log.totalCost), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                    Text("${log.liters}L", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(dateFormat.format(Date(log.fillDate)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("%,d km".format(log.odometer), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            log.kmPerLiter?.let { kml ->
                Column(horizontalAlignment = Alignment.End) {
                    Text("%.1f".format(kml), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.tertiary)
                    Text("km/L", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.width(8.dp))
            }
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(0.7f))
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete fuel log?") },
            text = { Text("This fill-up record will be permanently deleted.") },
            confirmButton = {
                TextButton(
                    onClick = { onDelete(); showDeleteConfirm = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") } }
        )
    }
}
