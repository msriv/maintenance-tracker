package com.moto.tracker.data.repository;

import android.content.Context;
import com.moto.tracker.data.local.dao.FuelLogDao;
import com.moto.tracker.data.local.dao.MaintenanceLogDao;
import com.moto.tracker.data.local.dao.VehicleDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class ExportRepositoryImpl_Factory implements Factory<ExportRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<VehicleDao> vehicleDaoProvider;

  private final Provider<MaintenanceLogDao> maintenanceLogDaoProvider;

  private final Provider<FuelLogDao> fuelLogDaoProvider;

  public ExportRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<VehicleDao> vehicleDaoProvider,
      Provider<MaintenanceLogDao> maintenanceLogDaoProvider,
      Provider<FuelLogDao> fuelLogDaoProvider) {
    this.contextProvider = contextProvider;
    this.vehicleDaoProvider = vehicleDaoProvider;
    this.maintenanceLogDaoProvider = maintenanceLogDaoProvider;
    this.fuelLogDaoProvider = fuelLogDaoProvider;
  }

  @Override
  public ExportRepositoryImpl get() {
    return newInstance(contextProvider.get(), vehicleDaoProvider.get(), maintenanceLogDaoProvider.get(), fuelLogDaoProvider.get());
  }

  public static ExportRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<VehicleDao> vehicleDaoProvider,
      Provider<MaintenanceLogDao> maintenanceLogDaoProvider,
      Provider<FuelLogDao> fuelLogDaoProvider) {
    return new ExportRepositoryImpl_Factory(contextProvider, vehicleDaoProvider, maintenanceLogDaoProvider, fuelLogDaoProvider);
  }

  public static ExportRepositoryImpl newInstance(Context context, VehicleDao vehicleDao,
      MaintenanceLogDao maintenanceLogDao, FuelLogDao fuelLogDao) {
    return new ExportRepositoryImpl(context, vehicleDao, maintenanceLogDao, fuelLogDao);
  }
}
