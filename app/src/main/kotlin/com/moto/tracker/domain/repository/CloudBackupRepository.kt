package com.moto.tracker.domain.repository

import com.moto.tracker.domain.model.BackupStatus
import kotlinx.coroutines.flow.Flow

interface CloudBackupRepository {
    suspend fun backup(): Result<Unit>
    suspend fun restore(): Result<Unit>
    fun observeBackupStatus(): Flow<BackupStatus>
}
