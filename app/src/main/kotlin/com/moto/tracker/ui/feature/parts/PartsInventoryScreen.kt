package com.moto.tracker.ui.feature.parts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moto.tracker.domain.model.PartsInventory
import com.moto.tracker.ui.components.EmptyState
import com.moto.tracker.ui.components.MotoTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartsInventoryScreen(
    vehicleId: Long,
    onBack: () -> Unit,
    onAddPart: () -> Unit,
    onEditPart: (Long) -> Unit,
    viewModel: PartsInventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var partToDelete by remember { mutableStateOf<PartsInventory?>(null) }

    val totalValue = uiState.parts.sumOf { it.totalValue ?: 0.0 }
    val lowStockCount = uiState.parts.count { it.isLowStock }

    Scaffold(
        topBar = {
            MotoTopBar(
                title = "Parts Inventory",
                onBack = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPart) {
                Icon(Icons.Default.Add, contentDescription = "Add Part")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.parts.isEmpty() -> {
                    EmptyState(
                        title = "No Parts Tracked Yet",
                        message = "No parts tracked yet. Tap + to add a spare part.",
                        actionLabel = "Add Part",
                        onAction = onAddPart
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SuggestionChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                            "${uiState.parts.size} items | Total: ₹${"%.2f".format(totalValue)}",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                )
                            }
                        }

                        if (lowStockCount > 0) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                        Text(
                                            text = "  $lowStockCount part(s) running low on stock",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }

                        items(uiState.parts, key = { it.id }) { part ->
                            PartCard(
                                part = part,
                                onClick = { onEditPart(part.id) },
                                onDeleteClick = { partToDelete = part }
                            )
                        }

                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }

    partToDelete?.let { part ->
        AlertDialog(
            onDismissRequest = { partToDelete = null },
            title = { Text("Delete ${part.partName}?") },
            text = { Text("This part will be permanently removed from your inventory.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePart(part)
                        partToDelete = null
                    },
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { partToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun PartCard(
    part: PartsInventory,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = part.partName,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    part.partNumber?.let { pn ->
                        Text(
                            text = "Part #: $pn",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (part.isLowStock) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text("Low Stock", style = MaterialTheme.typography.labelSmall) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                labelColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        text = "Qty",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${part.quantity} / ${part.minQuantity} min",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                part.unitCost?.let { cost ->
                    Column {
                        Text(
                            text = "Unit Cost",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${"%.2f".format(cost)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                part.totalValue?.let { tv ->
                    Column {
                        Text(
                            text = "Total Value",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${"%.2f".format(tv)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
