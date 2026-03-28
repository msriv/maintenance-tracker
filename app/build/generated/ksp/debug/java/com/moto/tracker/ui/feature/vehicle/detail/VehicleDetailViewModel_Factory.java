package com.moto.tracker.ui.feature.vehicle.detail;

import androidx.lifecycle.SavedStateHandle;
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
public final class VehicleDetailViewModel_Factory implements Factory<VehicleDetailViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<VehicleRepository> vehicleRepositoryProvider;

  public VehicleDetailViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.vehicleRepositoryProvider = vehicleRepositoryProvider;
  }

  @Override
  public VehicleDetailViewModel get() {
    return newInstance(savedStateHandleProvider.get(), vehicleRepositoryProvider.get());
  }

  public static VehicleDetailViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider) {
    return new VehicleDetailViewModel_Factory(savedStateHandleProvider, vehicleRepositoryProvider);
  }

  public static VehicleDetailViewModel newInstance(SavedStateHandle savedStateHandle,
      VehicleRepository vehicleRepository) {
    return new VehicleDetailViewModel(savedStateHandle, vehicleRepository);
  }
}
