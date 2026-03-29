package com.moto.tracker.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.moto.tracker.domain.repository.RegistryRepository
import com.moto.tracker.domain.usecase.registry.SyncRegistryUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class RegistrySyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncRegistry: SyncRegistryUseCase,
    private val registryRepository: RegistryRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val lastSyncTime = registryRepository.getLastSyncTime()
        val twentyFourHoursMs = TimeUnit.HOURS.toMillis(24)
        val now = System.currentTimeMillis()

        // Skip sync if it was done less than 24 hours ago
        if (lastSyncTime != null && (now - lastSyncTime) < twentyFourHoursMs) {
            return Result.success()
        }

        return syncRegistry().fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() }
        )
    }
}
