package com.moto.tracker.ui.feature.maintenance.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moto.tracker.domain.model.DuenessStatus
import com.moto.tracker.domain.model.MaintenanceTask
import com.moto.tracker.ui.components.DuenessStatusBadge
import com.moto.tracker.ui.components.EmptyState
import com.moto.tracker.ui.components.MotoTopBar
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceListScreen(
    vehicleId: Long,
    onBack: () -> Unit,
    onAddTask: () -> Unit,
    onLogMaintenance: (Long) -> Unit,
    onEditTask: (Long) -> Unit,
    onKmLog: () -> Unit,
    onManufacturerSchedule: () -> Unit,
    viewModel: MaintenanceListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MotoTopBar(
                title = "Maintenance",
                onBack = onBack,
                actions = {
                    IconButton(onClick = onKmLog) {
                        Icon(Icons.Default.Speed, contentDescription = "Log Km")
                    }
                    IconButton(onClick = onManufacturerSchedule) {
                        Icon(Icons.Default.MenuBook, contentDescription = "Service Schedule")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTask,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(Modifier.padding(innerPadding).fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            return@Scaffold
        }

        val allTasks = uiState.overdueTasks + uiState.dueSoonTasks + uiState.upcomingTasks
        if (allTasks.isEmpty()) {
            EmptyState(
                title = "No maintenance tasks",
                message = "Add maintenance tasks to track engine oil, air filter, and more.",
                actionLabel = "Add Task",
                onAction = onAddTask,
                modifier = Modifier.padding(innerPadding)
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (uiState.overdueTasks.isNotEmpty()) {
                item { SectionHeader("Overdue", uiState.overdueTasks.size) }
                items(uiState.overdueTasks, key = { it.id }) { task ->
                    MaintenanceTaskCard(
                        task = task,
                        currentOdometer = uiState.vehicle?.currentOdometer ?: 0,
                        onLogClick = { onLogMaintenance(task.id) },
                        onEditClick = { onEditTask(task.id) },
                        onDeactivate = { viewModel.onDeactivateTask(task) }
                    )
                }
            }
            if (uiState.dueSoonTasks.isNotEmpty()) {
                item { SectionHeader("Due Soon", uiState.dueSoonTasks.size) }
                items(uiState.dueSoonTasks, key = { it.id }) { task ->
                    MaintenanceTaskCard(
                        task = task,
                        currentOdometer = uiState.vehicle?.currentOdometer ?: 0,
                        onLogClick = { onLogMaintenance(task.id) },
                        onEditClick = { onEditTask(task.id) },
                        onDeactivate = { viewModel.onDeactivateTask(task) }
                    )
                }
            }
            if (uiState.upcomingTasks.isNotEmpty()) {
                item { SectionHeader("Upcoming", uiState.upcomingTasks.size) }
                items(uiState.upcomingTasks, key = { it.id }) { task ->
                    MaintenanceTaskCard(
                        task = task,
                        currentOdometer = uiState.vehicle?.currentOdometer ?: 0,
                        onLogClick = { onLogMaintenance(task.id) },
                        onEditClick = { onEditTask(task.id) },
                        onDeactivate = { viewModel.onDeactivateTask(task) }
                    )
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
            Text("$count", color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

@Composable
private fun MaintenanceTaskCard(
    task: MaintenanceTask,
    currentOdometer: Int,
    onLogClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeactivate: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val status = task.duenessStatus(currentOdometer)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (status) {
                DuenessStatus.OVERDUE -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                DuenessStatus.DUE_SOON -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                DuenessStatus.OK -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(task.displayName, style = MaterialTheme.typography.titleSmall)
                DuenessStatusBadge(status)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                task.nextDueDate?.let {
                    InfoChip("Due: ${dateFormat.format(Date(it))}", Icons.Default.CalendarToday)
                }
                task.nextDueOdometer?.let {
                    InfoChip("Due at: %,d km".format(it), Icons.Default.Speed)
                }
            }
            if (task.lastPerformedDate != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Last done: ${dateFormat.format(Date(task.lastPerformedDate))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onEditClick, modifier = Modifier.weight(1f)) {
                    Text("Edit")
                }
                Button(onClick = onLogClick, modifier = Modifier.weight(1f)) {
                    Text("Log Service")
                }
            }
        }
    }
}

@Composable
private fun InfoChip(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
