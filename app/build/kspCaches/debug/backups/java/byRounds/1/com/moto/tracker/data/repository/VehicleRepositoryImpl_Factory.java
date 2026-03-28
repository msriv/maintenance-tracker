package com.moto.tracker.data.repository;

import com.moto.tracker.data.local.dao.VehicleDao;
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
public final class VehicleRepositoryImpl_Factory implements Factory<VehicleRepositoryImpl> {
  private final Provider<VehicleDao> daoProvider;

  public VehicleRepositoryImpl_Factory(Provider<VehicleDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public VehicleRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static VehicleRepositoryImpl_Factory create(Provider<VehicleDao> daoProvider) {
    return new VehicleRepositoryImpl_Factory(daoProvider);
  }

  public static VehicleRepositoryImpl newInstance(VehicleDao dao) {
    return new VehicleRepositoryImpl(dao);
  }
}
