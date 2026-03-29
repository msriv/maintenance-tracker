package com.moto.tracker.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.moto.tracker.data.local.dao.DocumentDao
import com.moto.tracker.data.local.dao.FuelLogDao
import com.moto.tracker.data.local.dao.KmLogDao
import com.moto.tracker.data.local.dao.MaintenanceLogDao
import com.moto.tracker.data.local.dao.MaintenanceTaskDao
import com.moto.tracker.data.local.dao.ServiceAppointmentDao
import com.moto.tracker.data.local.dao.VehicleDao
import com.moto.tracker.data.local.entity.DocumentEntity
import com.moto.tracker.data.local.entity.FuelLogEntity
import com.moto.tracker.data.local.entity.KmLogEntity
import com.moto.tracker.data.local.entity.MaintenanceLogEntity
import com.moto.tracker.data.local.entity.MaintenanceTaskEntity
import com.moto.tracker.data.local.entity.ServiceAppointmentEntity
import com.moto.tracker.data.local.entity.VehicleEntity
import com.moto.tracker.domain.model.BackupStatus
import com.moto.tracker.domain.repository.CloudBackupRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class CloudBackupRepositoryImpl @Inject constructor(
    private val vehicleDao: VehicleDao,
    private val maintenanceTaskDao: MaintenanceTaskDao,
    private val maintenanceLogDao: MaintenanceLogDao,
    private val fuelLogDao: FuelLogDao,
    private val kmLogDao: KmLogDao,
    private val appointmentDao: ServiceAppointmentDao,
    private val documentDao: DocumentDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : CloudBackupRepository {

    private val _status = MutableStateFlow(BackupStatus())

    override fun observeBackupStatus(): Flow<BackupStatus> = _status.asStateFlow()

    override suspend fun backup(): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            _status.value = BackupStatus(isBackingUp = true)
            val deviceId = ensureSignedIn()
            val backupRoot = firestore.collection("backups").document(deviceId)

            val vehicles = vehicleDao.getAll()
            vehicles.forEach { vehicle ->
                backupRoot.collection("vehicles")
                    .document(vehicle.id.toString())
                    .set(vehicle.toMap())
                    .await()
            }

            val tasks = maintenanceTaskDao.getAllActive()
            tasks.forEach { task ->
                backupRoot.collection("maintenance_tasks")
                    .document(task.id.toString())
                    .set(task.toMap())
                    .await()
            }

            val kmLogs = vehicles.flatMap { v -> kmLogDao.getRecentByVehicle(v.id, 365) }
            kmLogs.forEach { log ->
                backupRoot.collection("km_logs")
                    .document(log.id.toString())
                    .set(log.toMap())
                    .await()
            }

            val fuelLogs = vehicles.flatMap { v ->
                fuelLogDao.getByVehicleAndDateRange(v.id, 0L, Long.MAX_VALUE)
            }
            fuelLogs.forEach { log ->
                backupRoot.collection("fuel_logs")
                    .document(log.id.toString())
                    .set(log.toMap())
                    .await()
            }

            val now = System.currentTimeMillis()
            _status.value = BackupStatus(lastBackupAt = now, isBackingUp = false)
        }.onFailure { error ->
            _status.value = BackupStatus(isBackingUp = false, errorMessage = error.message)
        }
    }

    override suspend fun restore(): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            _status.value = BackupStatus(isRestoring = true)
            val deviceId = ensureSignedIn()
            val backupRoot = firestore.collection("backups").document(deviceId)

            // Fetch and restore vehicles
            val vehiclesSnapshot = backupRoot.collection("vehicles").get().await()
            vehiclesSnapshot.documents.forEach { doc ->
                val entity = doc.toVehicleEntity() ?: return@forEach
                vehicleDao.insert(entity)
            }

            // Fetch and restore maintenance tasks
            val tasksSnapshot = backupRoot.collection("maintenance_tasks").get().await()
            tasksSnapshot.documents.forEach { doc ->
                val entity = doc.toMaintenanceTaskEntity() ?: return@forEach
                maintenanceTaskDao.insert(entity)
            }

            // Fetch and restore km logs
            val kmLogsSnapshot = backupRoot.collection("km_logs").get().await()
            kmLogsSnapshot.documents.forEach { doc ->
                val entity = doc.toKmLogEntity() ?: return@forEach
                kmLogDao.insert(entity)
            }

            // Fetch and restore fuel logs
            val fuelLogsSnapshot = backupRoot.collection("fuel_logs").get().await()
            fuelLogsSnapshot.documents.forEach { doc ->
                val entity = doc.toFuelLogEntity() ?: return@forEach
                fuelLogDao.insert(entity)
            }

            _status.value = BackupStatus(isRestoring = false)
        }.onFailure { error ->
            _status.value = BackupStatus(isRestoring = false, errorMessage = error.message)
        }
    }

    private suspend fun ensureSignedIn(): String {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            currentUser.uid
        } else {
            val result = auth.signInAnonymously().await()
            result.user?.uid ?: error("Failed to sign in anonymously")
        }
    }

    // Extension functions to convert entities to maps for Firestore
    private fun VehicleEntity.toMap(): Map<String, Any?> = mapOf(
        "id" to id, "nickname" to nickname, "make" to make, "model" to model,
        "year" to year, "type" to type, "vin" to vin, "licensePlate" to licensePlate,
        "currentOdometer" to currentOdometer, "fuelType" to fuelType, "color" to color,
        "purchaseDate" to purchaseDate, "imageUri" to imageUri,
        "createdAt" to createdAt, "updatedAt" to updatedAt
    )

    private fun MaintenanceTaskEntity.toMap(): Map<String, Any?> = mapOf(
        "id" to id, "vehicleId" to vehicleId, "taskType" to taskType,
        "customName" to customName, "thresholdDays" to thresholdDays,
        "thresholdKm" to thresholdKm, "lastPerformedDate" to lastPerformedDate,
        "lastPerformedOdometer" to lastPerformedOdometer, "nextDueDate" to nextDueDate,
        "nextDueOdometer" to nextDueOdometer, "isActive" to isActive, "notes" to notes,
        "createdAt" to createdAt, "updatedAt" to updatedAt
    )

    private fun KmLogEntity.toMap(): Map<String, Any?> = mapOf(
        "id" to id, "vehicleId" to vehicleId, "logDate" to logDate,
        "odometer" to odometer, "kmDriven" to kmDriven, "notes" to notes,
        "createdAt" to createdAt
    )

    private fun FuelLogEntity.toMap(): Map<String, Any?> = mapOf(
        "id" to id, "vehicleId" to vehicleId, "fillDate" to fillDate,
        "odometer" to odometer, "liters" to liters, "pricePerLiter" to pricePerLiter,
        "totalCost" to totalCost, "fuelType" to fuelType, "stationName" to stationName,
        "isTankFull" to isTankFull, "kmPerLiter" to kmPerLiter,
        "notes" to notes, "createdAt" to createdAt
    )

    // Extension functions to parse Firestore documents back to entities
    private fun com.google.firebase.firestore.DocumentSnapshot.toVehicleEntity(): VehicleEntity? {
        return try {
            VehicleEntity(
                id = getLong("id") ?: return null,
                nickname = getString("nickname") ?: return null,
                make = getString("make") ?: return null,
                model = getString("model") ?: return null,
                year = getLong("year")?.toInt() ?: return null,
                type = getString("type") ?: return null,
                vin = getString("vin"),
                licensePlate = getString("licensePlate"),
                currentOdometer = getLong("currentOdometer")?.toInt() ?: return null,
                fuelType = getString("fuelType") ?: return null,
                color = getString("color"),
                purchaseDate = getLong("purchaseDate"),
                imageUri = getString("imageUri"),
                createdAt = getLong("createdAt") ?: return null,
                updatedAt = getLong("updatedAt") ?: return null
            )
        } catch (e: Exception) { null }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toMaintenanceTaskEntity(): MaintenanceTaskEntity? {
        return try {
            MaintenanceTaskEntity(
                id = getLong("id") ?: return null,
                vehicleId = getLong("vehicleId") ?: return null,
                taskType = getString("taskType") ?: return null,
                customName = getString("customName"),
                thresholdDays = getLong("thresholdDays")?.toInt(),
                thresholdKm = getLong("thresholdKm")?.toInt(),
                lastPerformedDate = getLong("lastPerformedDate"),
                lastPerformedOdometer = getLong("lastPerformedOdometer")?.toInt(),
                nextDueDate = getLong("nextDueDate"),
                nextDueOdometer = getLong("nextDueOdometer")?.toInt(),
                isActive = getBoolean("isActive") ?: true,
                notes = getString("notes"),
                createdAt = getLong("createdAt") ?: return null,
                updatedAt = getLong("updatedAt") ?: return null
            )
        } catch (e: Exception) { null }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toKmLogEntity(): KmLogEntity? {
        return try {
            KmLogEntity(
                id = getLong("id") ?: return null,
                vehicleId = getLong("vehicleId") ?: return null,
                logDate = getLong("logDate") ?: return null,
                odometer = getLong("odometer")?.toInt() ?: return null,
                kmDriven = getLong("kmDriven")?.toInt() ?: return null,
                notes = getString("notes"),
                createdAt = getLong("createdAt") ?: return null
            )
        } catch (e: Exception) { null }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toFuelLogEntity(): FuelLogEntity? {
        return try {
            FuelLogEntity(
                id = getLong("id") ?: return null,
                vehicleId = getLong("vehicleId") ?: return null,
                fillDate = getLong("fillDate") ?: return null,
                odometer = getLong("odometer")?.toInt() ?: return null,
                liters = getDouble("liters") ?: return null,
                pricePerLiter = getDouble("pricePerLiter") ?: 0.0,
                totalCost = getDouble("totalCost") ?: 0.0,
                fuelType = getString("fuelType") ?: "PETROL",
                stationName = getString("stationName"),
                isTankFull = getBoolean("isTankFull") ?: false,
                kmPerLiter = getDouble("kmPerLiter"),
                notes = getString("notes"),
                createdAt = getLong("createdAt") ?: return null
            )
        } catch (e: Exception) { null }
    }
}
