package com.moto.tracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.moto.tracker.domain.model.DuenessStatus
import com.moto.tracker.domain.model.ExpiryStatus
import com.moto.tracker.ui.theme.MotoColors

@Composable
fun DuenessStatusBadge(status: DuenessStatus, modifier: Modifier = Modifier) {
    val (label, bg, fg) = when (status) {
        DuenessStatus.OVERDUE -> Triple("Overdue", MotoColors.Overdue, Color.White)
        DuenessStatus.DUE_SOON -> Triple("Due Soon", MotoColors.DueSoon, Color.White)
        DuenessStatus.OK -> Triple("OK", MotoColors.Ok, Color.White)
    }
    StatusBadge(label, bg, fg, modifier)
}

@Composable
fun ExpiryStatusBadge(status: ExpiryStatus, modifier: Modifier = Modifier) {
    when (status) {
        ExpiryStatus.NO_EXPIRY -> return
        ExpiryStatus.VALID -> StatusBadge("Valid", MotoColors.Ok, Color.White, modifier)
        ExpiryStatus.EXPIRING_SOON -> StatusBadge("Expiring Soon", MotoColors.DueSoon, Color.White, modifier)
        ExpiryStatus.EXPIRED -> StatusBadge("Expired", MotoColors.Overdue, Color.White, modifier)
    }
}

@Composable
private fun StatusBadge(
    label: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = textColor,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}
