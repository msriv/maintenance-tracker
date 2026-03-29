package com.moto.tracker.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.google.firebase.firestore.FirebaseFirestore
import com.moto.tracker.data.local.dao.RegistryCacheDao
import com.moto.tracker.data.local.entity.BestPracticeEntity
import com.moto.tracker.data.local.entity.RegistryMakeEntity
import com.moto.tracker.data.local.entity.RegistryModelEntity
import com.moto.tracker.data.local.entity.RegistryScheduleEntity
import com.moto.tracker.data.local.mapper.toDomain
import com.moto.tracker.domain.model.BestPractice
import com.moto.tracker.domain.model.RegistryMake
import com.moto.tracker.domain.model.RegistryModel
import com.moto.tracker.domain.model.RegistrySchedule
import com.moto.tracker.domain.repository.RegistryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class RegistryRepositoryImpl @Inject constructor(
    private val cacheDao: RegistryCacheDao,
    private val firestore: FirebaseFirestore,
    private val dataStore: DataStore<Preferences>,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : RegistryRepository {

    private val lastSyncKey = longPreferencesKey("registry_last_sync")

    override fun observeMakes(): Flow<List<RegistryMake>> =
        cacheDao.observeMakes().map { list -> list.map { it.toDomain() } }

    override fun observeModelsByMake(make: String): Flow<List<RegistryModel>> =
        cacheDao.observeModelsByMake(make).map { list -> list.map { it.toDomain() } }

    override fun observeBestPracticeTemplates(): Flow<List<BestPractice>> =
        cacheDao.observeTemplates().map { list -> list.map { it.toDomain() } }

    override suspend fun getMakes(): List<RegistryMake> = withContext(ioDispatcher) {
        cacheDao.getMakes().map { it.toDomain() }
    }

    override suspend fun getModelsByMake(make: String): List<RegistryModel> = withContext(ioDispatcher) {
        cacheDao.getModelsByMake(make).map { it.toDomain() }
    }

    override suspend fun getSchedules(make: String, model: String, year: Int): List<RegistrySchedule> =
        withContext(ioDispatcher) {
            cacheDao.getSchedules(make, model, year).map { it.toDomain() }
        }

    override suspend fun syncFromFirestore(): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            // Fetch makes from flat Firestore collection "registry_makes"
            val makesSnapshot = firestore.collection("registry_makes").get().await()
            val makes = makesSnapshot.documents.mapNotNull { doc ->
                RegistryMakeEntity(
                    id = doc.id,
                    name = doc.getString("name") ?: return@mapNotNull null,
                    country = doc.getString("country"),
                    logoUrl = doc.getString("logoUrl"),
                    updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                )
            }
            if (makes.isNotEmpty()) {
                cacheDao.upsertMakes(makes)
            }

            // Fetch models from flat Firestore collection "registry_models"
            val modelsSnapshot = firestore.collection("registry_models").get().await()
            val models = modelsSnapshot.documents.mapNotNull { doc ->
                RegistryModelEntity(
                    id = doc.id,
                    make = doc.getString("make") ?: return@mapNotNull null,
                    model = doc.getString("model") ?: return@mapNotNull null,
                    yearFrom = doc.getLong("yearFrom")?.toInt() ?: return@mapNotNull null,
                    yearTo = doc.getLong("yearTo")?.toInt(),
                    variantsJson = doc.getString("variantsJson") ?: "[]",
                    vehicleType = doc.getString("vehicleType") ?: "BIKE",
                    displacementCc = doc.getLong("displacementCc")?.toInt(),
                    updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                )
            }
            if (models.isNotEmpty()) {
                cacheDao.upsertModels(models)
            }

            // Fetch schedules from flat Firestore collection "registry_schedules"
            val schedulesSnapshot = firestore.collection("registry_schedules").get().await()
            val schedules = schedulesSnapshot.documents.mapNotNull { doc ->
                RegistryScheduleEntity(
                    id = doc.id,
                    make = doc.getString("make") ?: return@mapNotNull null,
                    model = doc.getString("model") ?: return@mapNotNull null,
                    yearFrom = doc.getLong("yearFrom")?.toInt() ?: return@mapNotNull null,
                    yearTo = doc.getLong("yearTo")?.toInt(),
                    taskType = doc.getString("taskType") ?: return@mapNotNull null,
                    taskLabel = doc.getString("taskLabel") ?: return@mapNotNull null,
                    intervalDays = doc.getLong("intervalDays")?.toInt(),
                    intervalKm = doc.getLong("intervalKm")?.toInt(),
                    notes = doc.getString("notes"),
                    source = doc.getString("source") ?: "OEM",
                    updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                )
            }
            if (schedules.isNotEmpty()) {
                cacheDao.upsertSchedules(schedules)
            }

            // Fetch best practices from flat Firestore collection "best_practices"
            val bpSnapshot = firestore.collection("best_practices").get().await()
            val bestPractices = bpSnapshot.documents.mapNotNull { doc ->
                BestPracticeEntity(
                    id = doc.id,
                    category = doc.getString("category") ?: return@mapNotNull null,
                    taskType = doc.getString("taskType") ?: return@mapNotNull null,
                    title = doc.getString("title") ?: return@mapNotNull null,
                    description = doc.getString("description") ?: "",
                    applicableToJson = doc.getString("applicableToJson") ?: "[]",
                    isTemplate = doc.getBoolean("isTemplate") ?: false,
                    templateIntervalDays = doc.getLong("templateIntervalDays")?.toInt(),
                    templateIntervalKm = doc.getLong("templateIntervalKm")?.toInt(),
                    updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                )
            }
            if (bestPractices.isNotEmpty()) {
                cacheDao.upsertBestPractices(bestPractices)
            }

            // Store last sync timestamp
            dataStore.edit { it[lastSyncKey] = System.currentTimeMillis() }
            Unit
        }
    }

    override suspend fun getLastSyncTime(): Long? =
        dataStore.data.map { it[lastSyncKey] }.first()
}
