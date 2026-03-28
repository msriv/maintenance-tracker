package com.moto.tracker.ui.feature.maintenance.log;

import androidx.lifecycle.SavedStateHandle;
import com.moto.tracker.domain.repository.MaintenanceRepository;
import com.moto.tracker.domain.usecase.maintenance.LogMaintenanceUseCase;
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
public final class LogMaintenanceViewModel_Factory implements Factory<LogMaintenanceViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<MaintenanceRepository> maintenanceRepositoryProvider;

  private final Provider<LogMaintenanceUseCase> logMaintenanceUseCaseProvider;

  public LogMaintenanceViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<MaintenanceRepository> maintenanceRepositoryProvider,
      Provider<LogMaintenanceUseCase> logMaintenanceUseCaseProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.maintenanceRepositoryProvider = maintenanceRepositoryProvider;
    this.logMaintenanceUseCaseProvider = logMaintenanceUseCaseProvider;
  }

  @Override
  public LogMaintenanceViewModel get() {
    return newInstance(savedStateHandleProvider.get(), maintenanceRepositoryProvider.get(), logMaintenanceUseCaseProvider.get());
  }

  public static LogMaintenanceViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<MaintenanceRepository> maintenanceRepositoryProvider,
      Provider<LogMaintenanceUseCase> logMaintenanceUseCaseProvider) {
    return new LogMaintenanceViewModel_Factory(savedStateHandleProvider, maintenanceRepositoryProvider, logMaintenanceUseCaseProvider);
  }

  public static LogMaintenanceViewModel newInstance(SavedStateHandle savedStateHandle,
      MaintenanceRepository maintenanceRepository, LogMaintenanceUseCase logMaintenanceUseCase) {
    return new LogMaintenanceViewModel(savedStateHandle, maintenanceRepository, logMaintenanceUseCase);
  }
}
