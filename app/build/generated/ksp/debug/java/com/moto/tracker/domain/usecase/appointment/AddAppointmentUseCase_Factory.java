package com.moto.tracker.domain.usecase.appointment;

import com.moto.tracker.domain.repository.AppointmentRepository;
import com.moto.tracker.domain.repository.NotificationScheduler;
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
public final class AddAppointmentUseCase_Factory implements Factory<AddAppointmentUseCase> {
  private final Provider<AppointmentRepository> repositoryProvider;

  private final Provider<NotificationScheduler> notificationSchedulerProvider;

  public AddAppointmentUseCase_Factory(Provider<AppointmentRepository> repositoryProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    this.repositoryProvider = repositoryProvider;
    this.notificationSchedulerProvider = notificationSchedulerProvider;
  }

  @Override
  public AddAppointmentUseCase get() {
    return newInstance(repositoryProvider.get(), notificationSchedulerProvider.get());
  }

  public static AddAppointmentUseCase_Factory create(
      Provider<AppointmentRepository> repositoryProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    return new AddAppointmentUseCase_Factory(repositoryProvider, notificationSchedulerProvider);
  }

  public static AddAppointmentUseCase newInstance(AppointmentRepository repository,
      NotificationScheduler notificationScheduler) {
    return new AddAppointmentUseCase(repository, notificationScheduler);
  }
}
