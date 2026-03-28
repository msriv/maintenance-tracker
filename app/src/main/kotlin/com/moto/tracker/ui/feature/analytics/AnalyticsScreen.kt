package com.moto.tracker.ui.feature.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moto.tracker.domain.model.CategoryExpense
import com.moto.tracker.domain.model.MonthlyExpense
import com.moto.tracker.ui.theme.MotoColors
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.shape.Shape
import java.util.Calendar

private val MONTH_LABELS = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
private val CATEGORY_COLORS = mapOf(
    "fuel" to MotoColors.Secondary40,
    "service" to MotoColors.Primary40,
    "insurance" to MotoColors.FuelGradientEnd,
    "other" to Color(0xFF78909C)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val yearRange = (currentYear - 3..currentYear).toList()
    var showVehicleDropdown by remember { mutableStateOf(false) }
    var showYearDropdown by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Expense Analytics",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        // Filters row
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Vehicle filter
                ExposedDropdownMenuBox(
                    expanded = showVehicleDropdown,
                    onExpandedChange = { showVehicleDropdown = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = uiState.vehicles.find { it.id == uiState.selectedVehicleId }?.displayName ?: "All Vehicles",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vehicle") },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showVehicleDropdown) }
                    )
                    ExposedDropdownMenu(
                        expanded = showVehicleDropdown,
                        onDismissRequest = { showVehicleDropdown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Vehicles") },
                            onClick = { viewModel.onVehicleSelected(null); showVehicleDropdown = false }
                        )
                        uiState.vehicles.forEach { vehicle ->
                            DropdownMenuItem(
                                text = { Text(vehicle.displayName) },
                                onClick = { viewModel.onVehicleSelected(vehicle.id); showVehicleDropdown = false }
                            )
                        }
                    }
                }

                // Year filter
                ExposedDropdownMenuBox(
                    expanded = showYearDropdown,
                    onExpandedChange = { showYearDropdown = it },
                    modifier = Modifier.width(120.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.selectedYear.toString(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Year") },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showYearDropdown) }
                    )
                    ExposedDropdownMenu(
                        expanded = showYearDropdown,
                        onDismissRequest = { showYearDropdown = false }
                    ) {
                        yearRange.reversed().forEach { year ->
                            DropdownMenuItem(
                                text = { Text(year.toString()) },
                                onClick = { viewModel.onYearSelected(year); showYearDropdown = false }
                            )
                        }
                    }
                }
            }
        }

        // Total summary card
        if (!uiState.isLoading && uiState.totalAmount > 0) {
            item {
                TotalExpenseCard(total = uiState.totalAmount, year = uiState.selectedYear)
            }
        }

        // Monthly bar chart
        if (!uiState.isLoading && uiState.monthlyTrend.isNotEmpty()) {
            item {
                MonthlyExpenseChart(months = uiState.monthlyTrend)
            }
        }

        // Category breakdown
        if (!uiState.isLoading && uiState.categoryTotals.isNotEmpty()) {
            item {
                CategoryBreakdownCard(categories = uiState.categoryTotals)
            }
        }

        if (!uiState.isLoading && uiState.totalAmount == 0.0) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No expenses recorded", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Log fuel fill-ups, maintenance, and documents to see your expense analytics.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun TotalExpenseCard(total: Double, year: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Total Expenses $year", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f))
                Text("₹%.0f".format(total), style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

@Composable
private fun MonthlyExpenseChart(months: List<MonthlyExpense>) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(months) {
        modelProducer.runTransaction {
            val amounts = (1..12).map { month ->
                months.find { it.month == month }?.amount?.toFloat() ?: 0f
            }
            columnSeries { series(amounts) }
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Monthly Breakdown", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
            Spacer(Modifier.height(12.dp))
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                            rememberLineComponent(
                                color = MotoColors.Primary40,
                                thickness = 16.dp,
                                shape = Shape.rounded(allPercent = 50)
                            )
                        )
                    ),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(
                        valueFormatter = { _, x, _ -> MONTH_LABELS.getOrElse(x.toInt()) { "" } },
                        itemPlacer = AxisItemPlacer.Horizontal.default(addExtremeLabelPadding = true)
                    )
                ),
                modelProducer = modelProducer,
                modifier = Modifier.fillMaxWidth().height(180.dp)
            )
        }
    }
}

@Composable
private fun CategoryBreakdownCard(categories: List<CategoryExpense>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("By Category", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
            categories.forEach { expense ->
                CategoryRow(expense)
            }
        }
    }
}

@Composable
private fun CategoryRow(expense: CategoryExpense) {
    val categoryKey = expense.category.lowercase()
    val color = CATEGORY_COLORS[categoryKey] ?: Color(0xFF78909C)
    val label = expense.category.replaceFirstChar { it.uppercase() }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(color)
                )
                Text(label, style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                "₹%.0f".format(expense.amount),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
        LinearProgressIndicator(
            progress = { expense.percentage / 100f },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
        Text(
            "%.1f%%".format(expense.percentage),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
