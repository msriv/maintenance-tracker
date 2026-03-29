package com.moto.tracker.ui.widget

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import com.moto.tracker.MainActivity
import com.moto.tracker.domain.repository.MaintenanceRepository
import com.moto.tracker.domain.repository.VehicleRepository
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color as ComposeColor

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun vehicleRepository(): VehicleRepository
    fun maintenanceRepository(): MaintenanceRepository
}

class MaintenanceStatusWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val vehicleRepo = entryPoint.vehicleRepository()
        val maintenanceRepo = entryPoint.maintenanceRepository()

        val vehicles = vehicleRepo.getAll()
        val vehicleCount = vehicles.size
        val overdueTasks = maintenanceRepo.getOverdueTasks(System.currentTimeMillis())
        val overdueCount = overdueTasks.size
        val nextDueTask = overdueTasks.minByOrNull { it.nextDueDate ?: Long.MAX_VALUE }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .background(ColorProvider(ComposeColor(0xFF1A1A2E)))
                        .padding(16.dp)
                        .clickable(actionStartActivity(launchIntent))
                ) {
                    Text(
                        text = "MotoTracker",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(ComposeColor(0xFF6C63FF))
                        )
                    )

                    Spacer(GlanceModifier.height(8.dp))

                    if (vehicleCount == 0) {
                        Text(
                            text = "Open app to add your first vehicle",
                            style = TextStyle(color = ColorProvider(ComposeColor.White.copy(alpha = 0.7f)))
                        )
                    } else {
                        Text(
                            text = "$vehicleCount vehicle${if (vehicleCount != 1) "s" else ""}",
                            style = TextStyle(color = ColorProvider(ComposeColor.White.copy(alpha = 0.8f)))
                        )

                        Spacer(GlanceModifier.height(4.dp))

                        if (overdueCount > 0) {
                            Text(
                                text = "$overdueCount overdue task${if (overdueCount != 1) "s" else ""}",
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = ColorProvider(ComposeColor(0xFFFF6B6B))
                                )
                            )
                            nextDueTask?.let { task ->
                                Spacer(GlanceModifier.height(2.dp))
                                Text(
                                    text = task.displayName,
                                    style = TextStyle(
                                        color = ColorProvider(ComposeColor.White.copy(alpha = 0.7f))
                                    )
                                )
                            }
                        } else {
                            Text(
                                text = "All up to date",
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = ColorProvider(ComposeColor(0xFF4CAF50))
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

class MaintenanceStatusWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MaintenanceStatusWidget()
}
