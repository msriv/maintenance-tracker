package com.moto.tracker.ui.feature.maintenance.schedule;

import com.moto.tracker.domain.repository.ManufacturerScheduleRepository;
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
public final class ManufacturerScheduleViewModel_Factory implements Factory<ManufacturerScheduleViewModel> {
  private final Provider<ManufacturerScheduleRepository> repositoryProvider;

  public ManufacturerScheduleViewModel_Factory(
      Provider<ManufacturerScheduleRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ManufacturerScheduleViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static ManufacturerScheduleViewModel_Factory create(
      Provider<ManufacturerScheduleRepository> repositoryProvider) {
    return new ManufacturerScheduleViewModel_Factory(repositoryProvider);
  }

  public static ManufacturerScheduleViewModel newInstance(
      ManufacturerScheduleRepository repository) {
    return new ManufacturerScheduleViewModel(repository);
  }
}
