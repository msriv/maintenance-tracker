package com.moto.tracker.data.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.moto.tracker.data.local.dao.KmLogDao;
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
public final class DailyKmReminderWorker_Factory {
  private final Provider<VehicleDao> vehicleDaoProvider;

  private final Provider<KmLogDao> kmLogDaoProvider;

  public DailyKmReminderWorker_Factory(Provider<VehicleDao> vehicleDaoProvider,
      Provider<KmLogDao> kmLogDaoProvider) {
    this.vehicleDaoProvider = vehicleDaoProvider;
    this.kmLogDaoProvider = kmLogDaoProvider;
  }

  public DailyKmReminderWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, vehicleDaoProvider.get(), kmLogDaoProvider.get());
  }

  public static DailyKmReminderWorker_Factory create(Provider<VehicleDao> vehicleDaoProvider,
      Provider<KmLogDao> kmLogDaoProvider) {
    return new DailyKmReminderWorker_Factory(vehicleDaoProvider, kmLogDaoProvider);
  }

  public static DailyKmReminderWorker newInstance(Context context, WorkerParameters params,
      VehicleDao vehicleDao, KmLogDao kmLogDao) {
    return new DailyKmReminderWorker(context, params, vehicleDao, kmLogDao);
  }
}
