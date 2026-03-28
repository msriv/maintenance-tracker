package com.moto.tracker.data.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.moto.tracker.data.repository.NotificationSchedulerImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationScheduler: NotificationSchedulerImpl

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        scope.launch {
            // Re-schedule WorkManager jobs after boot (they don't survive reboot by default)
            notificationScheduler.scheduleMaintenanceCheck()
            // Km reminder will be rescheduled via settings on next app open
        }
    }
}
