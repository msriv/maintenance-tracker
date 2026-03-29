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
        RecallAlertEntity::class,
        PartsInventoryEntity::class,
        SeasonalChecklistEntity::class,
        ServiceCenterEntity::class,
        RideLogEntity::class,
        RegistryMakeEntity::class,
        RegistryModelEntity::class,
        RegistryScheduleEntity::class,
        BestPracticeEntity::class,
    ],
    views = [ExpenseView::class],
    version = 2,
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
    abstract fun recallAlertDao(): RecallAlertDao
    abstract fun partsInventoryDao(): PartsInventoryDao
    abstract fun seasonalChecklistDao(): SeasonalChecklistDao
    abstract fun serviceCenterDao(): ServiceCenterDao
    abstract fun rideLogDao(): RideLogDao
    abstract fun registryCacheDao(): RegistryCacheDao
}
