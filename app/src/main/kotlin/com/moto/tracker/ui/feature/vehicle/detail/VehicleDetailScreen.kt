package com.moto.tracker.ui.feature.vehicle.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moto.tracker.ui.components.MotoTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailScreen(
    vehicleId: Long,
    onBack: () -> Unit,
    onEditVehicle: () -> Unit,
    onNavigateDocuments: () -> Unit,
    onNavigateMaintenance: () -> Unit,
    onNavigateFuel: () -> Unit,
    onNavigateAppointments: () -> Unit,
    viewModel: VehicleDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val vehicle = uiState.vehicle

    Scaffold(
        topBar = {
            MotoTopBar(
                title = vehicle?.displayName ?: "Vehicle",
                onBack = onBack,
                actions = {
                    IconButton(onClick = onEditVehicle) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (vehicle == null) {
            Box(Modifier.padding(innerPadding).fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Vehicle info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    InfoItem(label = "Odometer", value = "%,d km".format(vehicle.currentOdometer))
                    InfoItem(label = "Year", value = vehicle.year.toString())
                    InfoItem(label = "Fuel", value = vehicle.fuelType.displayName)
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Manage", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))

            // Navigation tiles
            val tiles = listOf(
                NavTile("Documents", Icons.Default.Description, onNavigateDocuments),
                NavTile("Maintenance", Icons.Default.Build, onNavigateMaintenance),
                NavTile("Fuel Log", Icons.Default.LocalGasStation, onNavigateFuel),
                NavTile("Appointments", Icons.Default.CalendarToday, onNavigateAppointments)
            )

            tiles.forEach { tile ->
                FilledTonalButton(
                    onClick = tile.onClick,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Icon(tile.icon, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text(tile.label, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
        Text(value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

private data class NavTile(val label: String, val icon: ImageVector, val onClick: () -> Unit)
