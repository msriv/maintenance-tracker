package com.moto.tracker.domain.usecase.vehicle;

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
public final class AddVehicleUseCase_Factory implements Factory<AddVehicleUseCase> {
  private final Provider<VehicleRepository> repositoryProvider;

  public AddVehicleUseCase_Factory(Provider<VehicleRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public AddVehicleUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static AddVehicleUseCase_Factory create(Provider<VehicleRepository> repositoryProvider) {
    return new AddVehicleUseCase_Factory(repositoryProvider);
  }

  public static AddVehicleUseCase newInstance(VehicleRepository repository) {
    return new AddVehicleUseCase(repository);
  }
}
