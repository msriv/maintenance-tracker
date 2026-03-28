package com.moto.tracker.ui.feature.vehicle.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.moto.tracker.domain.model.Vehicle
import com.moto.tracker.domain.model.VehicleType
import com.moto.tracker.ui.components.EmptyState
import com.moto.tracker.ui.theme.MotoColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleListScreen(
    onAddVehicle: () -> Unit,
    onVehicleClick: (Long) -> Unit,
    viewModel: VehicleListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var vehicleToDelete by remember { mutableStateOf<Vehicle?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "MotoTracker",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddVehicle,
                icon = { Icon(Icons.Default.Add, "Add Vehicle") },
                text = { Text("Add Vehicle") },
                containerColor = MaterialTheme.colorScheme.secondary
            )
        }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                uiState.vehicles.isEmpty() -> {
                    EmptyState(
                        title = "No vehicles yet",
                        message = "Add your first vehicle to start tracking maintenance, fuel, and documents.",
                        actionLabel = "Add Vehicle",
                        onAction = onAddVehicle
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.vehicles, key = { it.id }) { vehicle ->
                            VehicleCard(
                                vehicle = vehicle,
                                onClick = { onVehicleClick(vehicle.id) },
                                onDeleteClick = { vehicleToDelete = vehicle }
                            )
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }

        // Delete confirmation
        vehicleToDelete?.let { vehicle ->
            AlertDialog(
                onDismissRequest = { vehicleToDelete = null },
                title = { Text("Delete ${vehicle.displayName}?") },
                text = { Text("This will permanently delete the vehicle and all its data including maintenance history, documents, and fuel logs.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.onDeleteVehicle(vehicle)
                            vehicleToDelete = null
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { vehicleToDelete = null }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
private fun VehicleCard(
    vehicle: Vehicle,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientColors = when (vehicle.type) {
        VehicleType.BIKE, VehicleType.SCOOTER -> listOf(MotoColors.CardGradientStart, MotoColors.CardGradientEnd)
        VehicleType.CAR, VehicleType.TRUCK -> listOf(MotoColors.Primary20, MotoColors.Primary40)
        else -> listOf(MotoColors.CardGradientStart, MotoColors.CardGradientEnd)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(gradientColors))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vehicle.displayName,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${vehicle.year} • ${vehicle.type.displayName} • ${vehicle.fuelType.displayName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    if (vehicle.licensePlate != null) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = vehicle.licensePlate,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
                IconButton(
                    onClick = onDeleteClick,
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White.copy(alpha = 0.7f))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                VehicleStatItem(
                    label = "Odometer",
                    value = "${"%,d".format(vehicle.currentOdometer)} km"
                )
                VehicleStatItem(
                    label = "Make",
                    value = vehicle.make
                )
                VehicleStatItem(
                    label = "Model",
                    value = vehicle.model
                )
            }
        }

        // Vehicle type icon
        Icon(
            imageVector = when (vehicle.type) {
                VehicleType.CAR, VehicleType.TRUCK -> Icons.Default.DirectionsCar
                else -> Icons.Default.TwoWheeler
            },
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.08f),
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 20.dp)
        )
    }
}

@Composable
private fun VehicleStatItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White
        )
    }
}
