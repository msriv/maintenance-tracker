package com.moto.tracker.data.local

import android.content.Context
import com.moto.tracker.data.local.dao.ManufacturerScheduleDao
import com.moto.tracker.data.local.entity.ManufacturerScheduleEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManufacturerScheduleSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: ManufacturerScheduleDao
) {
    suspend fun seedIfEmpty() {
        if (dao.count() > 0) return

        val json = context.assets.open("manufacturer_schedules.json")
            .bufferedReader()
            .use { it.readText() }
        val array = JSONArray(json)
        val entities = (0 until array.length()).map { i ->
            val obj = array.getJSONObject(i)
            ManufacturerScheduleEntity(
                make = obj.getString("make"),
                model = obj.getString("model"),
                yearFrom = obj.getInt("yearFrom"),
                yearTo = obj.optInt("yearTo", -1).takeIf { it != -1 },
                taskType = obj.getString("taskType"),
                taskLabel = obj.getString("taskLabel"),
                intervalDays = obj.optInt("intervalDays", -1).takeIf { it != -1 },
                intervalKm = obj.optInt("intervalKm", -1).takeIf { it != -1 },
                notes = obj.optString("notes").takeIf { it.isNotEmpty() && it != "null" }
            )
        }
        dao.insertAll(entities)
    }
}
