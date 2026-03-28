package com.moto.tracker.ui.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.repository.NotificationScheduler
import com.moto.tracker.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
    val kmReminderEnabled: Boolean = true,
    val maintenanceReminderEnabled: Boolean = true,
    val appointmentReminderEnabled: Boolean = true,
    val isLoading: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: UserPreferencesRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        prefs.observeReminderHour(),
        prefs.observeReminderMinute(),
        prefs.observeKmReminderEnabled(),
        prefs.observeMaintenanceReminderEnabled(),
        prefs.observeAppointmentReminderEnabled()
    ) { hour, minute, km, maint, appt ->
        SettingsUiState(
            reminderHour = hour,
            reminderMinute = minute,
            kmReminderEnabled = km,
            maintenanceReminderEnabled = maint,
            appointmentReminderEnabled = appt,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(isLoading = true)
    )

    fun onReminderTimeChange(hour: Int, minute: Int) {
        viewModelScope.launch {
            prefs.setReminderTime(hour, minute)
            if (uiState.value.kmReminderEnabled) {
                notificationScheduler.scheduleDailyKmReminder(hour, minute)
            }
        }
    }

    fun onKmReminderToggle(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setKmReminderEnabled(enabled)
            val state = uiState.value
            if (enabled) {
                notificationScheduler.scheduleDailyKmReminder(state.reminderHour, state.reminderMinute)
            } else {
                notificationScheduler.cancelDailyKmReminder()
            }
        }
    }

    fun onMaintenanceReminderToggle(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setMaintenanceReminderEnabled(enabled)
            if (enabled) notificationScheduler.scheduleMaintenanceCheck()
        }
    }

    fun onAppointmentReminderToggle(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setAppointmentReminderEnabled(enabled)
        }
    }
}
