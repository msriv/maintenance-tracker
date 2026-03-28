package com.moto.tracker.data.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.moto.tracker.data.local.dao.ServiceAppointmentDao;
import com.moto.tracker.data.local.dao.VehicleDao;
import dagger.internal.DaggerGenerated;
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
public final class AppointmentReminderWorker_Factory {
  private final Provider<ServiceAppointmentDao> appointmentDaoProvider;

  private final Provider<VehicleDao> vehicleDaoProvider;

  public AppointmentReminderWorker_Factory(Provider<ServiceAppointmentDao> appointmentDaoProvider,
      Provider<VehicleDao> vehicleDaoProvider) {
    this.appointmentDaoProvider = appointmentDaoProvider;
    this.vehicleDaoProvider = vehicleDaoProvider;
  }

  public AppointmentReminderWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, appointmentDaoProvider.get(), vehicleDaoProvider.get());
  }

  public static AppointmentReminderWorker_Factory create(
      Provider<ServiceAppointmentDao> appointmentDaoProvider,
      Provider<VehicleDao> vehicleDaoProvider) {
    return new AppointmentReminderWorker_Factory(appointmentDaoProvider, vehicleDaoProvider);
  }

  public static AppointmentReminderWorker newInstance(Context context, WorkerParameters params,
      ServiceAppointmentDao appointmentDao, VehicleDao vehicleDao) {
    return new AppointmentReminderWorker(context, params, appointmentDao, vehicleDao);
  }
}
