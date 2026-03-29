package com.moto.tracker.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.moto.tracker.domain.model.Document
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun DocumentExpiryCard(
    expiringDocuments: List<Document>,
    modifier: Modifier = Modifier
) {
    val now = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (expiringDocuments.any {
                    it.expiryDate != null && it.expiryDate - now < TimeUnit.DAYS.toMillis(7)
                }) MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (expiringDocuments.isNotEmpty())
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "  Document Expiry Alerts",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(12.dp))

            if (expiringDocuments.isEmpty()) {
                Text(
                    text = "No expiring documents",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                expiringDocuments.forEachIndexed { index, document ->
                    if (index > 0) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                    DocumentExpiryItem(document = document, now = now, dateFormat = dateFormat)
                }
            }
        }
    }
}

@Composable
private fun DocumentExpiryItem(
    document: Document,
    now: Long,
    dateFormat: SimpleDateFormat
) {
    val expiryDate = document.expiryDate ?: return
    val daysRemaining = TimeUnit.MILLISECONDS.toDays(expiryDate - now).toInt()

    val daysColor = when {
        daysRemaining < 0 -> Color(0xFFB00020)
        daysRemaining < 7 -> Color(0xFFB00020)
        daysRemaining < 30 -> Color(0xFFE65100)
        else -> Color(0xFF388E3C)
    }

    Column {
        Text(
            text = document.title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Expires: ${dateFormat.format(Date(expiryDate))}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = when {
                daysRemaining < 0 -> "Expired ${-daysRemaining} days ago"
                daysRemaining == 0 -> "Expires today"
                else -> "$daysRemaining days remaining"
            },
            style = MaterialTheme.typography.labelSmall,
            color = daysColor
        )
    }
}
