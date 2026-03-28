package com.moto.tracker.ui.feature.vehicle.list;

import com.moto.tracker.domain.usecase.vehicle.DeleteVehicleUseCase;
import com.moto.tracker.domain.usecase.vehicle.GetVehiclesUseCase;
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
public final class VehicleListViewModel_Factory implements Factory<VehicleListViewModel> {
  private final Provider<GetVehiclesUseCase> getVehiclesProvider;

  private final Provider<DeleteVehicleUseCase> deleteVehicleProvider;

  public VehicleListViewModel_Factory(Provider<GetVehiclesUseCase> getVehiclesProvider,
      Provider<DeleteVehicleUseCase> deleteVehicleProvider) {
    this.getVehiclesProvider = getVehiclesProvider;
    this.deleteVehicleProvider = deleteVehicleProvider;
  }

  @Override
  public VehicleListViewModel get() {
    return newInstance(getVehiclesProvider.get(), deleteVehicleProvider.get());
  }

  public static VehicleListViewModel_Factory create(
      Provider<GetVehiclesUseCase> getVehiclesProvider,
      Provider<DeleteVehicleUseCase> deleteVehicleProvider) {
    return new VehicleListViewModel_Factory(getVehiclesProvider, deleteVehicleProvider);
  }

  public static VehicleListViewModel newInstance(GetVehiclesUseCase getVehicles,
      DeleteVehicleUseCase deleteVehicle) {
    return new VehicleListViewModel(getVehicles, deleteVehicle);
  }
}
