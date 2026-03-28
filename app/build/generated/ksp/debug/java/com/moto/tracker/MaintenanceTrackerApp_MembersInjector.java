package com.moto.tracker;

import androidx.hilt.work.HiltWorkerFactory;
import com.moto.tracker.data.local.ManufacturerScheduleSeeder;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MaintenanceTrackerApp_MembersInjector implements MembersInjector<MaintenanceTrackerApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  private final Provider<ManufacturerScheduleSeeder> manufacturerScheduleSeederProvider;

  public MaintenanceTrackerApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<ManufacturerScheduleSeeder> manufacturerScheduleSeederProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
    this.manufacturerScheduleSeederProvider = manufacturerScheduleSeederProvider;
  }

  public static MembersInjector<MaintenanceTrackerApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<ManufacturerScheduleSeeder> manufacturerScheduleSeederProvider) {
    return new MaintenanceTrackerApp_MembersInjector(workerFactoryProvider, manufacturerScheduleSeederProvider);
  }

  @Override
  public void injectMembers(MaintenanceTrackerApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
    injectManufacturerScheduleSeeder(instance, manufacturerScheduleSeederProvider.get());
  }

  @InjectedFieldSignature("com.moto.tracker.MaintenanceTrackerApp.workerFactory")
  public static void injectWorkerFactory(MaintenanceTrackerApp instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }

  @InjectedFieldSignature("com.moto.tracker.MaintenanceTrackerApp.manufacturerScheduleSeeder")
  public static void injectManufacturerScheduleSeeder(MaintenanceTrackerApp instance,
      ManufacturerScheduleSeeder manufacturerScheduleSeeder) {
    instance.manufacturerScheduleSeeder = manufacturerScheduleSeeder;
  }
}
