package com.moto.tracker.ui.feature.document.list

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moto.tracker.domain.model.Document
import com.moto.tracker.domain.model.DocumentType
import com.moto.tracker.domain.model.ExpiryStatus
import com.moto.tracker.ui.components.EmptyState
import com.moto.tracker.ui.components.ExpiryStatusBadge
import com.moto.tracker.ui.components.MotoTopBar
import com.moto.tracker.ui.theme.MotoColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DocumentListScreen(
    vehicleId: Long,
    onBack: () -> Unit,
    onAddDocument: () -> Unit,
    onDocumentClick: (Long) -> Unit,
    viewModel: DocumentListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var documentToDelete by remember { mutableStateOf<Document?>(null) }

    Scaffold(
        topBar = { MotoTopBar(title = "Documents", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddDocument,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Document")
            }
        }
    ) { innerPadding ->
        if (uiState.groupedDocuments.isEmpty()) {
            EmptyState(
                title = "No documents",
                message = "Store RC, insurance, license, and service bills safely.",
                actionLabel = "Add Document",
                onAction = onAddDocument,
                modifier = Modifier.padding(innerPadding)
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uiState.groupedDocuments.forEach { (type, documents) ->
                item {
                    Text(
                        type.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }
                items(documents, key = { it.id }) { document ->
                    DocumentCard(
                        document = document,
                        onClick = { onDocumentClick(document.id) },
                        onDeleteClick = { documentToDelete = document }
                    )
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    documentToDelete?.let { doc ->
        AlertDialog(
            onDismissRequest = { documentToDelete = null },
            title = { Text("Delete ${doc.title}?") },
            text = { Text("This will permanently delete this document.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onDeleteDocument(doc)
                        documentToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { documentToDelete = null }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun DocumentCard(
    document: Document,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val gradientColors = when (document.type) {
        DocumentType.INSURANCE, DocumentType.RC -> listOf(MotoColors.DocGradientStart, MotoColors.DocGradientEnd)
        DocumentType.LICENSE, DocumentType.PUC -> listOf(MotoColors.FuelGradientStart, MotoColors.FuelGradientEnd)
        else -> listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primaryContainer)
    }
    val isGradient = document.type in listOf(DocumentType.INSURANCE, DocumentType.RC, DocumentType.LICENSE, DocumentType.PUC)

    val cardModifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .then(
            if (isGradient) Modifier.background(Brush.horizontalGradient(gradientColors))
            else Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
        )
        .clickable(onClick = onClick)

    Box(cardModifier) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        document.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isGradient) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    document.expiryDate?.let { expiry ->
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "Expires: ${dateFormat.format(Date(expiry))}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isGradient) Color.White.copy(0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ExpiryStatusBadge(document.expiryStatus)
                    Spacer(Modifier.width(4.dp))
                    IconButton(
                        onClick = onDeleteClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = if (isGradient) Color.White.copy(0.7f) else MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp))
                    }
                }
            }
            document.amount?.let { amount ->
                Spacer(Modifier.height(4.dp))
                Text(
                    "₹%.0f".format(amount),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isGradient) Color.White.copy(0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (document.fileUri != null) {
            Icon(
                Icons.Default.Image,
                contentDescription = null,
                tint = if (isGradient) Color.White.copy(0.08f) else MaterialTheme.colorScheme.onSurface.copy(0.05f),
                modifier = Modifier.size(80.dp).align(Alignment.CenterEnd).offset(x = 16.dp)
            )
        }
    }
}
