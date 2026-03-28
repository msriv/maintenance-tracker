package com.moto.tracker.data.repository;

import com.moto.tracker.data.local.dao.ManufacturerScheduleDao;
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
public final class ManufacturerScheduleRepositoryImpl_Factory implements Factory<ManufacturerScheduleRepositoryImpl> {
  private final Provider<ManufacturerScheduleDao> daoProvider;

  public ManufacturerScheduleRepositoryImpl_Factory(Provider<ManufacturerScheduleDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public ManufacturerScheduleRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static ManufacturerScheduleRepositoryImpl_Factory create(
      Provider<ManufacturerScheduleDao> daoProvider) {
    return new ManufacturerScheduleRepositoryImpl_Factory(daoProvider);
  }

  public static ManufacturerScheduleRepositoryImpl newInstance(ManufacturerScheduleDao dao) {
    return new ManufacturerScheduleRepositoryImpl(dao);
  }
}
