package com.moto.tracker.domain.usecase.backup

import com.moto.tracker.domain.repository.CloudBackupRepository
import javax.inject.Inject

class RestoreFromCloudUseCase @Inject constructor(
    private val backupRepo: CloudBackupRepository
) {
    suspend operator fun invoke(): Result<Unit> = backupRepo.restore()
}
