package com.moto.tracker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.moto.tracker.data.local.AppDatabase
import com.moto.tracker.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "moto_tracker_prefs")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "moto_tracker.db"
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideVehicleDao(db: AppDatabase): VehicleDao = db.vehicleDao()

    @Provides
    fun provideDocumentDao(db: AppDatabase): DocumentDao = db.documentDao()

    @Provides
    fun provideMaintenanceTaskDao(db: AppDatabase): MaintenanceTaskDao = db.maintenanceTaskDao()

    @Provides
    fun provideMaintenanceLogDao(db: AppDatabase): MaintenanceLogDao = db.maintenanceLogDao()

    @Provides
    fun provideKmLogDao(db: AppDatabase): KmLogDao = db.kmLogDao()

    @Provides
    fun provideFuelLogDao(db: AppDatabase): FuelLogDao = db.fuelLogDao()

    @Provides
    fun provideServiceAppointmentDao(db: AppDatabase): ServiceAppointmentDao = db.serviceAppointmentDao()

    @Provides
    fun provideManufacturerScheduleDao(db: AppDatabase): ManufacturerScheduleDao = db.manufacturerScheduleDao()

    @Provides
    fun provideExpenseViewDao(db: AppDatabase): ExpenseViewDao = db.expenseViewDao()

    @Provides
    fun provideRecallAlertDao(db: AppDatabase): RecallAlertDao = db.recallAlertDao()

    @Provides
    fun providePartsInventoryDao(db: AppDatabase): PartsInventoryDao = db.partsInventoryDao()

    @Provides
    fun provideSeasonalChecklistDao(db: AppDatabase): SeasonalChecklistDao = db.seasonalChecklistDao()

    @Provides
    fun provideServiceCenterDao(db: AppDatabase): ServiceCenterDao = db.serviceCenterDao()

    @Provides
    fun provideRideLogDao(db: AppDatabase): RideLogDao = db.rideLogDao()

    @Provides
    fun provideRegistryCacheDao(db: AppDatabase): RegistryCacheDao = db.registryCacheDao()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> = context.dataStore

    @Provides
    @Named("IO")
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Named("Default")
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Named("Main")
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}
