package com.moto.tracker.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.moto.tracker.domain.model.MileagePrediction

@Composable
fun MileagePredictionCard(
    prediction: MileagePrediction,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "  Mileage Insights",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Avg: ${"%.0f".format(prediction.avgKmPerWeek)} km/week (${"%,.0f".format(prediction.avgKmPerMonth)} km/month)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            prediction.daysUntilService?.let { days ->
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Next service due in ~$days days  ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val (chipColor, chipTextColor) = when {
                        days <= 7 -> Pair(Color(0xFFB00020), Color.White)
                        days <= 30 -> Pair(Color(0xFFE65100), Color.White)
                        else -> Pair(Color(0xFF388E3C), Color.White)
                    }
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                text = "$days days",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = chipColor,
                            labelColor = chipTextColor
                        )
                    )
                }
            }

            prediction.projectedNextServiceOdometer?.let { odometer ->
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "At ~${"%,d".format(odometer)} km",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Prediction confidence",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { prediction.confidenceScore },
                modifier = Modifier.fillMaxWidth(),
                color = when {
                    prediction.confidenceScore >= 0.7f -> Color(0xFF388E3C)
                    prediction.confidenceScore >= 0.4f -> Color(0xFFE65100)
                    else -> MaterialTheme.colorScheme.error
                }
            )
            Text(
                text = "${"%.0f".format(prediction.confidenceScore * 100)}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
