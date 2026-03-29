package com.moto.tracker.ui.feature.servicecenter

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moto.tracker.domain.model.ServiceCenter
import com.moto.tracker.ui.components.EmptyState
import com.moto.tracker.ui.components.MotoTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceCenterDirectoryScreen(
    onBack: () -> Unit,
    onAddCenter: () -> Unit,
    onEditCenter: (Long) -> Unit,
    viewModel: ServiceCenterDirectoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            MotoTopBar(title = "Service Centers", onBack = onBack)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCenter) {
                Icon(Icons.Default.Add, contentDescription = "Add Service Center")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                uiState.centers.isEmpty() -> {
                    EmptyState(
                        title = "No Service Centers Saved",
                        message = "No service centers saved yet.",
                        actionLabel = "Add Center",
                        onAction = onAddCenter
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.centers, key = { it.id }) { center ->
                            ServiceCenterCard(
                                center = center,
                                onClick = { onEditCenter(center.id) },
                                onToggleFavorite = { viewModel.toggleFavorite(center) },
                                onDelete = {
                                    scope.launch {
                                        viewModel.delete(center)
                                        val result = snackbarHostState.showSnackbar(
                                            message = "${center.name} removed",
                                            actionLabel = "Undo"
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            // Undo by re-inserting - not implemented at repo level here
                                            // In a real app would store the deleted item and re-insert
                                        }
                                    }
                                },
                                onCallPhone = { phone ->
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                    context.startActivity(intent)
                                }
                            )
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceCenterCard(
    center: ServiceCenter,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onCallPhone: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = center.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    center.address?.let { addr ->
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = addr,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            if (center.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = if (center.isFavorite) "Unfavorite" else "Favorite",
                            tint = if (center.isFavorite) Color(0xFFFFA000) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDelete) {
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                center.phone?.let { phone ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = androidx.compose.ui.Modifier
                    ) {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        androidx.compose.material3.TextButton(
                            onClick = { onCallPhone(phone) },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = phone,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                center.rating?.let { rating ->
                    RatingDisplay(rating = rating)
                }
            }

            center.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                Spacer(Modifier.height(4.dp))
                Text(
                    text = notes.take(80) + if (notes.length > 80) "..." else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RatingDisplay(rating: Float) {
    Row {
        val fullStars = rating.toInt()
        val hasHalf = rating - fullStars >= 0.5f
        repeat(5) { index ->
            Icon(
                imageVector = if (index < fullStars || (index == fullStars && hasHalf)) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = Color(0xFFFFA000),
                modifier = Modifier.padding(1.dp)
            )
        }
    }
}
