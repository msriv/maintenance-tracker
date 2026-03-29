package com.moto.tracker

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.moto.tracker.ui.feature.analytics.AnalyticsScreen
import com.moto.tracker.ui.feature.appointment.addedit.AddEditAppointmentScreen
import com.moto.tracker.ui.feature.appointment.list.AppointmentListScreen
import com.moto.tracker.ui.feature.document.add.AddDocumentScreen
import com.moto.tracker.ui.feature.document.detail.DocumentDetailScreen
import com.moto.tracker.ui.feature.document.list.DocumentListScreen
import com.moto.tracker.ui.feature.export.ExportScreen
import com.moto.tracker.ui.feature.fuel.add.AddFuelLogScreen
import com.moto.tracker.ui.feature.fuel.list.FuelLogListScreen
import com.moto.tracker.ui.feature.maintenance.addedit.AddEditMaintenanceTaskScreen
import com.moto.tracker.ui.feature.maintenance.kmlog.KmLogScreen
import com.moto.tracker.ui.feature.maintenance.list.MaintenanceListScreen
import com.moto.tracker.ui.feature.maintenance.log.LogMaintenanceScreen
import com.moto.tracker.ui.feature.maintenance.schedule.ManufacturerScheduleScreen
import com.moto.tracker.ui.feature.settings.SettingsScreen
import com.moto.tracker.ui.feature.vehicle.addedit.AddEditVehicleScreen
import com.moto.tracker.ui.feature.vehicle.detail.VehicleDetailScreen
import com.moto.tracker.ui.feature.vehicle.list.VehicleListScreen
import com.moto.tracker.ui.feature.recall.RecallAlertsScreen
import com.moto.tracker.ui.feature.maintenance.template.TemplateApplyScreen
import com.moto.tracker.ui.feature.parts.PartsInventoryScreen
import com.moto.tracker.ui.feature.parts.addedit.AddEditPartScreen
import com.moto.tracker.ui.feature.seasonal.SeasonalChecklistsScreen
import com.moto.tracker.ui.feature.servicecenter.ServiceCenterDirectoryScreen
import com.moto.tracker.ui.feature.servicecenter.addedit.AddEditServiceCenterScreen
import com.moto.tracker.ui.feature.ridelog.RideLogListScreen
import com.moto.tracker.ui.feature.ridelog.add.AddRideLogScreen
import com.moto.tracker.ui.feature.backup.CloudBackupScreen
import com.moto.tracker.ui.feature.registry.VehicleRegistryScreen
import kotlinx.serialization.Serializable

// — Type-safe navigation destinations —

@Serializable object VehicleListDestination
@Serializable data class AddEditVehicleDestination(val vehicleId: Long = -1L)
@Serializable data class VehicleDetailDestination(val vehicleId: Long)
@Serializable data class DocumentListDestination(val vehicleId: Long)
@Serializable data class AddDocumentDestination(val vehicleId: Long)
@Serializable data class DocumentDetailDestination(val documentId: Long)
@Serializable data class MaintenanceListDestination(val vehicleId: Long)
@Serializable data class AddEditMaintenanceTaskDestination(val vehicleId: Long, val taskId: Long = -1L)
@Serializable data class LogMaintenanceDestination(val taskId: Long)
@Serializable data class KmLogDestination(val vehicleId: Long)
@Serializable object ManufacturerScheduleDestination
@Serializable data class FuelLogListDestination(val vehicleId: Long)
@Serializable data class AddFuelLogDestination(val vehicleId: Long)
@Serializable data class AppointmentListDestination(val vehicleId: Long)
@Serializable data class AddEditAppointmentDestination(val vehicleId: Long, val appointmentId: Long = -1L)
@Serializable object AnalyticsDestination
@Serializable object ExportDestination
@Serializable object SettingsDestination
@Serializable data class RecallAlertsDestination(val vehicleId: Long)
@Serializable data class TemplateApplyDestination(val vehicleId: Long)
@Serializable data class PartsInventoryDestination(val vehicleId: Long)
@Serializable data class AddEditPartDestination(val vehicleId: Long, val partId: Long = -1L)
@Serializable object SeasonalChecklistsDestination
@Serializable object ServiceCenterDirectoryDestination
@Serializable data class AddEditServiceCenterDestination(val centerId: Long = -1L)
@Serializable data class RideLogListDestination(val vehicleId: Long)
@Serializable data class AddRideLogDestination(val vehicleId: Long)
@Serializable object CloudBackupDestination
@Serializable object VehicleRegistryDestination

