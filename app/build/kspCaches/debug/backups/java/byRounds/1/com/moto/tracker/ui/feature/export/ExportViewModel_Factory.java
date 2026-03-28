package com.moto.tracker.ui.feature.export;

import android.content.Context;
import com.moto.tracker.domain.repository.ExportRepository;
import com.moto.tracker.domain.repository.VehicleRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ExportViewModel_Factory implements Factory<ExportViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<VehicleRepository> vehicleRepositoryProvider;

  private final Provider<ExportRepository> exportRepositoryProvider;

  public ExportViewModel_Factory(Provider<Context> contextProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider,
      Provider<ExportRepository> exportRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.vehicleRepositoryProvider = vehicleRepositoryProvider;
    this.exportRepositoryProvider = exportRepositoryProvider;
  }

  @Override
  public ExportViewModel get() {
    return newInstance(contextProvider.get(), vehicleRepositoryProvider.get(), exportRepositoryProvider.get());
  }

  public static ExportViewModel_Factory create(Provider<Context> contextProvider,
      Provider<VehicleRepository> vehicleRepositoryProvider,
      Provider<ExportRepository> exportRepositoryProvider) {
    return new ExportViewModel_Factory(contextProvider, vehicleRepositoryProvider, exportRepositoryProvider);
  }

  public static ExportViewModel newInstance(Context context, VehicleRepository vehicleRepository,
      ExportRepository exportRepository) {
    return new ExportViewModel(context, vehicleRepository, exportRepository);
  }
}
