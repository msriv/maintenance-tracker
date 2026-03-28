package com.moto.tracker.di;

import com.moto.tracker.data.local.AppDatabase;
import com.moto.tracker.data.local.dao.DocumentDao;
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
public final class AppModule_ProvideDocumentDaoFactory implements Factory<DocumentDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideDocumentDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public DocumentDao get() {
    return provideDocumentDao(dbProvider.get());
  }

  public static AppModule_ProvideDocumentDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideDocumentDaoFactory(dbProvider);
  }

  public static DocumentDao provideDocumentDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideDocumentDao(db));
  }
}
