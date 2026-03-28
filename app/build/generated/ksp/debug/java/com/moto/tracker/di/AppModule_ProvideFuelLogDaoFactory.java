package com.moto.tracker.di;

import com.moto.tracker.data.local.AppDatabase;
import com.moto.tracker.data.local.dao.FuelLogDao;
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
public final class AppModule_ProvideFuelLogDaoFactory implements Factory<FuelLogDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideFuelLogDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public FuelLogDao get() {
    return provideFuelLogDao(dbProvider.get());
  }

  public static AppModule_ProvideFuelLogDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideFuelLogDaoFactory(dbProvider);
  }

  public static FuelLogDao provideFuelLogDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideFuelLogDao(db));
  }
}
