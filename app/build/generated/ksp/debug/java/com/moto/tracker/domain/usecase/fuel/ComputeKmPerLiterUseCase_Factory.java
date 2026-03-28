package com.moto.tracker.domain.usecase.fuel;

import com.moto.tracker.domain.repository.FuelRepository;
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
public final class ComputeKmPerLiterUseCase_Factory implements Factory<ComputeKmPerLiterUseCase> {
  private final Provider<FuelRepository> fuelRepositoryProvider;

  public ComputeKmPerLiterUseCase_Factory(Provider<FuelRepository> fuelRepositoryProvider) {
    this.fuelRepositoryProvider = fuelRepositoryProvider;
  }

  @Override
  public ComputeKmPerLiterUseCase get() {
    return newInstance(fuelRepositoryProvider.get());
  }

  public static ComputeKmPerLiterUseCase_Factory create(
      Provider<FuelRepository> fuelRepositoryProvider) {
    return new ComputeKmPerLiterUseCase_Factory(fuelRepositoryProvider);
  }

  public static ComputeKmPerLiterUseCase newInstance(FuelRepository fuelRepository) {
    return new ComputeKmPerLiterUseCase(fuelRepository);
  }
}
