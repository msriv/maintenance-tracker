package com.moto.tracker.ui.feature.vehicle.addedit;

import androidx.lifecycle.SavedStateHandle;
import com.moto.tracker.domain.repository.VehicleRepository;
import com.moto.tracker.domain.usecase.vehicle.AddVehicleUseCase;
import com.moto.tracker.domain.usecase.vehicle.UpdateVehicleUseCase;
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
public final class AddEditVehicleViewModel_Factory implements Factory<AddEditVehicleViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<VehicleRepository> vehicleRepositoryProvider;

  private final Provider<AddVehicleUseCase> addVehicleProvider;

  private final Provider<UpdateVehicleUseCase> updateVehicleProvider;

  public AddEditVehicleViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider,
      Provider<AddVehicleUseCase> addVehicleProvider,
      Provider<UpdateVehicleUseCase> updateVehicleProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.vehicleRepositoryProvider = vehicleRepositoryProvider;
    this.addVehicleProvider = addVehicleProvider;
    this.updateVehicleProvider = updateVehicleProvider;
  }

  @Override
  public AddEditVehicleViewModel get() {
    return newInstance(savedStateHandleProvider.get(), vehicleRepositoryProvider.get(), addVehicleProvider.get(), updateVehicleProvider.get());
  }

  public static AddEditVehicleViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider,
      Provider<AddVehicleUseCase> addVehicleProvider,
      Provider<UpdateVehicleUseCase> updateVehicleProvider) {
    return new AddEditVehicleViewModel_Factory(savedStateHandleProvider, vehicleRepositoryProvider, addVehicleProvider, updateVehicleProvider);
  }

  public static AddEditVehicleViewModel newInstance(SavedStateHandle savedStateHandle,
      VehicleRepository vehicleRepository, AddVehicleUseCase addVehicle,
      UpdateVehicleUseCase updateVehicle) {
    return new AddEditVehicleViewModel(savedStateHandle, vehicleRepository, addVehicle, updateVehicle);
  }
}
