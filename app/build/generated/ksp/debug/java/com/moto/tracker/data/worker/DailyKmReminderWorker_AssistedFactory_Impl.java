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
public final class DailyKmReminderWorker_AssistedFactory_Impl implements DailyKmReminderWorker_AssistedFactory {
  private final DailyKmReminderWorker_Factory delegateFactory;

  DailyKmReminderWorker_AssistedFactory_Impl(DailyKmReminderWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public DailyKmReminderWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<DailyKmReminderWorker_AssistedFactory> create(
      DailyKmReminderWorker_Factory delegateFactory) {
    return InstanceFactory.create(new DailyKmReminderWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<DailyKmReminderWorker_AssistedFactory> createFactoryProvider(
      DailyKmReminderWorker_Factory delegateFactory) {
    return InstanceFactory.create(new DailyKmReminderWorker_AssistedFactory_Impl(delegateFactory));
  }
}
