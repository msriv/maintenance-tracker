package com.moto.tracker.ui.feature.fuel.list;

import androidx.lifecycle.SavedStateHandle;
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
public final class FuelLogListViewModel_Factory implements Factory<FuelLogListViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<FuelRepository> fuelRepositoryProvider;

  private final Provider<VehicleRepository> vehicleRepositoryProvider;

  public FuelLogListViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<FuelRepository> fuelRepositoryProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.fuelRepositoryProvider = fuelRepositoryProvider;
    this.vehicleRepositoryProvider = vehicleRepositoryProvider;
  }

  @Override
  public FuelLogListViewModel get() {
    return newInstance(savedStateHandleProvider.get(), fuelRepositoryProvider.get(), vehicleRepositoryProvider.get());
  }

  public static FuelLogListViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<FuelRepository> fuelRepositoryProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider) {
    return new FuelLogListViewModel_Factory(savedStateHandleProvider, fuelRepositoryProvider, vehicleRepositoryProvider);
  }

  public static FuelLogListViewModel newInstance(SavedStateHandle savedStateHandle,
      FuelRepository fuelRepository, VehicleRepository vehicleRepository) {
    return new FuelLogListViewModel(savedStateHandle, fuelRepository, vehicleRepository);
  }
}
