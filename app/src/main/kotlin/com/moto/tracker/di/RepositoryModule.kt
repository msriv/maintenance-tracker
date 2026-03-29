package com.moto.tracker.di

import com.moto.tracker.data.repository.*
import com.moto.tracker.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVehicleRepository(impl: VehicleRepositoryImpl): VehicleRepository

    @Binds
    @Singleton
    abstract fun bindDocumentRepository(impl: DocumentRepositoryImpl): DocumentRepository

    @Binds
    @Singleton
    abstract fun bindMaintenanceRepository(impl: MaintenanceRepositoryImpl): MaintenanceRepository

    @Binds
    @Singleton
    abstract fun bindFuelRepository(impl: FuelRepositoryImpl): FuelRepository

    @Binds
    @Singleton
    abstract fun bindAppointmentRepository(impl: AppointmentRepositoryImpl): AppointmentRepository

    @Binds
    @Singleton
    abstract fun bindManufacturerScheduleRepository(impl: ManufacturerScheduleRepositoryImpl): ManufacturerScheduleRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(impl: ExpenseRepositoryImpl): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindExportRepository(impl: ExportRepositoryImpl): ExportRepository

    @Binds
    @Singleton
    abstract fun bindNotificationScheduler(impl: NotificationSchedulerImpl): NotificationScheduler

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindRecallRepository(impl: RecallRepositoryImpl): RecallRepository

    @Binds
    @Singleton
    abstract fun bindPartsRepository(impl: PartsRepositoryImpl): PartsRepository

    @Binds
    @Singleton
    abstract fun bindSeasonalChecklistRepository(impl: SeasonalChecklistRepositoryImpl): SeasonalChecklistRepository

    @Binds
    @Singleton
    abstract fun bindServiceCenterRepository(impl: ServiceCenterRepositoryImpl): ServiceCenterRepository

    @Binds
    @Singleton
    abstract fun bindRideLogRepository(impl: RideLogRepositoryImpl): RideLogRepository

    @Binds
    @Singleton
    abstract fun bindRegistryRepository(impl: RegistryRepositoryImpl): RegistryRepository

    @Binds
    @Singleton
    abstract fun bindCloudBackupRepository(impl: CloudBackupRepositoryImpl): CloudBackupRepository
}
