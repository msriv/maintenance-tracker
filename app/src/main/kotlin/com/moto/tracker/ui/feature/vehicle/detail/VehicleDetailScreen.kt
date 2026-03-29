package com.moto.tracker.ui.feature.vehicle.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moto.tracker.ui.component.MileagePredictionCard
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
    onNavigateRecalls: () -> Unit = {},
    onNavigateParts: () -> Unit = {},
    onNavigateRideLog: () -> Unit = {},
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
                .verticalScroll(rememberScrollState())
        ) {
            // Vehicle info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    InfoItem(label = "Odometer", value = "%,d km".format(vehicle.currentOdometer))
                    InfoItem(label = "Year", value = vehicle.year.toString())
                    InfoItem(label = "Fuel", value = vehicle.fuelType.displayName)
                }
            }

            uiState.mileagePrediction?.let { prediction ->
                Spacer(Modifier.height(16.dp))
                MileagePredictionCard(prediction = prediction)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Manage",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            // Navigation tiles
            val tiles = listOf(
                NavTile("Documents", Icons.Default.Description, onNavigateDocuments),
                NavTile("Maintenance", Icons.Default.Build, onNavigateMaintenance),
                NavTile("Fuel Log", Icons.Default.LocalGasStation, onNavigateFuel),
                NavTile("Appointments", Icons.Default.CalendarToday, onNavigateAppointments),
                NavTile("Recalls", Icons.Default.Notifications, onNavigateRecalls),
                NavTile("Parts Inventory", Icons.Default.Settings, onNavigateParts),
                NavTile("Ride Log", Icons.Default.DirectionsRun, onNavigateRideLog)
            )

            tiles.forEach { tile ->
                FilledTonalButton(
                    onClick = tile.onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Icon(tile.icon, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text(tile.label, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }

            Spacer(Modifier.height(80.dp))
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
