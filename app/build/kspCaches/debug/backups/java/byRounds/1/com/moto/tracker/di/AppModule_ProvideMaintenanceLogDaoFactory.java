package com.moto.tracker.di;

import com.moto.tracker.data.local.AppDatabase;
import com.moto.tracker.data.local.dao.MaintenanceLogDao;
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
public final class AppModule_ProvideMaintenanceLogDaoFactory implements Factory<MaintenanceLogDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideMaintenanceLogDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public MaintenanceLogDao get() {
    return provideMaintenanceLogDao(dbProvider.get());
  }

  public static AppModule_ProvideMaintenanceLogDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideMaintenanceLogDaoFactory(dbProvider);
  }

  public static MaintenanceLogDao provideMaintenanceLogDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideMaintenanceLogDao(db));
  }
}
