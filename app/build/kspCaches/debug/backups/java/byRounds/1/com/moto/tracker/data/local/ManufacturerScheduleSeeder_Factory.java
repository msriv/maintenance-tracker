package com.moto.tracker.data.local;

import android.content.Context;
import com.moto.tracker.data.local.dao.ManufacturerScheduleDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ManufacturerScheduleSeeder_Factory implements Factory<ManufacturerScheduleSeeder> {
  private final Provider<Context> contextProvider;

  private final Provider<ManufacturerScheduleDao> daoProvider;

  public ManufacturerScheduleSeeder_Factory(Provider<Context> contextProvider,
      Provider<ManufacturerScheduleDao> daoProvider) {
    this.contextProvider = contextProvider;
    this.daoProvider = daoProvider;
  }

  @Override
  public ManufacturerScheduleSeeder get() {
    return newInstance(contextProvider.get(), daoProvider.get());
  }

  public static ManufacturerScheduleSeeder_Factory create(Provider<Context> contextProvider,
      Provider<ManufacturerScheduleDao> daoProvider) {
    return new ManufacturerScheduleSeeder_Factory(contextProvider, daoProvider);
  }

  public static ManufacturerScheduleSeeder newInstance(Context context,
      ManufacturerScheduleDao dao) {
    return new ManufacturerScheduleSeeder(context, dao);
  }
}
