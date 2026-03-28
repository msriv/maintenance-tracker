package com.moto.tracker.ui.feature.maintenance.kmlog;

import androidx.lifecycle.SavedStateHandle;
import com.moto.tracker.domain.repository.MaintenanceRepository;
import com.moto.tracker.domain.repository.VehicleRepository;
import com.moto.tracker.domain.usecase.kmlog.LogDailyKmUseCase;
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
public final class KmLogViewModel_Factory implements Factory<KmLogViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<VehicleRepository> vehicleRepositoryProvider;

  private final Provider<MaintenanceRepository> maintenanceRepositoryProvider;

  private final Provider<LogDailyKmUseCase> logDailyKmProvider;

  public KmLogViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider,
      Provider<MaintenanceRepository> maintenanceRepositoryProvider,
      Provider<LogDailyKmUseCase> logDailyKmProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.vehicleRepositoryProvider = vehicleRepositoryProvider;
    this.maintenanceRepositoryProvider = maintenanceRepositoryProvider;
    this.logDailyKmProvider = logDailyKmProvider;
  }

  @Override
  public KmLogViewModel get() {
    return newInstance(savedStateHandleProvider.get(), vehicleRepositoryProvider.get(), maintenanceRepositoryProvider.get(), logDailyKmProvider.get());
  }

  public static KmLogViewModel_Factory create(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider,
      Provider<MaintenanceRepository> maintenanceRepositoryProvider,
      Provider<LogDailyKmUseCase> logDailyKmProvider) {
    return new KmLogViewModel_Factory(savedStateHandleProvider, vehicleRepositoryProvider, maintenanceRepositoryProvider, logDailyKmProvider);
  }

  public static KmLogViewModel newInstance(SavedStateHandle savedStateHandle,
      VehicleRepository vehicleRepository, MaintenanceRepository maintenanceRepository,
      LogDailyKmUseCase logDailyKm) {
    return new KmLogViewModel(savedStateHandle, vehicleRepository, maintenanceRepository, logDailyKm);
  }
}
