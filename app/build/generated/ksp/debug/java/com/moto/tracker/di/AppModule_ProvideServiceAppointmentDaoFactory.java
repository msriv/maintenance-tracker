package com.moto.tracker.di;

import com.moto.tracker.data.local.AppDatabase;
import com.moto.tracker.data.local.dao.ServiceAppointmentDao;
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
public final class AppModule_ProvideServiceAppointmentDaoFactory implements Factory<ServiceAppointmentDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideServiceAppointmentDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ServiceAppointmentDao get() {
    return provideServiceAppointmentDao(dbProvider.get());
  }

  public static AppModule_ProvideServiceAppointmentDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideServiceAppointmentDaoFactory(dbProvider);
  }

  public static ServiceAppointmentDao provideServiceAppointmentDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideServiceAppointmentDao(db));
  }
}
