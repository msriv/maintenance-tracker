package com.moto.tracker.di;

import com.moto.tracker.data.local.AppDatabase;
import com.moto.tracker.data.local.dao.VehicleDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideVehicleDaoFactory implements Factory<VehicleDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideVehicleDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public VehicleDao get() {
    return provideVehicleDao(dbProvider.get());
  }

  public static AppModule_ProvideVehicleDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideVehicleDaoFactory(dbProvider);
  }

  public static VehicleDao provideVehicleDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideVehicleDao(db));
  }
}
