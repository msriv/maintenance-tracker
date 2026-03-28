package com.moto.tracker.di;

import com.moto.tracker.data.local.AppDatabase;
import com.moto.tracker.data.local.dao.KmLogDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideKmLogDaoFactory implements Factory<KmLogDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideKmLogDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public KmLogDao get() {
    return provideKmLogDao(dbProvider.get());
  }

  public static AppModule_ProvideKmLogDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideKmLogDaoFactory(dbProvider);
  }

  public static KmLogDao provideKmLogDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideKmLogDao(db));
  }
}
