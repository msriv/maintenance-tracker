package com.moto.tracker.domain.usecase.backup

import com.moto.tracker.domain.model.BackupStatus
import com.moto.tracker.domain.repository.CloudBackupRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBackupStatusUseCase @Inject constructor(
    private val backupRepo: CloudBackupRepository
) {
    operator fun invoke(): Flow<BackupStatus> = backupRepo.observeBackupStatus()
}
