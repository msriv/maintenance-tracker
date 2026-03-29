package com.moto.tracker.data.repository

import com.moto.tracker.data.local.dao.RecallAlertDao
import com.moto.tracker.data.local.entity.RecallAlertEntity
import com.moto.tracker.data.local.mapper.toDomain
import com.moto.tracker.domain.model.RecallAlert
import com.moto.tracker.domain.model.RecallSeverity
import com.moto.tracker.domain.repository.RecallRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Named

class RecallRepositoryImpl @Inject constructor(
    private val dao: RecallAlertDao,
    private val okHttpClient: OkHttpClient,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : RecallRepository {

    private val nhtsaJson = Json { ignoreUnknownKeys = true }

    override fun observeByVehicle(vehicleId: Long): Flow<List<RecallAlert>> =
        dao.observeByVehicle(vehicleId).map { list -> list.map { it.toDomain() } }

    override fun observeUnacknowledged(vehicleId: Long): Flow<List<RecallAlert>> =
        dao.observeUnacknowledgedByVehicle(vehicleId).map { list -> list.map { it.toDomain() } }

    override fun observeUnacknowledgedCount(vehicleId: Long): Flow<Int> =
        dao.observeUnacknowledgedCount(vehicleId)

    override suspend fun fetchAndStoreRecalls(
        vehicleId: Long,
        make: String,
        model: String,
        year: Int
    ): Result<Int> = withContext(ioDispatcher) {
        runCatching {
            val url = "https://api.nhtsa.gov/recalls/recallsByVehicle?" +
                "make=${make.replace(" ", "%20")}" +
                "&model=${model.replace(" ", "%20")}" +
                "&modelYear=$year"

            val request = Request.Builder().url(url).build()
            val responseBody = okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) error("NHTSA API error: ${response.code}")
                response.body?.string() ?: error("Empty response from NHTSA")
            }

            val root = nhtsaJson.parseToJsonElement(responseBody).jsonObject
            val results = root["results"]?.jsonArray ?: JsonArray(emptyList())

            val now = System.currentTimeMillis()
            var newCount = 0

            val entities = results.mapNotNull { element ->
                val obj = element.jsonObject
                val campaignId = obj["CampaignNumber"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                val existing = dao.getByCampaignAndVehicle(campaignId, vehicleId)
                if (existing != null) return@mapNotNull null

                newCount++
                val consequence = obj["Consequence"]?.jsonPrimitive?.contentOrNull ?: ""
                val severity = classifySeverity(consequence)
                val rawDate = obj["ReportReceivedDate"]?.jsonPrimitive?.contentOrNull ?: ""
                val issuedDate = parseNhtsaDate(rawDate)

                val makeStr = obj["Make"]?.jsonPrimitive?.contentOrNull ?: make
                val modelStr = obj["Model"]?.jsonPrimitive?.contentOrNull ?: model
                val modelYearStr = obj["ModelYear"]?.jsonPrimitive?.contentOrNull ?: year.toString()
                val modelYearInt = modelYearStr.trim().toIntOrNull() ?: year

                RecallAlertEntity(
                    vehicleId = vehicleId,
                    campaignId = campaignId,
                    make = makeStr,
                    model = modelStr,
                    yearFrom = modelYearInt,
                    yearTo = modelYearInt,
                    componentName = obj["Component"]?.jsonPrimitive?.contentOrNull ?: "",
                    consequence = consequence,
                    remedy = obj["Remedy"]?.jsonPrimitive?.contentOrNull ?: "",
                    description = obj["Summary"]?.jsonPrimitive?.contentOrNull ?: "",
                    severity = severity.name,
                    issuedDate = issuedDate,
                    nhtsaUrl = "https://www.nhtsa.gov/vehicle-safety/recalls#$campaignId",
                    isAcknowledged = false,
                    createdAt = now,
                    updatedAt = now
                )
            }

            if (entities.isNotEmpty()) {
                dao.insertAll(entities)
            }

            newCount
        }
    }

    override suspend fun acknowledge(recallId: Long) {
        dao.acknowledge(recallId, System.currentTimeMillis())
    }

    private fun classifySeverity(consequence: String): RecallSeverity {
        val lower = consequence.lowercase()
        return when {
            lower.contains("death") || lower.contains("fatal") ||
                lower.contains("fire") || lower.contains("rollover") ||
                lower.contains("crash") -> RecallSeverity.CRITICAL
            lower.contains("injury") || lower.contains("accident") ||
                lower.contains("burn") -> RecallSeverity.MODERATE
            else -> RecallSeverity.MINOR
        }
    }

    private fun parseNhtsaDate(raw: String): String {
        // Format: "/Date(1234567890000)/"
        return try {
            val millis = Regex("\\d+").find(raw)?.value?.toLong() ?: return raw
            val instant = Instant.ofEpochMilli(millis)
            val date = instant.atOffset(ZoneOffset.UTC).toLocalDate()
            date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            raw
        }
    }
}
