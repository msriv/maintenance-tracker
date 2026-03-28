package com.moto.tracker.domain.usecase.fuel;

import com.moto.tracker.domain.repository.FuelRepository;
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
public final class AddFuelLogUseCase_Factory implements Factory<AddFuelLogUseCase> {
  private final Provider<FuelRepository> fuelRepositoryProvider;

  private final Provider<VehicleRepository> vehicleRepositoryProvider;

  private final Provider<ComputeKmPerLiterUseCase> computeKmPerLiterProvider;

  public AddFuelLogUseCase_Factory(Provider<FuelRepository> fuelRepositoryProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider,
      Provider<ComputeKmPerLiterUseCase> computeKmPerLiterProvider) {
    this.fuelRepositoryProvider = fuelRepositoryProvider;
    this.vehicleRepositoryProvider = vehicleRepositoryProvider;
    this.computeKmPerLiterProvider = computeKmPerLiterProvider;
  }

  @Override
  public AddFuelLogUseCase get() {
    return newInstance(fuelRepositoryProvider.get(), vehicleRepositoryProvider.get(), computeKmPerLiterProvider.get());
  }

  public static AddFuelLogUseCase_Factory create(Provider<FuelRepository> fuelRepositoryProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider,
      Provider<ComputeKmPerLiterUseCase> computeKmPerLiterProvider) {
    return new AddFuelLogUseCase_Factory(fuelRepositoryProvider, vehicleRepositoryProvider, computeKmPerLiterProvider);
  }

  public static AddFuelLogUseCase newInstance(FuelRepository fuelRepository,
      VehicleRepository vehicleRepository, ComputeKmPerLiterUseCase computeKmPerLiter) {
    return new AddFuelLogUseCase(fuelRepository, vehicleRepository, computeKmPerLiter);
  }
}
