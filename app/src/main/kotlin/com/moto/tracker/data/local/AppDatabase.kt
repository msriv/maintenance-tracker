package com.moto.tracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.moto.tracker.data.local.dao.*
import com.moto.tracker.data.local.entity.*

@Database(
    entities = [
        VehicleEntity::class,
        DocumentEntity::class,
        MaintenanceTaskEntity::class,
        MaintenanceLogEntity::class,
        KmLogEntity::class,
        FuelLogEntity::class,
        ServiceAppointmentEntity::class,
        ManufacturerScheduleEntity::class,
    ],
    views = [ExpenseView::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun documentDao(): DocumentDao
    abstract fun maintenanceTaskDao(): MaintenanceTaskDao
    abstract fun maintenanceLogDao(): MaintenanceLogDao
    abstract fun kmLogDao(): KmLogDao
    abstract fun fuelLogDao(): FuelLogDao
    abstract fun serviceAppointmentDao(): ServiceAppointmentDao
    abstract fun manufacturerScheduleDao(): ManufacturerScheduleDao
    abstract fun expenseViewDao(): ExpenseViewDao
}
