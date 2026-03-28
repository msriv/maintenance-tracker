package com.moto.tracker.data.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MaintenanceCheckWorker_AssistedFactory_Impl implements MaintenanceCheckWorker_AssistedFactory {
  private final MaintenanceCheckWorker_Factory delegateFactory;

  MaintenanceCheckWorker_AssistedFactory_Impl(MaintenanceCheckWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public MaintenanceCheckWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<MaintenanceCheckWorker_AssistedFactory> create(
      MaintenanceCheckWorker_Factory delegateFactory) {
    return InstanceFactory.create(new MaintenanceCheckWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<MaintenanceCheckWorker_AssistedFactory> createFactoryProvider(
      MaintenanceCheckWorker_Factory delegateFactory) {
    return InstanceFactory.create(new MaintenanceCheckWorker_AssistedFactory_Impl(delegateFactory));
  }
}
