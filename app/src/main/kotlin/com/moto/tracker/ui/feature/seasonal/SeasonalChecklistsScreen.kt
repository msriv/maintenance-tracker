package com.moto.tracker.ui.feature.seasonal

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moto.tracker.domain.model.Season
import com.moto.tracker.domain.model.SeasonalChecklist
import com.moto.tracker.ui.components.EmptyState
import com.moto.tracker.ui.components.MotoTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonalChecklistsScreen(
    onBack: () -> Unit,
    viewModel: SeasonalChecklistsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            MotoTopBar(title = "Seasonal Checklists", onBack = onBack)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Templates") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("My Checklists") }
                )
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else {
                when (selectedTab) {
                    0 -> TemplatesTab(
                        templates = uiState.templates,
                        onUseTemplate = viewModel::useTemplate
                    )
                    1 -> MyChecklistsTab(
                        checklists = uiState.myChecklists,
                        onDeleteChecklist = viewModel::deleteChecklist
                    )
                }
            }
        }
    }
}

@Composable
private fun TemplatesTab(
    templates: List<SeasonalChecklist>,
    onUseTemplate: (SeasonalChecklist) -> Unit
) {
    if (templates.isEmpty()) {
        EmptyState(
            title = "No Templates Available",
            message = "Seasonal templates will appear here once synced.",
            actionLabel = null,
            onAction = null
        )
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(templates, key = { it.id }) { template ->
            TemplateCard(template = template, onUseTemplate = { onUseTemplate(template) })
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun TemplateCard(
    template: SeasonalChecklist,
    onUseTemplate: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = template.name,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SeasonChip(season = template.season)
                        Text(
                            text = "${template.tasks.size} tasks",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }

            if (expanded && template.tasks.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    template.tasks.forEach { task ->
                        Text(
                            text = "• $task",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onUseTemplate,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Use Template")
            }
        }
    }
}

@Composable
private fun MyChecklistsTab(
    checklists: List<SeasonalChecklist>,
    onDeleteChecklist: (SeasonalChecklist) -> Unit
) {
    if (checklists.isEmpty()) {
        EmptyState(
            title = "No Checklists Yet",
            message = "No checklists yet. Use a template to get started.",
            actionLabel = null,
            onAction = null
        )
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(checklists, key = { it.id }) { checklist ->
            MyChecklistCard(
                checklist = checklist,
                onDelete = { onDeleteChecklist(checklist) }
            )
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun MyChecklistCard(
    checklist: SeasonalChecklist,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = checklist.name, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SeasonChip(season = checklist.season)
                        Text(
                            text = "${checklist.tasks.size} tasks",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (checklist.isCompleted) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text("Completed", style = MaterialTheme.typography.labelSmall) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = Color(0xFF388E3C),
                                    labelColor = Color.White
                                )
                            )
                        }
                    }
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
    }
}

@Composable
private fun SeasonChip(season: Season) {
    val (containerColor, labelColor) = when (season) {
        Season.SUMMER -> Pair(Color(0xFFFFA000), Color.White)
        Season.MONSOON -> Pair(Color(0xFF1976D2), Color.White)
        Season.WINTER -> Pair(Color(0xFF00838F), Color.White)
        Season.ALL -> Pair(Color(0xFF757575), Color.White)
    }
    SuggestionChip(
        onClick = {},
        label = { Text(season.displayName, style = MaterialTheme.typography.labelSmall) },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = containerColor,
            labelColor = labelColor
        )
    )
}
