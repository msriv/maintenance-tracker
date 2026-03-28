package com.moto.tracker.domain.usecase.maintenance;

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
public final class LogMaintenanceUseCase_Factory implements Factory<LogMaintenanceUseCase> {
  private final Provider<MaintenanceRepository> maintenanceRepositoryProvider;

  private final Provider<VehicleRepository> vehicleRepositoryProvider;

  private final Provider<ComputeNextDueDateUseCase> computeNextDueDateProvider;

  public LogMaintenanceUseCase_Factory(
      Provider<MaintenanceRepository> maintenanceRepositoryProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider,
      Provider<ComputeNextDueDateUseCase> computeNextDueDateProvider) {
    this.maintenanceRepositoryProvider = maintenanceRepositoryProvider;
    this.vehicleRepositoryProvider = vehicleRepositoryProvider;
    this.computeNextDueDateProvider = computeNextDueDateProvider;
  }

  @Override
  public LogMaintenanceUseCase get() {
    return newInstance(maintenanceRepositoryProvider.get(), vehicleRepositoryProvider.get(), computeNextDueDateProvider.get());
  }

  public static LogMaintenanceUseCase_Factory create(
      Provider<MaintenanceRepository> maintenanceRepositoryProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider,
      Provider<ComputeNextDueDateUseCase> computeNextDueDateProvider) {
    return new LogMaintenanceUseCase_Factory(maintenanceRepositoryProvider, vehicleRepositoryProvider, computeNextDueDateProvider);
  }

  public static LogMaintenanceUseCase newInstance(MaintenanceRepository maintenanceRepository,
      VehicleRepository vehicleRepository, ComputeNextDueDateUseCase computeNextDueDate) {
    return new LogMaintenanceUseCase(maintenanceRepository, vehicleRepository, computeNextDueDate);
  }
}
