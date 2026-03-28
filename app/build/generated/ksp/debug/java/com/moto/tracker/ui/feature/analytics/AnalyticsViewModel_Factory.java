package com.moto.tracker.ui.feature.analytics;

import com.moto.tracker.domain.repository.ExpenseRepository;
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
public final class AnalyticsViewModel_Factory implements Factory<AnalyticsViewModel> {
  private final Provider<VehicleRepository> vehicleRepositoryProvider;

  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  public AnalyticsViewModel_Factory(Provider<VehicleRepository> vehicleRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider) {
    this.vehicleRepositoryProvider = vehicleRepositoryProvider;
    this.expenseRepositoryProvider = expenseRepositoryProvider;
  }

  @Override
  public AnalyticsViewModel get() {
    return newInstance(vehicleRepositoryProvider.get(), expenseRepositoryProvider.get());
  }

  public static AnalyticsViewModel_Factory create(
      Provider<VehicleRepository> vehicleRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider) {
    return new AnalyticsViewModel_Factory(vehicleRepositoryProvider, expenseRepositoryProvider);
  }

  public static AnalyticsViewModel newInstance(VehicleRepository vehicleRepository,
      ExpenseRepository expenseRepository) {
    return new AnalyticsViewModel(vehicleRepository, expenseRepository);
  }
}
