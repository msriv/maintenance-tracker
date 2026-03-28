package com.moto.tracker.ui.feature.appointment.addedit;

import androidx.lifecycle.SavedStateHandle;
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
public final class AddEditAppointmentViewModel_Factory implements Factory<AddEditAppointmentViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<AppointmentRepository> appointmentRepositoryProvider;

  private final Provider<NotificationScheduler> notificationSchedulerProvider;

  public AddEditAppointmentViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<AppointmentRepository> appointmentRepositoryProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.appointmentRepositoryProvider = appointmentRepositoryProvider;
    this.notificationSchedulerProvider = notificationSchedulerProvider;
  }

  @Override
  public AddEditAppointmentViewModel get() {
    return newInstance(savedStateHandleProvider.get(), appointmentRepositoryProvider.get(), notificationSchedulerProvider.get());
  }

  public static AddEditAppointmentViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<AppointmentRepository> appointmentRepositoryProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    return new AddEditAppointmentViewModel_Factory(savedStateHandleProvider, appointmentRepositoryProvider, notificationSchedulerProvider);
  }

  public static AddEditAppointmentViewModel newInstance(SavedStateHandle savedStateHandle,
      AppointmentRepository appointmentRepository, NotificationScheduler notificationScheduler) {
    return new AddEditAppointmentViewModel(savedStateHandle, appointmentRepository, notificationScheduler);
  }
}
