package com.moto.tracker.data.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.moto.tracker.data.local.dao.MaintenanceTaskDao;
import com.moto.tracker.data.local.dao.VehicleDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
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
public final class MaintenanceCheckWorker_Factory {
  private final Provider<MaintenanceTaskDao> maintenanceTaskDaoProvider;

  private final Provider<VehicleDao> vehicleDaoProvider;

  public MaintenanceCheckWorker_Factory(Provider<MaintenanceTaskDao> maintenanceTaskDaoProvider,
      Provider<VehicleDao> vehicleDaoProvider) {
    this.maintenanceTaskDaoProvider = maintenanceTaskDaoProvider;
    this.vehicleDaoProvider = vehicleDaoProvider;
  }

  public MaintenanceCheckWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, maintenanceTaskDaoProvider.get(), vehicleDaoProvider.get());
  }

  public static MaintenanceCheckWorker_Factory create(
      Provider<MaintenanceTaskDao> maintenanceTaskDaoProvider,
      Provider<VehicleDao> vehicleDaoProvider) {
    return new MaintenanceCheckWorker_Factory(maintenanceTaskDaoProvider, vehicleDaoProvider);
  }

  public static MaintenanceCheckWorker newInstance(Context context, WorkerParameters params,
      MaintenanceTaskDao maintenanceTaskDao, VehicleDao vehicleDao) {
    return new MaintenanceCheckWorker(context, params, maintenanceTaskDao, vehicleDao);
  }
}
