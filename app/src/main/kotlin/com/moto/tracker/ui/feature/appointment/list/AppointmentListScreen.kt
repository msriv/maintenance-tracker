package com.moto.tracker.ui.feature.appointment.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moto.tracker.domain.model.AppointmentStatus
import com.moto.tracker.domain.model.ServiceAppointment
import com.moto.tracker.ui.components.EmptyState
import com.moto.tracker.ui.components.MotoTopBar
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AppointmentListScreen(
    vehicleId: Long,
    onBack: () -> Unit,
    onAddAppointment: () -> Unit,
    onEditAppointment: (Long) -> Unit,
    viewModel: AppointmentListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    Scaffold(
        topBar = { MotoTopBar(title = "Appointments", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAppointment,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Appointment")
            }
        }
    ) { innerPadding ->
        if (uiState.upcoming.isEmpty() && uiState.past.isEmpty()) {
            EmptyState(
                title = "No appointments",
                message = "Schedule service appointments and get reminders on time.",
                actionLabel = "Add Appointment",
                onAction = onAddAppointment,
                modifier = Modifier.padding(innerPadding)
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (uiState.upcoming.isNotEmpty()) {
                item {
                    SectionHeader("Upcoming")
                }
                items(uiState.upcoming, key = { it.id }) { appointment ->
                    AppointmentCard(
                        appointment = appointment,
                        dateFormat = dateFormat,
                        onEdit = { onEditAppointment(appointment.id) },
                        onMarkCompleted = { viewModel.onMarkCompleted(appointment) },
                        onDelete = { viewModel.onDeleteAppointment(appointment) }
                    )
                }
            }

            if (uiState.past.isNotEmpty()) {
                item {
                    SectionHeader(
                        "Past",
                        modifier = Modifier.padding(top = if (uiState.upcoming.isNotEmpty()) 8.dp else 0.dp)
                    )
                }
                items(uiState.past, key = { it.id }) { appointment ->
                    AppointmentCard(
                        appointment = appointment,
                        dateFormat = dateFormat,
                        onEdit = { onEditAppointment(appointment.id) },
                        onMarkCompleted = null,
                        onDelete = { viewModel.onDeleteAppointment(appointment) }
                    )
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun AppointmentCard(
    appointment: ServiceAppointment,
    dateFormat: SimpleDateFormat,
    onEdit: () -> Unit,
    onMarkCompleted: (() -> Unit)?,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val containerColor = when (appointment.status) {
        AppointmentStatus.COMPLETED -> MaterialTheme.colorScheme.surfaceVariant
        AppointmentStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        AppointmentStatus.UPCOMING -> MaterialTheme.colorScheme.surface
    }

    Card(
        onClick = onEdit,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = when (appointment.status) {
                    AppointmentStatus.UPCOMING -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(36.dp).padding(end = 4.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        appointment.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    AppointmentStatusChip(appointment.status)
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    dateFormat.format(Date(appointment.appointmentDate)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                appointment.serviceCenterName?.let { center ->
                    Text(
                        center,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                appointment.estimatedCost?.let { cost ->
                    Text(
                        "Est. ₹%.0f".format(cost),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (onMarkCompleted != null && appointment.status == AppointmentStatus.UPCOMING) {
                IconButton(onClick = onMarkCompleted) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Mark completed",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(
                    Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(0.7f)
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete appointment?") },
            text = { Text("This appointment record will be permanently deleted.") },
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

@Composable
private fun AppointmentStatusChip(status: AppointmentStatus) {
    val (label, color) = when (status) {
        AppointmentStatus.UPCOMING -> "Upcoming" to MaterialTheme.colorScheme.secondary
        AppointmentStatus.COMPLETED -> "Done" to MaterialTheme.colorScheme.tertiary
        AppointmentStatus.CANCELLED -> "Cancelled" to MaterialTheme.colorScheme.error
    }
    SuggestionChip(
        onClick = {},
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = color.copy(alpha = 0.12f),
            labelColor = color
        ),
        modifier = Modifier.height(24.dp)
    )
}
