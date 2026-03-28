package com.moto.tracker.data.repository;

import com.moto.tracker.data.local.dao.ServiceAppointmentDao;
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
public final class AppointmentRepositoryImpl_Factory implements Factory<AppointmentRepositoryImpl> {
  private final Provider<ServiceAppointmentDao> daoProvider;

  public AppointmentRepositoryImpl_Factory(Provider<ServiceAppointmentDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public AppointmentRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static AppointmentRepositoryImpl_Factory create(
      Provider<ServiceAppointmentDao> daoProvider) {
    return new AppointmentRepositoryImpl_Factory(daoProvider);
  }

  public static AppointmentRepositoryImpl newInstance(ServiceAppointmentDao dao) {
    return new AppointmentRepositoryImpl(dao);
  }
}
