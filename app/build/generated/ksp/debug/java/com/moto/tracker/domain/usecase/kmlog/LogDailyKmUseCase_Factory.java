package com.moto.tracker.domain.usecase.kmlog;

import com.moto.tracker.domain.repository.MaintenanceRepository;
import com.moto.tracker.domain.repository.VehicleRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class LogDailyKmUseCase_Factory implements Factory<LogDailyKmUseCase> {
  private final Provider<MaintenanceRepository> maintenanceRepositoryProvider;

  private final Provider<VehicleRepository> vehicleRepositoryProvider;

  public LogDailyKmUseCase_Factory(Provider<MaintenanceRepository> maintenanceRepositoryProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider) {
    this.maintenanceRepositoryProvider = maintenanceRepositoryProvider;
    this.vehicleRepositoryProvider = vehicleRepositoryProvider;
  }

  @Override
  public LogDailyKmUseCase get() {
    return newInstance(maintenanceRepositoryProvider.get(), vehicleRepositoryProvider.get());
  }

  public static LogDailyKmUseCase_Factory create(
      Provider<MaintenanceRepository> maintenanceRepositoryProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider) {
    return new LogDailyKmUseCase_Factory(maintenanceRepositoryProvider, vehicleRepositoryProvider);
  }

  public static LogDailyKmUseCase newInstance(MaintenanceRepository maintenanceRepository,
      VehicleRepository vehicleRepository) {
    return new LogDailyKmUseCase(maintenanceRepository, vehicleRepository);
  }
}
