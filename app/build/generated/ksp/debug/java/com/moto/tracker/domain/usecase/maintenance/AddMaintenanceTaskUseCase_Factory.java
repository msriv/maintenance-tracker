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
public final class AddMaintenanceTaskUseCase_Factory implements Factory<AddMaintenanceTaskUseCase> {
  private final Provider<MaintenanceRepository> maintenanceRepositoryProvider;

  private final Provider<VehicleRepository> vehicleRepositoryProvider;

  private final Provider<ComputeNextDueDateUseCase> computeNextDueDateProvider;

  public AddMaintenanceTaskUseCase_Factory(
      Provider<MaintenanceRepository> maintenanceRepositoryProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider,
      Provider<ComputeNextDueDateUseCase> computeNextDueDateProvider) {
    this.maintenanceRepositoryProvider = maintenanceRepositoryProvider;
    this.vehicleRepositoryProvider = vehicleRepositoryProvider;
    this.computeNextDueDateProvider = computeNextDueDateProvider;
  }

  @Override
  public AddMaintenanceTaskUseCase get() {
    return newInstance(maintenanceRepositoryProvider.get(), vehicleRepositoryProvider.get(), computeNextDueDateProvider.get());
  }

  public static AddMaintenanceTaskUseCase_Factory create(
      Provider<MaintenanceRepository> maintenanceRepositoryProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider,
      Provider<ComputeNextDueDateUseCase> computeNextDueDateProvider) {
    return new AddMaintenanceTaskUseCase_Factory(maintenanceRepositoryProvider, vehicleRepositoryProvider, computeNextDueDateProvider);
  }

  public static AddMaintenanceTaskUseCase newInstance(MaintenanceRepository maintenanceRepository,
      VehicleRepository vehicleRepository, ComputeNextDueDateUseCase computeNextDueDate) {
    return new AddMaintenanceTaskUseCase(maintenanceRepository, vehicleRepository, computeNextDueDate);
  }
}
