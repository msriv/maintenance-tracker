package com.moto.tracker.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.moto.tracker.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    private object Keys {
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val KM_REMINDER_ENABLED = booleanPreferencesKey("km_reminder_enabled")
        val MAINTENANCE_REMINDER_ENABLED = booleanPreferencesKey("maintenance_reminder_enabled")
        val APPOINTMENT_REMINDER_ENABLED = booleanPreferencesKey("appointment_reminder_enabled")
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    }

    override fun observeReminderHour(): Flow<Int> =
        dataStore.data.map { it[Keys.REMINDER_HOUR] ?: 20 }

    override fun observeReminderMinute(): Flow<Int> =
        dataStore.data.map { it[Keys.REMINDER_MINUTE] ?: 0 }

    override fun observeKmReminderEnabled(): Flow<Boolean> =
        dataStore.data.map { it[Keys.KM_REMINDER_ENABLED] ?: true }

    override fun observeMaintenanceReminderEnabled(): Flow<Boolean> =
        dataStore.data.map { it[Keys.MAINTENANCE_REMINDER_ENABLED] ?: true }

    override fun observeAppointmentReminderEnabled(): Flow<Boolean> =
        dataStore.data.map { it[Keys.APPOINTMENT_REMINDER_ENABLED] ?: true }

    override fun observeIsFirstLaunch(): Flow<Boolean> =
        dataStore.data.map { it[Keys.IS_FIRST_LAUNCH] ?: true }

    override suspend fun setReminderTime(hour: Int, minute: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.REMINDER_HOUR] = hour
            prefs[Keys.REMINDER_MINUTE] = minute
        }
    }

    override suspend fun setKmReminderEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.KM_REMINDER_ENABLED] = enabled }
    }

    override suspend fun setMaintenanceReminderEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.MAINTENANCE_REMINDER_ENABLED] = enabled }
    }

    override suspend fun setAppointmentReminderEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.APPOINTMENT_REMINDER_ENABLED] = enabled }
    }

    override suspend fun setFirstLaunchComplete() {
        dataStore.edit { it[Keys.IS_FIRST_LAUNCH] = false }
    }
}
