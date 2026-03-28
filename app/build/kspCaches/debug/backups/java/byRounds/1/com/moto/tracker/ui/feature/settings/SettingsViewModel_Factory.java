package com.moto.tracker.ui.feature.settings;

import com.moto.tracker.domain.repository.NotificationScheduler;
import com.moto.tracker.domain.repository.UserPreferencesRepository;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<UserPreferencesRepository> prefsProvider;

  private final Provider<NotificationScheduler> notificationSchedulerProvider;

  public SettingsViewModel_Factory(Provider<UserPreferencesRepository> prefsProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    this.prefsProvider = prefsProvider;
    this.notificationSchedulerProvider = notificationSchedulerProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(prefsProvider.get(), notificationSchedulerProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<UserPreferencesRepository> prefsProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    return new SettingsViewModel_Factory(prefsProvider, notificationSchedulerProvider);
  }

  public static SettingsViewModel newInstance(UserPreferencesRepository prefs,
      NotificationScheduler notificationScheduler) {
    return new SettingsViewModel(prefs, notificationScheduler);
  }
}
