package com.moto.tracker.di;

import com.moto.tracker.data.local.AppDatabase;
import com.moto.tracker.data.local.dao.ManufacturerScheduleDao;
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
public final class AppModule_ProvideManufacturerScheduleDaoFactory implements Factory<ManufacturerScheduleDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideManufacturerScheduleDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ManufacturerScheduleDao get() {
    return provideManufacturerScheduleDao(dbProvider.get());
  }

  public static AppModule_ProvideManufacturerScheduleDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideManufacturerScheduleDaoFactory(dbProvider);
  }

  public static ManufacturerScheduleDao provideManufacturerScheduleDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideManufacturerScheduleDao(db));
  }
}
