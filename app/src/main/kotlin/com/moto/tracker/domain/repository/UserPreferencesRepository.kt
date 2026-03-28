package com.moto.tracker.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun observeReminderHour(): Flow<Int>
    fun observeReminderMinute(): Flow<Int>
    fun observeKmReminderEnabled(): Flow<Boolean>
    fun observeMaintenanceReminderEnabled(): Flow<Boolean>
    fun observeAppointmentReminderEnabled(): Flow<Boolean>
    fun observeIsFirstLaunch(): Flow<Boolean>
    suspend fun setReminderTime(hour: Int, minute: Int)
    suspend fun setKmReminderEnabled(enabled: Boolean)
    suspend fun setMaintenanceReminderEnabled(enabled: Boolean)
    suspend fun setAppointmentReminderEnabled(enabled: Boolean)
    suspend fun setFirstLaunchComplete()
}
