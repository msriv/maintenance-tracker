package com.moto.tracker;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkManager;
import androidx.work.WorkerParameters;
import com.moto.tracker.data.local.AppDatabase;
import com.moto.tracker.data.local.ManufacturerScheduleSeeder;
import com.moto.tracker.data.local.dao.DocumentDao;
import com.moto.tracker.data.local.dao.ExpenseViewDao;
import com.moto.tracker.data.local.dao.FuelLogDao;
import com.moto.tracker.data.local.dao.KmLogDao;
import com.moto.tracker.data.local.dao.MaintenanceLogDao;
import com.moto.tracker.data.local.dao.MaintenanceTaskDao;
import com.moto.tracker.data.local.dao.ManufacturerScheduleDao;
import com.moto.tracker.data.local.dao.ServiceAppointmentDao;
import com.moto.tracker.data.local.dao.VehicleDao;
import com.moto.tracker.data.repository.AppointmentRepositoryImpl;
import com.moto.tracker.data.repository.DocumentRepositoryImpl;
import com.moto.tracker.data.repository.ExpenseRepositoryImpl;
import com.moto.tracker.data.repository.ExportRepositoryImpl;
import com.moto.tracker.data.repository.FuelRepositoryImpl;
import com.moto.tracker.data.repository.MaintenanceRepositoryImpl;
import com.moto.tracker.data.repository.ManufacturerScheduleRepositoryImpl;
import com.moto.tracker.data.repository.NotificationSchedulerImpl;
import com.moto.tracker.data.repository.UserPreferencesRepositoryImpl;
import com.moto.tracker.data.repository.VehicleRepositoryImpl;
import com.moto.tracker.data.worker.AppointmentReminderWorker;
import com.moto.tracker.data.worker.AppointmentReminderWorker_AssistedFactory;
import com.moto.tracker.data.worker.BootReceiver;
import com.moto.tracker.data.worker.BootReceiver_MembersInjector;
import com.moto.tracker.data.worker.DailyKmReminderWorker;
import com.moto.tracker.data.worker.DailyKmReminderWorker_AssistedFactory;
import com.moto.tracker.data.worker.MaintenanceCheckWorker;
import com.moto.tracker.data.worker.MaintenanceCheckWorker_AssistedFactory;
import com.moto.tracker.di.AppModule_ProvideAppDatabaseFactory;
import com.moto.tracker.di.AppModule_ProvideDataStoreFactory;
import com.moto.tracker.di.AppModule_ProvideDocumentDaoFactory;
import com.moto.tracker.di.AppModule_ProvideExpenseViewDaoFactory;
import com.moto.tracker.di.AppModule_ProvideFuelLogDaoFactory;
import com.moto.tracker.di.AppModule_ProvideKmLogDaoFactory;
import com.moto.tracker.di.AppModule_ProvideMaintenanceLogDaoFactory;
import com.moto.tracker.di.AppModule_ProvideMaintenanceTaskDaoFactory;
import com.moto.tracker.di.AppModule_ProvideManufacturerScheduleDaoFactory;
import com.moto.tracker.di.AppModule_ProvideServiceAppointmentDaoFactory;
import com.moto.tracker.di.AppModule_ProvideVehicleDaoFactory;
import com.moto.tracker.di.WorkerModule_ProvideWorkManagerFactory;
import com.moto.tracker.domain.repository.AppointmentRepository;
import com.moto.tracker.domain.repository.DocumentRepository;
import com.moto.tracker.domain.repository.ExpenseRepository;
import com.moto.tracker.domain.repository.ExportRepository;
import com.moto.tracker.domain.repository.FuelRepository;
import com.moto.tracker.domain.repository.MaintenanceRepository;
import com.moto.tracker.domain.repository.ManufacturerScheduleRepository;
import com.moto.tracker.domain.repository.NotificationScheduler;
import com.moto.tracker.domain.repository.UserPreferencesRepository;
import com.moto.tracker.domain.repository.VehicleRepository;
import com.moto.tracker.domain.usecase.document.AddDocumentUseCase;
import com.moto.tracker.domain.usecase.fuel.AddFuelLogUseCase;
import com.moto.tracker.domain.usecase.fuel.ComputeKmPerLiterUseCase;
import com.moto.tracker.domain.usecase.kmlog.LogDailyKmUseCase;
import com.moto.tracker.domain.usecase.maintenance.AddMaintenanceTaskUseCase;
import com.moto.tracker.domain.usecase.maintenance.ComputeNextDueDateUseCase;
import com.moto.tracker.domain.usecase.maintenance.GetMaintenanceTasksUseCase;
import com.moto.tracker.domain.usecase.maintenance.LogMaintenanceUseCase;
import com.moto.tracker.domain.usecase.vehicle.AddVehicleUseCase;
import com.moto.tracker.domain.usecase.vehicle.DeleteVehicleUseCase;
import com.moto.tracker.domain.usecase.vehicle.GetVehiclesUseCase;
import com.moto.tracker.domain.usecase.vehicle.UpdateVehicleUseCase;
import com.moto.tracker.ui.feature.analytics.AnalyticsViewModel;
import com.moto.tracker.ui.feature.analytics.AnalyticsViewModel_HiltModules;
import com.moto.tracker.ui.feature.appointment.addedit.AddEditAppointmentViewModel;
import com.moto.tracker.ui.feature.appointment.addedit.AddEditAppointmentViewModel_HiltModules;
import com.moto.tracker.ui.feature.appointment.list.AppointmentListViewModel;
import com.moto.tracker.ui.feature.appointment.list.AppointmentListViewModel_HiltModules;
import com.moto.tracker.ui.feature.document.add.AddDocumentViewModel;
import com.moto.tracker.ui.feature.document.add.AddDocumentViewModel_HiltModules;
import com.moto.tracker.ui.feature.document.detail.DocumentDetailViewModel;
import com.moto.tracker.ui.feature.document.detail.DocumentDetailViewModel_HiltModules;
import com.moto.tracker.ui.feature.document.list.DocumentListViewModel;
import com.moto.tracker.ui.feature.document.list.DocumentListViewModel_HiltModules;
import com.moto.tracker.ui.feature.export.ExportViewModel;
import com.moto.tracker.ui.feature.export.ExportViewModel_HiltModules;
import com.moto.tracker.ui.feature.fuel.add.AddFuelLogViewModel;
import com.moto.tracker.ui.feature.fuel.add.AddFuelLogViewModel_HiltModules;
import com.moto.tracker.ui.feature.fuel.list.FuelLogListViewModel;
import com.moto.tracker.ui.feature.fuel.list.FuelLogListViewModel_HiltModules;
import com.moto.tracker.ui.feature.maintenance.addedit.AddEditMaintenanceTaskViewModel;
import com.moto.tracker.ui.feature.maintenance.addedit.AddEditMaintenanceTaskViewModel_HiltModules;
import com.moto.tracker.ui.feature.maintenance.kmlog.KmLogViewModel;
import com.moto.tracker.ui.feature.maintenance.kmlog.KmLogViewModel_HiltModules;
import com.moto.tracker.ui.feature.maintenance.list.MaintenanceListViewModel;
import com.moto.tracker.ui.feature.maintenance.list.MaintenanceListViewModel_HiltModules;
import com.moto.tracker.ui.feature.maintenance.log.LogMaintenanceViewModel;
import com.moto.tracker.ui.feature.maintenance.log.LogMaintenanceViewModel_HiltModules;
import com.moto.tracker.ui.feature.maintenance.schedule.ManufacturerScheduleViewModel;
import com.moto.tracker.ui.feature.maintenance.schedule.ManufacturerScheduleViewModel_HiltModules;
import com.moto.tracker.ui.feature.settings.SettingsViewModel;
import com.moto.tracker.ui.feature.settings.SettingsViewModel_HiltModules;
import com.moto.tracker.ui.feature.vehicle.addedit.AddEditVehicleViewModel;
import com.moto.tracker.ui.feature.vehicle.addedit.AddEditVehicleViewModel_HiltModules;
import com.moto.tracker.ui.feature.vehicle.detail.VehicleDetailViewModel;
import com.moto.tracker.ui.feature.vehicle.detail.VehicleDetailViewModel_HiltModules;
import com.moto.tracker.ui.feature.vehicle.list.VehicleListViewModel;
import com.moto.tracker.ui.feature.vehicle.list.VehicleListViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SingleCheck;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerMaintenanceTrackerApp_HiltComponents_SingletonC {
  private DaggerMaintenanceTrackerApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public MaintenanceTrackerApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements MaintenanceTrackerApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public MaintenanceTrackerApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements MaintenanceTrackerApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public MaintenanceTrackerApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements MaintenanceTrackerApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public MaintenanceTrackerApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements MaintenanceTrackerApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MaintenanceTrackerApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements MaintenanceTrackerApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MaintenanceTrackerApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements MaintenanceTrackerApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public MaintenanceTrackerApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements MaintenanceTrackerApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public MaintenanceTrackerApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends MaintenanceTrackerApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends MaintenanceTrackerApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends MaintenanceTrackerApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends MaintenanceTrackerApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(18).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_document_add_AddDocumentViewModel, AddDocumentViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_appointment_addedit_AddEditAppointmentViewModel, AddEditAppointmentViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_maintenance_addedit_AddEditMaintenanceTaskViewModel, AddEditMaintenanceTaskViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_vehicle_addedit_AddEditVehicleViewModel, AddEditVehicleViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_fuel_add_AddFuelLogViewModel, AddFuelLogViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_analytics_AnalyticsViewModel, AnalyticsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_appointment_list_AppointmentListViewModel, AppointmentListViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_document_detail_DocumentDetailViewModel, DocumentDetailViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_document_list_DocumentListViewModel, DocumentListViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_export_ExportViewModel, ExportViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_fuel_list_FuelLogListViewModel, FuelLogListViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_maintenance_kmlog_KmLogViewModel, KmLogViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_maintenance_log_LogMaintenanceViewModel, LogMaintenanceViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_maintenance_list_MaintenanceListViewModel, MaintenanceListViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_maintenance_schedule_ManufacturerScheduleViewModel, ManufacturerScheduleViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_settings_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_vehicle_detail_VehicleDetailViewModel, VehicleDetailViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_vehicle_list_VehicleListViewModel, VehicleListViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_moto_tracker_ui_feature_appointment_list_AppointmentListViewModel = "com.moto.tracker.ui.feature.appointment.list.AppointmentListViewModel";

      static String com_moto_tracker_ui_feature_export_ExportViewModel = "com.moto.tracker.ui.feature.export.ExportViewModel";

      static String com_moto_tracker_ui_feature_document_list_DocumentListViewModel = "com.moto.tracker.ui.feature.document.list.DocumentListViewModel";

      static String com_moto_tracker_ui_feature_appointment_addedit_AddEditAppointmentViewModel = "com.moto.tracker.ui.feature.appointment.addedit.AddEditAppointmentViewModel";

      static String com_moto_tracker_ui_feature_document_add_AddDocumentViewModel = "com.moto.tracker.ui.feature.document.add.AddDocumentViewModel";

      static String com_moto_tracker_ui_feature_maintenance_log_LogMaintenanceViewModel = "com.moto.tracker.ui.feature.maintenance.log.LogMaintenanceViewModel";

      static String com_moto_tracker_ui_feature_maintenance_addedit_AddEditMaintenanceTaskViewModel = "com.moto.tracker.ui.feature.maintenance.addedit.AddEditMaintenanceTaskViewModel";

      static String com_moto_tracker_ui_feature_vehicle_addedit_AddEditVehicleViewModel = "com.moto.tracker.ui.feature.vehicle.addedit.AddEditVehicleViewModel";

      static String com_moto_tracker_ui_feature_analytics_AnalyticsViewModel = "com.moto.tracker.ui.feature.analytics.AnalyticsViewModel";

      static String com_moto_tracker_ui_feature_vehicle_detail_VehicleDetailViewModel = "com.moto.tracker.ui.feature.vehicle.detail.VehicleDetailViewModel";

      static String com_moto_tracker_ui_feature_maintenance_list_MaintenanceListViewModel = "com.moto.tracker.ui.feature.maintenance.list.MaintenanceListViewModel";

      static String com_moto_tracker_ui_feature_maintenance_kmlog_KmLogViewModel = "com.moto.tracker.ui.feature.maintenance.kmlog.KmLogViewModel";

      static String com_moto_tracker_ui_feature_maintenance_schedule_ManufacturerScheduleViewModel = "com.moto.tracker.ui.feature.maintenance.schedule.ManufacturerScheduleViewModel";

      static String com_moto_tracker_ui_feature_document_detail_DocumentDetailViewModel = "com.moto.tracker.ui.feature.document.detail.DocumentDetailViewModel";

      static String com_moto_tracker_ui_feature_fuel_add_AddFuelLogViewModel = "com.moto.tracker.ui.feature.fuel.add.AddFuelLogViewModel";

      static String com_moto_tracker_ui_feature_vehicle_list_VehicleListViewModel = "com.moto.tracker.ui.feature.vehicle.list.VehicleListViewModel";

      static String com_moto_tracker_ui_feature_fuel_list_FuelLogListViewModel = "com.moto.tracker.ui.feature.fuel.list.FuelLogListViewModel";

      static String com_moto_tracker_ui_feature_settings_SettingsViewModel = "com.moto.tracker.ui.feature.settings.SettingsViewModel";

      @KeepFieldType
      AppointmentListViewModel com_moto_tracker_ui_feature_appointment_list_AppointmentListViewModel2;

      @KeepFieldType
      ExportViewModel com_moto_tracker_ui_feature_export_ExportViewModel2;

      @KeepFieldType
      DocumentListViewModel com_moto_tracker_ui_feature_document_list_DocumentListViewModel2;

      @KeepFieldType
      AddEditAppointmentViewModel com_moto_tracker_ui_feature_appointment_addedit_AddEditAppointmentViewModel2;

      @KeepFieldType
      AddDocumentViewModel com_moto_tracker_ui_feature_document_add_AddDocumentViewModel2;

      @KeepFieldType
      LogMaintenanceViewModel com_moto_tracker_ui_feature_maintenance_log_LogMaintenanceViewModel2;

      @KeepFieldType
      AddEditMaintenanceTaskViewModel com_moto_tracker_ui_feature_maintenance_addedit_AddEditMaintenanceTaskViewModel2;

      @KeepFieldType
      AddEditVehicleViewModel com_moto_tracker_ui_feature_vehicle_addedit_AddEditVehicleViewModel2;

      @KeepFieldType
      AnalyticsViewModel com_moto_tracker_ui_feature_analytics_AnalyticsViewModel2;

      @KeepFieldType
      VehicleDetailViewModel com_moto_tracker_ui_feature_vehicle_detail_VehicleDetailViewModel2;

      @KeepFieldType
      MaintenanceListViewModel com_moto_tracker_ui_feature_maintenance_list_MaintenanceListViewModel2;

      @KeepFieldType
      KmLogViewModel com_moto_tracker_ui_feature_maintenance_kmlog_KmLogViewModel2;

      @KeepFieldType
      ManufacturerScheduleViewModel com_moto_tracker_ui_feature_maintenance_schedule_ManufacturerScheduleViewModel2;

      @KeepFieldType
      DocumentDetailViewModel com_moto_tracker_ui_feature_document_detail_DocumentDetailViewModel2;

      @KeepFieldType
      AddFuelLogViewModel com_moto_tracker_ui_feature_fuel_add_AddFuelLogViewModel2;

      @KeepFieldType
      VehicleListViewModel com_moto_tracker_ui_feature_vehicle_list_VehicleListViewModel2;

      @KeepFieldType
      FuelLogListViewModel com_moto_tracker_ui_feature_fuel_list_FuelLogListViewModel2;

      @KeepFieldType
      SettingsViewModel com_moto_tracker_ui_feature_settings_SettingsViewModel2;
    }
  }

  private static final class ViewModelCImpl extends MaintenanceTrackerApp_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AddDocumentViewModel> addDocumentViewModelProvider;

    private Provider<AddEditAppointmentViewModel> addEditAppointmentViewModelProvider;

    private Provider<AddEditMaintenanceTaskViewModel> addEditMaintenanceTaskViewModelProvider;

    private Provider<AddEditVehicleViewModel> addEditVehicleViewModelProvider;

    private Provider<AddFuelLogViewModel> addFuelLogViewModelProvider;

    private Provider<AnalyticsViewModel> analyticsViewModelProvider;

    private Provider<AppointmentListViewModel> appointmentListViewModelProvider;

    private Provider<DocumentDetailViewModel> documentDetailViewModelProvider;

    private Provider<DocumentListViewModel> documentListViewModelProvider;

    private Provider<ExportViewModel> exportViewModelProvider;

    private Provider<FuelLogListViewModel> fuelLogListViewModelProvider;

    private Provider<KmLogViewModel> kmLogViewModelProvider;

    private Provider<LogMaintenanceViewModel> logMaintenanceViewModelProvider;

    private Provider<MaintenanceListViewModel> maintenanceListViewModelProvider;

    private Provider<ManufacturerScheduleViewModel> manufacturerScheduleViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<VehicleDetailViewModel> vehicleDetailViewModelProvider;

    private Provider<VehicleListViewModel> vehicleListViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private AddDocumentUseCase addDocumentUseCase() {
      return new AddDocumentUseCase(singletonCImpl.bindDocumentRepositoryProvider.get());
    }

    private AddMaintenanceTaskUseCase addMaintenanceTaskUseCase() {
      return new AddMaintenanceTaskUseCase(singletonCImpl.bindMaintenanceRepositoryProvider.get(), singletonCImpl.bindVehicleRepositoryProvider.get(), new ComputeNextDueDateUseCase());
    }

    private AddVehicleUseCase addVehicleUseCase() {
      return new AddVehicleUseCase(singletonCImpl.bindVehicleRepositoryProvider.get());
    }

    private UpdateVehicleUseCase updateVehicleUseCase() {
      return new UpdateVehicleUseCase(singletonCImpl.bindVehicleRepositoryProvider.get());
    }

    private ComputeKmPerLiterUseCase computeKmPerLiterUseCase() {
      return new ComputeKmPerLiterUseCase(singletonCImpl.bindFuelRepositoryProvider.get());
    }

    private AddFuelLogUseCase addFuelLogUseCase() {
      return new AddFuelLogUseCase(singletonCImpl.bindFuelRepositoryProvider.get(), singletonCImpl.bindVehicleRepositoryProvider.get(), computeKmPerLiterUseCase());
    }

    private LogDailyKmUseCase logDailyKmUseCase() {
      return new LogDailyKmUseCase(singletonCImpl.bindMaintenanceRepositoryProvider.get(), singletonCImpl.bindVehicleRepositoryProvider.get());
    }

    private LogMaintenanceUseCase logMaintenanceUseCase() {
      return new LogMaintenanceUseCase(singletonCImpl.bindMaintenanceRepositoryProvider.get(), singletonCImpl.bindVehicleRepositoryProvider.get(), new ComputeNextDueDateUseCase());
    }

    private GetMaintenanceTasksUseCase getMaintenanceTasksUseCase() {
      return new GetMaintenanceTasksUseCase(singletonCImpl.bindMaintenanceRepositoryProvider.get());
    }

    private GetVehiclesUseCase getVehiclesUseCase() {
      return new GetVehiclesUseCase(singletonCImpl.bindVehicleRepositoryProvider.get());
    }

    private DeleteVehicleUseCase deleteVehicleUseCase() {
      return new DeleteVehicleUseCase(singletonCImpl.bindVehicleRepositoryProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.addDocumentViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.addEditAppointmentViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.addEditMaintenanceTaskViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.addEditVehicleViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.addFuelLogViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.analyticsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.appointmentListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.documentDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.documentListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.exportViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.fuelLogListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
      this.kmLogViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 11);
      this.logMaintenanceViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 12);
      this.maintenanceListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 13);
      this.manufacturerScheduleViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 14);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 15);
      this.vehicleDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 16);
      this.vehicleListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 17);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(18).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_document_add_AddDocumentViewModel, ((Provider) addDocumentViewModelProvider)).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_appointment_addedit_AddEditAppointmentViewModel, ((Provider) addEditAppointmentViewModelProvider)).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_maintenance_addedit_AddEditMaintenanceTaskViewModel, ((Provider) addEditMaintenanceTaskViewModelProvider)).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_vehicle_addedit_AddEditVehicleViewModel, ((Provider) addEditVehicleViewModelProvider)).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_fuel_add_AddFuelLogViewModel, ((Provider) addFuelLogViewModelProvider)).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_analytics_AnalyticsViewModel, ((Provider) analyticsViewModelProvider)).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_appointment_list_AppointmentListViewModel, ((Provider) appointmentListViewModelProvider)).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_document_detail_DocumentDetailViewModel, ((Provider) documentDetailViewModelProvider)).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_document_list_DocumentListViewModel, ((Provider) documentListViewModelProvider)).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_export_ExportViewModel, ((Provider) exportViewModelProvider)).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_fuel_list_FuelLogListViewModel, ((Provider) fuelLogListViewModelProvider)).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_maintenance_kmlog_KmLogViewModel, ((Provider) kmLogViewModelProvider)).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_maintenance_log_LogMaintenanceViewModel, ((Provider) logMaintenanceViewModelProvider)).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_maintenance_list_MaintenanceListViewModel, ((Provider) maintenanceListViewModelProvider)).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_maintenance_schedule_ManufacturerScheduleViewModel, ((Provider) manufacturerScheduleViewModelProvider)).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_settings_SettingsViewModel, ((Provider) settingsViewModelProvider)).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_vehicle_detail_VehicleDetailViewModel, ((Provider) vehicleDetailViewModelProvider)).put(LazyClassKeyProvider.com_moto_tracker_ui_feature_vehicle_list_VehicleListViewModel, ((Provider) vehicleListViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_moto_tracker_ui_feature_maintenance_kmlog_KmLogViewModel = "com.moto.tracker.ui.feature.maintenance.kmlog.KmLogViewModel";

      static String com_moto_tracker_ui_feature_fuel_add_AddFuelLogViewModel = "com.moto.tracker.ui.feature.fuel.add.AddFuelLogViewModel";

      static String com_moto_tracker_ui_feature_analytics_AnalyticsViewModel = "com.moto.tracker.ui.feature.analytics.AnalyticsViewModel";

      static String com_moto_tracker_ui_feature_vehicle_addedit_AddEditVehicleViewModel = "com.moto.tracker.ui.feature.vehicle.addedit.AddEditVehicleViewModel";

      static String com_moto_tracker_ui_feature_appointment_list_AppointmentListViewModel = "com.moto.tracker.ui.feature.appointment.list.AppointmentListViewModel";

      static String com_moto_tracker_ui_feature_fuel_list_FuelLogListViewModel = "com.moto.tracker.ui.feature.fuel.list.FuelLogListViewModel";

      static String com_moto_tracker_ui_feature_appointment_addedit_AddEditAppointmentViewModel = "com.moto.tracker.ui.feature.appointment.addedit.AddEditAppointmentViewModel";

      static String com_moto_tracker_ui_feature_maintenance_list_MaintenanceListViewModel = "com.moto.tracker.ui.feature.maintenance.list.MaintenanceListViewModel";

      static String com_moto_tracker_ui_feature_document_detail_DocumentDetailViewModel = "com.moto.tracker.ui.feature.document.detail.DocumentDetailViewModel";

      static String com_moto_tracker_ui_feature_vehicle_list_VehicleListViewModel = "com.moto.tracker.ui.feature.vehicle.list.VehicleListViewModel";

      static String com_moto_tracker_ui_feature_document_add_AddDocumentViewModel = "com.moto.tracker.ui.feature.document.add.AddDocumentViewModel";

      static String com_moto_tracker_ui_feature_export_ExportViewModel = "com.moto.tracker.ui.feature.export.ExportViewModel";

      static String com_moto_tracker_ui_feature_settings_SettingsViewModel = "com.moto.tracker.ui.feature.settings.SettingsViewModel";

      static String com_moto_tracker_ui_feature_maintenance_log_LogMaintenanceViewModel = "com.moto.tracker.ui.feature.maintenance.log.LogMaintenanceViewModel";

      static String com_moto_tracker_ui_feature_maintenance_addedit_AddEditMaintenanceTaskViewModel = "com.moto.tracker.ui.feature.maintenance.addedit.AddEditMaintenanceTaskViewModel";

      static String com_moto_tracker_ui_feature_maintenance_schedule_ManufacturerScheduleViewModel = "com.moto.tracker.ui.feature.maintenance.schedule.ManufacturerScheduleViewModel";

      static String com_moto_tracker_ui_feature_document_list_DocumentListViewModel = "com.moto.tracker.ui.feature.document.list.DocumentListViewModel";

      static String com_moto_tracker_ui_feature_vehicle_detail_VehicleDetailViewModel = "com.moto.tracker.ui.feature.vehicle.detail.VehicleDetailViewModel";

      @KeepFieldType
      KmLogViewModel com_moto_tracker_ui_feature_maintenance_kmlog_KmLogViewModel2;

      @KeepFieldType
      AddFuelLogViewModel com_moto_tracker_ui_feature_fuel_add_AddFuelLogViewModel2;

      @KeepFieldType
      AnalyticsViewModel com_moto_tracker_ui_feature_analytics_AnalyticsViewModel2;

      @KeepFieldType
      AddEditVehicleViewModel com_moto_tracker_ui_feature_vehicle_addedit_AddEditVehicleViewModel2;

      @KeepFieldType
      AppointmentListViewModel com_moto_tracker_ui_feature_appointment_list_AppointmentListViewModel2;

      @KeepFieldType
      FuelLogListViewModel com_moto_tracker_ui_feature_fuel_list_FuelLogListViewModel2;

      @KeepFieldType
      AddEditAppointmentViewModel com_moto_tracker_ui_feature_appointment_addedit_AddEditAppointmentViewModel2;

      @KeepFieldType
      MaintenanceListViewModel com_moto_tracker_ui_feature_maintenance_list_MaintenanceListViewModel2;

      @KeepFieldType
      DocumentDetailViewModel com_moto_tracker_ui_feature_document_detail_DocumentDetailViewModel2;

      @KeepFieldType
      VehicleListViewModel com_moto_tracker_ui_feature_vehicle_list_VehicleListViewModel2;

      @KeepFieldType
      AddDocumentViewModel com_moto_tracker_ui_feature_document_add_AddDocumentViewModel2;

      @KeepFieldType
      ExportViewModel com_moto_tracker_ui_feature_export_ExportViewModel2;

      @KeepFieldType
      SettingsViewModel com_moto_tracker_ui_feature_settings_SettingsViewModel2;

      @KeepFieldType
      LogMaintenanceViewModel com_moto_tracker_ui_feature_maintenance_log_LogMaintenanceViewModel2;

      @KeepFieldType
      AddEditMaintenanceTaskViewModel com_moto_tracker_ui_feature_maintenance_addedit_AddEditMaintenanceTaskViewModel2;

      @KeepFieldType
      ManufacturerScheduleViewModel com_moto_tracker_ui_feature_maintenance_schedule_ManufacturerScheduleViewModel2;

      @KeepFieldType
      DocumentListViewModel com_moto_tracker_ui_feature_document_list_DocumentListViewModel2;

      @KeepFieldType
      VehicleDetailViewModel com_moto_tracker_ui_feature_vehicle_detail_VehicleDetailViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.moto.tracker.ui.feature.document.add.AddDocumentViewModel 
          return (T) new AddDocumentViewModel(viewModelCImpl.savedStateHandle, ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), viewModelCImpl.addDocumentUseCase());

          case 1: // com.moto.tracker.ui.feature.appointment.addedit.AddEditAppointmentViewModel 
          return (T) new AddEditAppointmentViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.bindAppointmentRepositoryProvider.get(), singletonCImpl.bindNotificationSchedulerProvider.get());

          case 2: // com.moto.tracker.ui.feature.maintenance.addedit.AddEditMaintenanceTaskViewModel 
          return (T) new AddEditMaintenanceTaskViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.bindMaintenanceRepositoryProvider.get(), viewModelCImpl.addMaintenanceTaskUseCase());

          case 3: // com.moto.tracker.ui.feature.vehicle.addedit.AddEditVehicleViewModel 
          return (T) new AddEditVehicleViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.bindVehicleRepositoryProvider.get(), viewModelCImpl.addVehicleUseCase(), viewModelCImpl.updateVehicleUseCase());

          case 4: // com.moto.tracker.ui.feature.fuel.add.AddFuelLogViewModel 
          return (T) new AddFuelLogViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.bindVehicleRepositoryProvider.get(), viewModelCImpl.addFuelLogUseCase());

          case 5: // com.moto.tracker.ui.feature.analytics.AnalyticsViewModel 
          return (T) new AnalyticsViewModel(singletonCImpl.bindVehicleRepositoryProvider.get(), singletonCImpl.bindExpenseRepositoryProvider.get());

          case 6: // com.moto.tracker.ui.feature.appointment.list.AppointmentListViewModel 
          return (T) new AppointmentListViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.bindAppointmentRepositoryProvider.get());

          case 7: // com.moto.tracker.ui.feature.document.detail.DocumentDetailViewModel 
          return (T) new DocumentDetailViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.bindDocumentRepositoryProvider.get());

          case 8: // com.moto.tracker.ui.feature.document.list.DocumentListViewModel 
          return (T) new DocumentListViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.bindDocumentRepositoryProvider.get());

          case 9: // com.moto.tracker.ui.feature.export.ExportViewModel 
          return (T) new ExportViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.bindVehicleRepositoryProvider.get(), singletonCImpl.bindExportRepositoryProvider.get());

          case 10: // com.moto.tracker.ui.feature.fuel.list.FuelLogListViewModel 
          return (T) new FuelLogListViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.bindFuelRepositoryProvider.get(), singletonCImpl.bindVehicleRepositoryProvider.get());

          case 11: // com.moto.tracker.ui.feature.maintenance.kmlog.KmLogViewModel 
          return (T) new KmLogViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.bindVehicleRepositoryProvider.get(), singletonCImpl.bindMaintenanceRepositoryProvider.get(), viewModelCImpl.logDailyKmUseCase());

          case 12: // com.moto.tracker.ui.feature.maintenance.log.LogMaintenanceViewModel 
          return (T) new LogMaintenanceViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.bindMaintenanceRepositoryProvider.get(), viewModelCImpl.logMaintenanceUseCase());

          case 13: // com.moto.tracker.ui.feature.maintenance.list.MaintenanceListViewModel 
          return (T) new MaintenanceListViewModel(viewModelCImpl.savedStateHandle, viewModelCImpl.getMaintenanceTasksUseCase(), singletonCImpl.bindVehicleRepositoryProvider.get(), singletonCImpl.bindMaintenanceRepositoryProvider.get());

          case 14: // com.moto.tracker.ui.feature.maintenance.schedule.ManufacturerScheduleViewModel 
          return (T) new ManufacturerScheduleViewModel(singletonCImpl.bindManufacturerScheduleRepositoryProvider.get());

          case 15: // com.moto.tracker.ui.feature.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.bindUserPreferencesRepositoryProvider.get(), singletonCImpl.bindNotificationSchedulerProvider.get());

          case 16: // com.moto.tracker.ui.feature.vehicle.detail.VehicleDetailViewModel 
          return (T) new VehicleDetailViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.bindVehicleRepositoryProvider.get());

          case 17: // com.moto.tracker.ui.feature.vehicle.list.VehicleListViewModel 
          return (T) new VehicleListViewModel(viewModelCImpl.getVehiclesUseCase(), viewModelCImpl.deleteVehicleUseCase());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends MaintenanceTrackerApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends MaintenanceTrackerApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends MaintenanceTrackerApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<AppDatabase> provideAppDatabaseProvider;

    private Provider<AppointmentReminderWorker_AssistedFactory> appointmentReminderWorker_AssistedFactoryProvider;

    private Provider<DailyKmReminderWorker_AssistedFactory> dailyKmReminderWorker_AssistedFactoryProvider;

    private Provider<MaintenanceCheckWorker_AssistedFactory> maintenanceCheckWorker_AssistedFactoryProvider;

    private Provider<ManufacturerScheduleSeeder> manufacturerScheduleSeederProvider;

    private Provider<WorkManager> provideWorkManagerProvider;

    private Provider<DocumentRepositoryImpl> documentRepositoryImplProvider;

    private Provider<DocumentRepository> bindDocumentRepositoryProvider;

    private Provider<AppointmentRepositoryImpl> appointmentRepositoryImplProvider;

    private Provider<AppointmentRepository> bindAppointmentRepositoryProvider;

    private Provider<NotificationSchedulerImpl> notificationSchedulerImplProvider;

    private Provider<NotificationScheduler> bindNotificationSchedulerProvider;

    private Provider<MaintenanceRepositoryImpl> maintenanceRepositoryImplProvider;

    private Provider<MaintenanceRepository> bindMaintenanceRepositoryProvider;

    private Provider<VehicleRepositoryImpl> vehicleRepositoryImplProvider;

    private Provider<VehicleRepository> bindVehicleRepositoryProvider;

    private Provider<FuelRepositoryImpl> fuelRepositoryImplProvider;

    private Provider<FuelRepository> bindFuelRepositoryProvider;

    private Provider<ExpenseRepositoryImpl> expenseRepositoryImplProvider;

    private Provider<ExpenseRepository> bindExpenseRepositoryProvider;

    private Provider<ExportRepositoryImpl> exportRepositoryImplProvider;

    private Provider<ExportRepository> bindExportRepositoryProvider;

    private Provider<ManufacturerScheduleRepositoryImpl> manufacturerScheduleRepositoryImplProvider;

    private Provider<ManufacturerScheduleRepository> bindManufacturerScheduleRepositoryProvider;

    private Provider<DataStore<Preferences>> provideDataStoreProvider;

    private Provider<UserPreferencesRepositoryImpl> userPreferencesRepositoryImplProvider;

    private Provider<UserPreferencesRepository> bindUserPreferencesRepositoryProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);
      initialize2(applicationContextModuleParam);

    }

    private ServiceAppointmentDao serviceAppointmentDao() {
      return AppModule_ProvideServiceAppointmentDaoFactory.provideServiceAppointmentDao(provideAppDatabaseProvider.get());
    }

    private VehicleDao vehicleDao() {
      return AppModule_ProvideVehicleDaoFactory.provideVehicleDao(provideAppDatabaseProvider.get());
    }

    private KmLogDao kmLogDao() {
      return AppModule_ProvideKmLogDaoFactory.provideKmLogDao(provideAppDatabaseProvider.get());
    }

    private MaintenanceTaskDao maintenanceTaskDao() {
      return AppModule_ProvideMaintenanceTaskDaoFactory.provideMaintenanceTaskDao(provideAppDatabaseProvider.get());
    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return MapBuilder.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>newMapBuilder(3).put("com.moto.tracker.data.worker.AppointmentReminderWorker", ((Provider) appointmentReminderWorker_AssistedFactoryProvider)).put("com.moto.tracker.data.worker.DailyKmReminderWorker", ((Provider) dailyKmReminderWorker_AssistedFactoryProvider)).put("com.moto.tracker.data.worker.MaintenanceCheckWorker", ((Provider) maintenanceCheckWorker_AssistedFactoryProvider)).build();
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    private ManufacturerScheduleDao manufacturerScheduleDao() {
      return AppModule_ProvideManufacturerScheduleDaoFactory.provideManufacturerScheduleDao(provideAppDatabaseProvider.get());
    }

    private NotificationSchedulerImpl notificationSchedulerImpl() {
      return new NotificationSchedulerImpl(ApplicationContextModule_ProvideContextFactory.provideContext(applicationContextModule), provideWorkManagerProvider.get());
    }

    private DocumentDao documentDao() {
      return AppModule_ProvideDocumentDaoFactory.provideDocumentDao(provideAppDatabaseProvider.get());
    }

    private MaintenanceLogDao maintenanceLogDao() {
      return AppModule_ProvideMaintenanceLogDaoFactory.provideMaintenanceLogDao(provideAppDatabaseProvider.get());
    }

    private FuelLogDao fuelLogDao() {
      return AppModule_ProvideFuelLogDaoFactory.provideFuelLogDao(provideAppDatabaseProvider.get());
    }

    private ExpenseViewDao expenseViewDao() {
      return AppModule_ProvideExpenseViewDaoFactory.provideExpenseViewDao(provideAppDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideAppDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 1));
      this.appointmentReminderWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<AppointmentReminderWorker_AssistedFactory>(singletonCImpl, 0));
      this.dailyKmReminderWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<DailyKmReminderWorker_AssistedFactory>(singletonCImpl, 2));
      this.maintenanceCheckWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<MaintenanceCheckWorker_AssistedFactory>(singletonCImpl, 3));
      this.manufacturerScheduleSeederProvider = DoubleCheck.provider(new SwitchingProvider<ManufacturerScheduleSeeder>(singletonCImpl, 4));
      this.provideWorkManagerProvider = DoubleCheck.provider(new SwitchingProvider<WorkManager>(singletonCImpl, 5));
      this.documentRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 6);
      this.bindDocumentRepositoryProvider = DoubleCheck.provider((Provider) documentRepositoryImplProvider);
      this.appointmentRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 7);
      this.bindAppointmentRepositoryProvider = DoubleCheck.provider((Provider) appointmentRepositoryImplProvider);
      this.notificationSchedulerImplProvider = new SwitchingProvider<>(singletonCImpl, 8);
      this.bindNotificationSchedulerProvider = DoubleCheck.provider((Provider) notificationSchedulerImplProvider);
      this.maintenanceRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 9);
      this.bindMaintenanceRepositoryProvider = DoubleCheck.provider((Provider) maintenanceRepositoryImplProvider);
      this.vehicleRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 10);
      this.bindVehicleRepositoryProvider = DoubleCheck.provider((Provider) vehicleRepositoryImplProvider);
      this.fuelRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 11);
      this.bindFuelRepositoryProvider = DoubleCheck.provider((Provider) fuelRepositoryImplProvider);
      this.expenseRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 12);
      this.bindExpenseRepositoryProvider = DoubleCheck.provider((Provider) expenseRepositoryImplProvider);
      this.exportRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 13);
      this.bindExportRepositoryProvider = DoubleCheck.provider((Provider) exportRepositoryImplProvider);
      this.manufacturerScheduleRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 14);
      this.bindManufacturerScheduleRepositoryProvider = DoubleCheck.provider((Provider) manufacturerScheduleRepositoryImplProvider);
      this.provideDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<DataStore<Preferences>>(singletonCImpl, 16));
    }

    @SuppressWarnings("unchecked")
    private void initialize2(final ApplicationContextModule applicationContextModuleParam) {
      this.userPreferencesRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 15);
      this.bindUserPreferencesRepositoryProvider = DoubleCheck.provider((Provider) userPreferencesRepositoryImplProvider);
    }

    @Override
    public void injectMaintenanceTrackerApp(MaintenanceTrackerApp maintenanceTrackerApp) {
      injectMaintenanceTrackerApp2(maintenanceTrackerApp);
    }

    @Override
    public void injectBootReceiver(BootReceiver bootReceiver) {
      injectBootReceiver2(bootReceiver);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private MaintenanceTrackerApp injectMaintenanceTrackerApp2(MaintenanceTrackerApp instance) {
      MaintenanceTrackerApp_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      MaintenanceTrackerApp_MembersInjector.injectManufacturerScheduleSeeder(instance, manufacturerScheduleSeederProvider.get());
      return instance;
    }

    private BootReceiver injectBootReceiver2(BootReceiver instance2) {
      BootReceiver_MembersInjector.injectNotificationScheduler(instance2, notificationSchedulerImpl());
      return instance2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.moto.tracker.data.worker.AppointmentReminderWorker_AssistedFactory 
          return (T) new AppointmentReminderWorker_AssistedFactory() {
            @Override
            public AppointmentReminderWorker create(Context context, WorkerParameters params) {
              return new AppointmentReminderWorker(context, params, singletonCImpl.serviceAppointmentDao(), singletonCImpl.vehicleDao());
            }
          };

          case 1: // com.moto.tracker.data.local.AppDatabase 
          return (T) AppModule_ProvideAppDatabaseFactory.provideAppDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.moto.tracker.data.worker.DailyKmReminderWorker_AssistedFactory 
          return (T) new DailyKmReminderWorker_AssistedFactory() {
            @Override
            public DailyKmReminderWorker create(Context context2, WorkerParameters params2) {
              return new DailyKmReminderWorker(context2, params2, singletonCImpl.vehicleDao(), singletonCImpl.kmLogDao());
            }
          };

          case 3: // com.moto.tracker.data.worker.MaintenanceCheckWorker_AssistedFactory 
          return (T) new MaintenanceCheckWorker_AssistedFactory() {
            @Override
            public MaintenanceCheckWorker create(Context context3, WorkerParameters params3) {
              return new MaintenanceCheckWorker(context3, params3, singletonCImpl.maintenanceTaskDao(), singletonCImpl.vehicleDao());
            }
          };

          case 4: // com.moto.tracker.data.local.ManufacturerScheduleSeeder 
          return (T) new ManufacturerScheduleSeeder(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.manufacturerScheduleDao());

          case 5: // androidx.work.WorkManager 
          return (T) WorkerModule_ProvideWorkManagerFactory.provideWorkManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 6: // com.moto.tracker.data.repository.DocumentRepositoryImpl 
          return (T) new DocumentRepositoryImpl(singletonCImpl.documentDao());

          case 7: // com.moto.tracker.data.repository.AppointmentRepositoryImpl 
          return (T) new AppointmentRepositoryImpl(singletonCImpl.serviceAppointmentDao());

          case 8: // com.moto.tracker.data.repository.NotificationSchedulerImpl 
          return (T) new NotificationSchedulerImpl(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideWorkManagerProvider.get());

          case 9: // com.moto.tracker.data.repository.MaintenanceRepositoryImpl 
          return (T) new MaintenanceRepositoryImpl(singletonCImpl.maintenanceTaskDao(), singletonCImpl.maintenanceLogDao(), singletonCImpl.kmLogDao());

          case 10: // com.moto.tracker.data.repository.VehicleRepositoryImpl 
          return (T) new VehicleRepositoryImpl(singletonCImpl.vehicleDao());

          case 11: // com.moto.tracker.data.repository.FuelRepositoryImpl 
          return (T) new FuelRepositoryImpl(singletonCImpl.fuelLogDao());

          case 12: // com.moto.tracker.data.repository.ExpenseRepositoryImpl 
          return (T) new ExpenseRepositoryImpl(singletonCImpl.expenseViewDao());

          case 13: // com.moto.tracker.data.repository.ExportRepositoryImpl 
          return (T) new ExportRepositoryImpl(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.vehicleDao(), singletonCImpl.maintenanceLogDao(), singletonCImpl.fuelLogDao());

          case 14: // com.moto.tracker.data.repository.ManufacturerScheduleRepositoryImpl 
          return (T) new ManufacturerScheduleRepositoryImpl(singletonCImpl.manufacturerScheduleDao());

          case 15: // com.moto.tracker.data.repository.UserPreferencesRepositoryImpl 
          return (T) new UserPreferencesRepositoryImpl(singletonCImpl.provideDataStoreProvider.get());

          case 16: // androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> 
          return (T) AppModule_ProvideDataStoreFactory.provideDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
