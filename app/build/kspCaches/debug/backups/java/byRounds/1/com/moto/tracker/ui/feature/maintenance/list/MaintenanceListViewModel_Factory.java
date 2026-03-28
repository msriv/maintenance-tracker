package com.moto.tracker.ui.feature.maintenance.list;

import androidx.lifecycle.SavedStateHandle;
import com.moto.tracker.domain.repository.MaintenanceRepository;
import com.moto.tracker.domain.repository.VehicleRepository;
import com.moto.tracker.domain.usecase.maintenance.GetMaintenanceTasksUseCase;
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
public final class MaintenanceListViewModel_Factory implements Factory<MaintenanceListViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<GetMaintenanceTasksUseCase> getMaintenanceTasksProvider;

  private final Provider<VehicleRepository> vehicleRepositoryProvider;

  private final Provider<MaintenanceRepository> maintenanceRepositoryProvider;

  public MaintenanceListViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<GetMaintenanceTasksUseCase> getMaintenanceTasksProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider,
      Provider<MaintenanceRepository> maintenanceRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.getMaintenanceTasksProvider = getMaintenanceTasksProvider;
    this.vehicleRepositoryProvider = vehicleRepositoryProvider;
    this.maintenanceRepositoryProvider = maintenanceRepositoryProvider;
  }

  @Override
  public MaintenanceListViewModel get() {
    return newInstance(savedStateHandleProvider.get(), getMaintenanceTasksProvider.get(), vehicleRepositoryProvider.get(), maintenanceRepositoryProvider.get());
  }

  public static MaintenanceListViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<GetMaintenanceTasksUseCase> getMaintenanceTasksProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider,
      Provider<MaintenanceRepository> maintenanceRepositoryProvider) {
    return new MaintenanceListViewModel_Factory(savedStateHandleProvider, getMaintenanceTasksProvider, vehicleRepositoryProvider, maintenanceRepositoryProvider);
  }

  public static MaintenanceListViewModel newInstance(SavedStateHandle savedStateHandle,
      GetMaintenanceTasksUseCase getMaintenanceTasks, VehicleRepository vehicleRepository,
      MaintenanceRepository maintenanceRepository) {
    return new MaintenanceListViewModel(savedStateHandle, getMaintenanceTasks, vehicleRepository, maintenanceRepository);
  }
}
