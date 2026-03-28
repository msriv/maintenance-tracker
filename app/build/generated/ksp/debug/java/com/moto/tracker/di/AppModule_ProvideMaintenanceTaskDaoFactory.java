package com.moto.tracker.di;

import com.moto.tracker.data.local.AppDatabase;
import com.moto.tracker.data.local.dao.MaintenanceTaskDao;
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
public final class AppModule_ProvideMaintenanceTaskDaoFactory implements Factory<MaintenanceTaskDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideMaintenanceTaskDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public MaintenanceTaskDao get() {
    return provideMaintenanceTaskDao(dbProvider.get());
  }

  public static AppModule_ProvideMaintenanceTaskDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideMaintenanceTaskDaoFactory(dbProvider);
  }

  public static MaintenanceTaskDao provideMaintenanceTaskDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideMaintenanceTaskDao(db));
  }
}
