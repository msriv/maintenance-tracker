package com.moto.tracker.domain.usecase.export

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.moto.tracker.domain.repository.ExportRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class ExportToPdfUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exportRepository: ExportRepository,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(vehicleId: Long, from: Long, to: Long): Result<Uri> =
        withContext(ioDispatcher) {
            runCatching {
                val file = exportRepository.exportToPdf(vehicleId, from, to)
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            }
        }
}
