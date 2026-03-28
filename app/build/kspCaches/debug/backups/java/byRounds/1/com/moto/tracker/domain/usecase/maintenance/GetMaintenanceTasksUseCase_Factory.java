package com.moto.tracker.domain.usecase.maintenance;

import com.moto.tracker.domain.repository.MaintenanceRepository;
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
public final class GetMaintenanceTasksUseCase_Factory implements Factory<GetMaintenanceTasksUseCase> {
  private final Provider<MaintenanceRepository> repositoryProvider;

  public GetMaintenanceTasksUseCase_Factory(Provider<MaintenanceRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetMaintenanceTasksUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetMaintenanceTasksUseCase_Factory create(
      Provider<MaintenanceRepository> repositoryProvider) {
    return new GetMaintenanceTasksUseCase_Factory(repositoryProvider);
  }

  public static GetMaintenanceTasksUseCase newInstance(MaintenanceRepository repository) {
    return new GetMaintenanceTasksUseCase(repository);
  }
}
