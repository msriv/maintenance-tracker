package com.moto.tracker.domain.repository

import java.io.File

interface ExportRepository {
    suspend fun exportToCsv(vehicleId: Long, from: Long, to: Long): File
    suspend fun exportToPdf(vehicleId: Long, from: Long, to: Long): File
}
