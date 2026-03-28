package com.moto.tracker.data.repository;

import android.content.Context;
import androidx.work.WorkManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class NotificationSchedulerImpl_Factory implements Factory<NotificationSchedulerImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<WorkManager> workManagerProvider;

  public NotificationSchedulerImpl_Factory(Provider<Context> contextProvider,
      Provider<WorkManager> workManagerProvider) {
    this.contextProvider = contextProvider;
    this.workManagerProvider = workManagerProvider;
  }

  @Override
  public NotificationSchedulerImpl get() {
    return newInstance(contextProvider.get(), workManagerProvider.get());
  }

  public static NotificationSchedulerImpl_Factory create(Provider<Context> contextProvider,
      Provider<WorkManager> workManagerProvider) {
    return new NotificationSchedulerImpl_Factory(contextProvider, workManagerProvider);
  }

  public static NotificationSchedulerImpl newInstance(Context context, WorkManager workManager) {
    return new NotificationSchedulerImpl(context, workManager);
  }
}
