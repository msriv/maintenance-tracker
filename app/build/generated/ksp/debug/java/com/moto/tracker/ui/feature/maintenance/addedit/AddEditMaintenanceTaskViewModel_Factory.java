package com.moto.tracker.ui.feature.maintenance.addedit;

import androidx.lifecycle.SavedStateHandle;
import com.moto.tracker.domain.repository.MaintenanceRepository;
import com.moto.tracker.domain.usecase.maintenance.AddMaintenanceTaskUseCase;
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
public final class AddEditMaintenanceTaskViewModel_Factory implements Factory<AddEditMaintenanceTaskViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<MaintenanceRepository> maintenanceRepositoryProvider;

  private final Provider<AddMaintenanceTaskUseCase> addMaintenanceTaskProvider;

  public AddEditMaintenanceTaskViewModel_Factory(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<MaintenanceRepository> maintenanceRepositoryProvider,
      Provider<AddMaintenanceTaskUseCase> addMaintenanceTaskProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.maintenanceRepositoryProvider = maintenanceRepositoryProvider;
    this.addMaintenanceTaskProvider = addMaintenanceTaskProvider;
  }

  @Override
  public AddEditMaintenanceTaskViewModel get() {
    return newInstance(savedStateHandleProvider.get(), maintenanceRepositoryProvider.get(), addMaintenanceTaskProvider.get());
  }

  public static AddEditMaintenanceTaskViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<MaintenanceRepository> maintenanceRepositoryProvider,
      Provider<AddMaintenanceTaskUseCase> addMaintenanceTaskProvider) {
    return new AddEditMaintenanceTaskViewModel_Factory(savedStateHandleProvider, maintenanceRepositoryProvider, addMaintenanceTaskProvider);
  }

  public static AddEditMaintenanceTaskViewModel newInstance(SavedStateHandle savedStateHandle,
      MaintenanceRepository maintenanceRepository, AddMaintenanceTaskUseCase addMaintenanceTask) {
    return new AddEditMaintenanceTaskViewModel(savedStateHandle, maintenanceRepository, addMaintenanceTask);
  }
}
