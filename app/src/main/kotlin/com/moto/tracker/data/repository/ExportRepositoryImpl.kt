package com.moto.tracker.data.repository

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.moto.tracker.data.local.dao.FuelLogDao
import com.moto.tracker.data.local.dao.MaintenanceLogDao
import com.moto.tracker.data.local.dao.VehicleDao
import com.moto.tracker.data.local.mapper.toDomain
import com.moto.tracker.domain.repository.ExportRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ExportRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val vehicleDao: VehicleDao,
    private val maintenanceLogDao: MaintenanceLogDao,
    private val fuelLogDao: FuelLogDao
) : ExportRepository {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override suspend fun exportToCsv(vehicleId: Long, from: Long, to: Long): File {
        val vehicle = vehicleDao.getById(vehicleId)?.toDomain()
            ?: error("Vehicle not found")
        val maintenanceLogs = maintenanceLogDao.getByVehicleAndDateRange(vehicleId, from, to)
        val fuelLogs = fuelLogDao.getByVehicleAndDateRange(vehicleId, from, to)

        val file = File(context.cacheDir, "export_${vehicle.nickname}_${System.currentTimeMillis()}.csv")

        FileWriter(file).use { writer ->
            writer.appendLine("MotoTracker Export — ${vehicle.displayName}")
            writer.appendLine("Exported on: ${dateFormat.format(Date())}")
            writer.appendLine()

            // Maintenance section
            writer.appendLine("MAINTENANCE HISTORY")
            writer.appendLine("Date,Task,Odometer (km),Cost (₹),Mechanic,Service Center,Notes")
            maintenanceLogs.forEach { log ->
                writer.appendLine(
                    "${dateFormat.format(Date(log.performedDate))}," +
                    "${log.vehicleId}," +
                    "${log.odometer}," +
                    "${log.cost ?: ""}," +
                    "${log.mechanicName ?: ""}," +
                    "${log.serviceCenterName ?: ""}," +
                    "${(log.notes ?: "").replace(",", ";")}"
                )
            }
            writer.appendLine()

            // Fuel section
            writer.appendLine("FUEL LOG")
            writer.appendLine("Date,Odometer (km),Liters,Price/L (₹),Total Cost (₹),Tank Full,km/L,Station,Notes")
            fuelLogs.forEach { log ->
                writer.appendLine(
                    "${dateFormat.format(Date(log.fillDate))}," +
                    "${log.odometer}," +
                    "${log.liters}," +
                    "${log.pricePerLiter}," +
                    "${log.totalCost}," +
                    "${if (log.isTankFull) "Yes" else "No"}," +
                    "${log.kmPerLiter?.let { "%.1f".format(it) } ?: ""}," +
                    "${log.stationName ?: ""}," +
                    "${(log.notes ?: "").replace(",", ";")}"
                )
            }
        }
        return file
    }

    override suspend fun exportToPdf(vehicleId: Long, from: Long, to: Long): File {
        val vehicle = vehicleDao.getById(vehicleId)?.toDomain()
            ?: error("Vehicle not found")
        val maintenanceLogs = maintenanceLogDao.getByVehicleAndDateRange(vehicleId, from, to)
        val fuelLogs = fuelLogDao.getByVehicleAndDateRange(vehicleId, from, to)

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()  // A4
        var page = pdfDocument.startPage(pageInfo)
        var canvas: Canvas = page.canvas

        val titlePaint = Paint().apply {
            textSize = 20f
            color = Color.parseColor("#1A237E")
            isFakeBoldText = true
        }
        val headingPaint = Paint().apply {
            textSize = 14f
            color = Color.parseColor("#FF6F00")
            isFakeBoldText = true
        }
        val bodyPaint = Paint().apply {
            textSize = 10f
            color = Color.DKGRAY
        }
        val subtlePaint = Paint().apply {
            textSize = 9f
            color = Color.GRAY
        }

        var y = 50f
        val margin = 40f
        val pageWidth = 595f

        fun newPageIfNeeded() {
            if (y > 780f) {
                pdfDocument.finishPage(page)
                val nextPageInfo = PdfDocument.PageInfo.Builder(595, 842, pdfDocument.pages.size + 1).create()
                page = pdfDocument.startPage(nextPageInfo)
                canvas = page.canvas
                y = 50f
            }
        }

        // Header
        canvas.drawText("MotoTracker", margin, y, titlePaint)
        y += 25f
        canvas.drawText("Vehicle Report — ${vehicle.displayName}", margin, y, headingPaint)
        y += 18f
        canvas.drawText("Period: ${dateFormat.format(Date(from))} – ${dateFormat.format(Date(to))}", margin, y, subtlePaint)
        canvas.drawText("Generated: ${dateFormat.format(Date())}", pageWidth - margin - 150, y, subtlePaint)
        y += 10f

        // Divider
        val linePaint = Paint().apply {
            color = Color.parseColor("#FF6F00")
            strokeWidth = 1.5f
        }
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint)
        y += 20f

        // Maintenance section
        canvas.drawText("Maintenance History (${maintenanceLogs.size} records)", margin, y, headingPaint)
        y += 18f

        if (maintenanceLogs.isEmpty()) {
            canvas.drawText("No maintenance records in this period.", margin, y, bodyPaint)
            y += 16f
        } else {
            maintenanceLogs.forEach { log ->
                newPageIfNeeded()
                val date = dateFormat.format(Date(log.performedDate))
                val cost = log.cost?.let { "₹%.0f".format(it) } ?: "—"
                canvas.drawText("• $date  |  ${log.odometer} km  |  $cost", margin, y, bodyPaint)
                y += 14f
                if (log.serviceCenterName != null) {
                    canvas.drawText("  ${log.serviceCenterName}", margin, y, subtlePaint)
                    y += 12f
                }
            }
        }
        y += 10f

        newPageIfNeeded()
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint)
        y += 20f

        // Fuel section
        canvas.drawText("Fuel Log (${fuelLogs.size} fill-ups)", margin, y, headingPaint)
        y += 18f

        val totalFuelCost = fuelLogs.sumOf { it.totalCost }
        val avgKmPerL = fuelLogs.mapNotNull { it.kmPerLiter }.average().let {
            if (it.isNaN()) null else "%.1f km/L".format(it)
        }
        canvas.drawText("Total fuel cost: ₹%.0f  |  Avg efficiency: ${avgKmPerL ?: "—"}".format(totalFuelCost), margin, y, bodyPaint)
        y += 18f

        fuelLogs.forEach { log ->
            newPageIfNeeded()
            val date = dateFormat.format(Date(log.fillDate))
            val kml = log.kmPerLiter?.let { "  ${"%.1f".format(it)} km/L" } ?: ""
            canvas.drawText("• $date  |  ${log.liters}L  |  ₹%.0f$kml".format(log.totalCost), margin, y, bodyPaint)
            y += 14f
        }

        pdfDocument.finishPage(page)

        val file = File(context.cacheDir, "export_${vehicle.nickname}_${System.currentTimeMillis()}.pdf")
        file.outputStream().use { pdfDocument.writeTo(it) }
        pdfDocument.close()

        return file
    }
}
