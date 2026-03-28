package com.moto.tracker.data.repository;

import com.moto.tracker.data.local.dao.FuelLogDao;
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
public final class FuelRepositoryImpl_Factory implements Factory<FuelRepositoryImpl> {
  private final Provider<FuelLogDao> daoProvider;

  public FuelRepositoryImpl_Factory(Provider<FuelLogDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public FuelRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static FuelRepositoryImpl_Factory create(Provider<FuelLogDao> daoProvider) {
    return new FuelRepositoryImpl_Factory(daoProvider);
  }

  public static FuelRepositoryImpl newInstance(FuelLogDao dao) {
    return new FuelRepositoryImpl(dao);
  }
}
