package com.moto.tracker.ui.feature.fuel.add;

import androidx.lifecycle.SavedStateHandle;
import com.moto.tracker.domain.repository.VehicleRepository;
import com.moto.tracker.domain.usecase.fuel.AddFuelLogUseCase;
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
public final class AddFuelLogViewModel_Factory implements Factory<AddFuelLogViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<VehicleRepository> vehicleRepositoryProvider;

  private final Provider<AddFuelLogUseCase> addFuelLogUseCaseProvider;

  public AddFuelLogViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider,
      Provider<AddFuelLogUseCase> addFuelLogUseCaseProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.vehicleRepositoryProvider = vehicleRepositoryProvider;
    this.addFuelLogUseCaseProvider = addFuelLogUseCaseProvider;
  }

  @Override
  public AddFuelLogViewModel get() {
    return newInstance(savedStateHandleProvider.get(), vehicleRepositoryProvider.get(), addFuelLogUseCaseProvider.get());
  }

  public static AddFuelLogViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider,
      Provider<AddFuelLogUseCase> addFuelLogUseCaseProvider) {
    return new AddFuelLogViewModel_Factory(savedStateHandleProvider, vehicleRepositoryProvider, addFuelLogUseCaseProvider);
  }

  public static AddFuelLogViewModel newInstance(SavedStateHandle savedStateHandle,
      VehicleRepository vehicleRepository, AddFuelLogUseCase addFuelLogUseCase) {
    return new AddFuelLogViewModel(savedStateHandle, vehicleRepository, addFuelLogUseCase);
  }
}
