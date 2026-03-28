package com.moto.tracker.data.repository;

import com.moto.tracker.data.local.dao.KmLogDao;
import com.moto.tracker.data.local.dao.MaintenanceLogDao;
import com.moto.tracker.data.local.dao.MaintenanceTaskDao;
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
public final class MaintenanceRepositoryImpl_Factory implements Factory<MaintenanceRepositoryImpl> {
  private final Provider<MaintenanceTaskDao> taskDaoProvider;

  private final Provider<MaintenanceLogDao> logDaoProvider;

  private final Provider<KmLogDao> kmLogDaoProvider;

  public MaintenanceRepositoryImpl_Factory(Provider<MaintenanceTaskDao> taskDaoProvider,
      Provider<MaintenanceLogDao> logDaoProvider, Provider<KmLogDao> kmLogDaoProvider) {
    this.taskDaoProvider = taskDaoProvider;
    this.logDaoProvider = logDaoProvider;
    this.kmLogDaoProvider = kmLogDaoProvider;
  }

  @Override
  public MaintenanceRepositoryImpl get() {
    return newInstance(taskDaoProvider.get(), logDaoProvider.get(), kmLogDaoProvider.get());
  }

  public static MaintenanceRepositoryImpl_Factory create(
      Provider<MaintenanceTaskDao> taskDaoProvider, Provider<MaintenanceLogDao> logDaoProvider,
      Provider<KmLogDao> kmLogDaoProvider) {
    return new MaintenanceRepositoryImpl_Factory(taskDaoProvider, logDaoProvider, kmLogDaoProvider);
  }

  public static MaintenanceRepositoryImpl newInstance(MaintenanceTaskDao taskDao,
      MaintenanceLogDao logDao, KmLogDao kmLogDao) {
    return new MaintenanceRepositoryImpl(taskDao, logDao, kmLogDao);
  }
}
