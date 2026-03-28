package com.moto.tracker.ui.feature.appointment.list;

import androidx.lifecycle.SavedStateHandle;
import com.moto.tracker.domain.repository.AppointmentRepository;
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
public final class AppointmentListViewModel_Factory implements Factory<AppointmentListViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<AppointmentRepository> appointmentRepositoryProvider;

  public AppointmentListViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<AppointmentRepository> appointmentRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.appointmentRepositoryProvider = appointmentRepositoryProvider;
  }

  @Override
  public AppointmentListViewModel get() {
    return newInstance(savedStateHandleProvider.get(), appointmentRepositoryProvider.get());
  }

  public static AppointmentListViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<AppointmentRepository> appointmentRepositoryProvider) {
    return new AppointmentListViewModel_Factory(savedStateHandleProvider, appointmentRepositoryProvider);
  }

  public static AppointmentListViewModel newInstance(SavedStateHandle savedStateHandle,
      AppointmentRepository appointmentRepository) {
    return new AppointmentListViewModel(savedStateHandle, appointmentRepository);
  }
}