private data class BottomNavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val destination: Any
)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem("Garage", Icons.Default.DirectionsCar, VehicleListDestination),
        BottomNavItem("Analytics", Icons.Default.BarChart, AnalyticsDestination),
        BottomNavItem("Export", Icons.Default.FileDownload, ExportDestination),
        BottomNavItem("Settings", Icons.Default.Settings, SettingsDestination),
        BottomNavItem("Registry", Icons.Default.Search, VehicleRegistryDestination),
    )

    val showBottomBar = currentDestination?.hierarchy?.any { dest ->
        bottomNavItems.any { dest.route == it.destination::class.qualifiedName }
    } == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { dest ->
                            dest.route == item.destination::class.qualifiedName
                        } == true
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.destination) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = VehicleListDestination,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 3 }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it / 3 }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
        ) {
            // — Vehicle —
            composable<VehicleListDestination> {
                VehicleListScreen(
                    onAddVehicle = { navController.navigate(AddEditVehicleDestination()) },
                    onVehicleClick = { navController.navigate(VehicleDetailDestination(it)) }
                )
            }
            composable<AddEditVehicleDestination> {
                val dest = it.toRoute<AddEditVehicleDestination>()
                AddEditVehicleScreen(
                    vehicleId = dest.vehicleId.takeIf { id -> id != -1L },
                    onBack = { navController.popBackStack() }
                )
            }
            composable<VehicleDetailDestination> {
                val dest = it.toRoute<VehicleDetailDestination>()
                VehicleDetailScreen(
                    vehicleId = dest.vehicleId,
                    onBack = { navController.popBackStack() },
                    onEditVehicle = { navController.navigate(AddEditVehicleDestination(dest.vehicleId)) },
                    onNavigateDocuments = { navController.navigate(DocumentListDestination(dest.vehicleId)) },
                    onNavigateMaintenance = { navController.navigate(MaintenanceListDestination(dest.vehicleId)) },
                    onNavigateFuel = { navController.navigate(FuelLogListDestination(dest.vehicleId)) },
                    onNavigateAppointments = { navController.navigate(AppointmentListDestination(dest.vehicleId)) },
                    onNavigateRecalls = { navController.navigate(RecallAlertsDestination(dest.vehicleId)) },
                    onNavigateParts = { navController.navigate(PartsInventoryDestination(dest.vehicleId)) },
                    onNavigateRideLog = { navController.navigate(RideLogListDestination(dest.vehicleId)) }
                )
            }

            // — Documents —
            composable<DocumentListDestination> {
                val dest = it.toRoute<DocumentListDestination>()
                DocumentListScreen(
                    vehicleId = dest.vehicleId,
                    onBack = { navController.popBackStack() },
                    onAddDocument = { navController.navigate(AddDocumentDestination(dest.vehicleId)) },
                    onDocumentClick = { docId -> navController.navigate(DocumentDetailDestination(docId)) }
                )
            }
            composable<AddDocumentDestination> {
                val dest = it.toRoute<AddDocumentDestination>()
                AddDocumentScreen(
                    vehicleId = dest.vehicleId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable<DocumentDetailDestination> {
                val dest = it.toRoute<DocumentDetailDestination>()
                DocumentDetailScreen(
                    documentId = dest.documentId,
                    onBack = { navController.popBackStack() }
                )
            }

            // — Maintenance —
            composable<MaintenanceListDestination> {
                val dest = it.toRoute<MaintenanceListDestination>()
                MaintenanceListScreen(
                    vehicleId = dest.vehicleId,
                    onBack = { navController.popBackStack() },
                    onAddTask = { navController.navigate(AddEditMaintenanceTaskDestination(dest.vehicleId)) },
                    onLogMaintenance = { taskId -> navController.navigate(LogMaintenanceDestination(taskId)) },
                    onEditTask = { taskId -> navController.navigate(AddEditMaintenanceTaskDestination(dest.vehicleId, taskId)) },
                    onKmLog = { navController.navigate(KmLogDestination(dest.vehicleId)) },
                    onManufacturerSchedule = { navController.navigate(ManufacturerScheduleDestination) },
                    onApplyTemplate = { navController.navigate(TemplateApplyDestination(dest.vehicleId)) }
                )
            }
            composable<AddEditMaintenanceTaskDestination> {
                val dest = it.toRoute<AddEditMaintenanceTaskDestination>()
                AddEditMaintenanceTaskScreen(
                    vehicleId = dest.vehicleId,
                    taskId = dest.taskId.takeIf { id -> id != -1L },
                    onBack = { navController.popBackStack() }
                )
            }
            composable<LogMaintenanceDestination> {
                val dest = it.toRoute<LogMaintenanceDestination>()
                LogMaintenanceScreen(
                    taskId = dest.taskId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable<KmLogDestination> {
                val dest = it.toRoute<KmLogDestination>()
                KmLogScreen(
                    vehicleId = dest.vehicleId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable<ManufacturerScheduleDestination> {
                ManufacturerScheduleScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // — Fuel —
            composable<FuelLogListDestination> {
                val dest = it.toRoute<FuelLogListDestination>()
                FuelLogListScreen(
                    vehicleId = dest.vehicleId,
                    onBack = { navController.popBackStack() },
                    onAddFuelLog = { navController.navigate(AddFuelLogDestination(dest.vehicleId)) }
                )
            }
            composable<AddFuelLogDestination> {
                val dest = it.toRoute<AddFuelLogDestination>()
                AddFuelLogScreen(
                    vehicleId = dest.vehicleId,
                    onBack = { navController.popBackStack() }
                )
            }

            // — Appointments —
            composable<AppointmentListDestination> {
                val dest = it.toRoute<AppointmentListDestination>()
                AppointmentListScreen(
                    vehicleId = dest.vehicleId,
                    onBack = { navController.popBackStack() },
                    onAddAppointment = { navController.navigate(AddEditAppointmentDestination(dest.vehicleId)) },
                    onEditAppointment = { apptId -> navController.navigate(AddEditAppointmentDestination(dest.vehicleId, apptId)) }
                )
            }
            composable<AddEditAppointmentDestination> {
                val dest = it.toRoute<AddEditAppointmentDestination>()
                AddEditAppointmentScreen(
                    vehicleId = dest.vehicleId,
                    appointmentId = dest.appointmentId.takeIf { id -> id != -1L },
                    onBack = { navController.popBackStack() }
                )
            }

            // — New Feature Routes —
            composable<RecallAlertsDestination> {
                val dest = it.toRoute<RecallAlertsDestination>()
                RecallAlertsScreen(vehicleId = dest.vehicleId, onBack = { navController.popBackStack() })
            }
            composable<TemplateApplyDestination> {
                val dest = it.toRoute<TemplateApplyDestination>()
                TemplateApplyScreen(vehicleId = dest.vehicleId, onBack = { navController.popBackStack() })
            }
            composable<PartsInventoryDestination> {
                val dest = it.toRoute<PartsInventoryDestination>()
                PartsInventoryScreen(
                    vehicleId = dest.vehicleId,
                    onBack = { navController.popBackStack() },
                    onAddPart = { navController.navigate(AddEditPartDestination(dest.vehicleId)) },
                    onEditPart = { partId -> navController.navigate(AddEditPartDestination(dest.vehicleId, partId)) }
                )
            }
            composable<AddEditPartDestination> {
                val dest = it.toRoute<AddEditPartDestination>()
                AddEditPartScreen(
                    vehicleId = dest.vehicleId,
                    partId = dest.partId.takeIf { id -> id != -1L },
                    onBack = { navController.popBackStack() }
                )
            }
            composable<SeasonalChecklistsDestination> {
                SeasonalChecklistsScreen(onBack = { navController.popBackStack() })
            }
            composable<ServiceCenterDirectoryDestination> {
                ServiceCenterDirectoryScreen(
                    onBack = { navController.popBackStack() },
                    onAddCenter = { navController.navigate(AddEditServiceCenterDestination()) },
                    onEditCenter = { centerId -> navController.navigate(AddEditServiceCenterDestination(centerId)) }
                )
            }
            composable<AddEditServiceCenterDestination> {
                val dest = it.toRoute<AddEditServiceCenterDestination>()
                AddEditServiceCenterScreen(
                    centerId = dest.centerId.takeIf { id -> id != -1L },
                    onBack = { navController.popBackStack() }
                )
            }
            composable<RideLogListDestination> {
                val dest = it.toRoute<RideLogListDestination>()
                RideLogListScreen(
                    vehicleId = dest.vehicleId,
                    onBack = { navController.popBackStack() },
                    onAddRide = { navController.navigate(AddRideLogDestination(dest.vehicleId)) }
                )
            }
            composable<AddRideLogDestination> {
                val dest = it.toRoute<AddRideLogDestination>()
                AddRideLogScreen(vehicleId = dest.vehicleId, onBack = { navController.popBackStack() })
            }
            composable<CloudBackupDestination> {
                CloudBackupScreen(onBack = { navController.popBackStack() })
            }
            composable<VehicleRegistryDestination> {
                VehicleRegistryScreen(onBack = { navController.popBackStack() })
            }

            // — Bottom nav roots —
            composable<AnalyticsDestination> {
                AnalyticsScreen()
            }
            composable<ExportDestination> {
                ExportScreen()
            }
            composable<SettingsDestination> {
                SettingsScreen(
                    onNavigateBackup = { navController.navigate(CloudBackupDestination) }
                )
            }
        }
    }
}
