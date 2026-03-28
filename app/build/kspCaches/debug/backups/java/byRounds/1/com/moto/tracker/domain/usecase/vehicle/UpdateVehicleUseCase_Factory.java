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
public final class UpdateVehicleUseCase_Factory implements Factory<UpdateVehicleUseCase> {
  private final Provider<VehicleRepository> repositoryProvider;

  public UpdateVehicleUseCase_Factory(Provider<VehicleRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UpdateVehicleUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static UpdateVehicleUseCase_Factory create(
      Provider<VehicleRepository> repositoryProvider) {
    return new UpdateVehicleUseCase_Factory(repositoryProvider);
  }

  public static UpdateVehicleUseCase newInstance(VehicleRepository repository) {
    return new UpdateVehicleUseCase(repository);
  }
}
