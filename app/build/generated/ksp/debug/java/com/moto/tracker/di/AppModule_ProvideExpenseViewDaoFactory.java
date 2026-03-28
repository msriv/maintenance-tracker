package com.moto.tracker.di;

import com.moto.tracker.data.local.AppDatabase;
import com.moto.tracker.data.local.dao.ExpenseViewDao;
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
public final class AppModule_ProvideExpenseViewDaoFactory implements Factory<ExpenseViewDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideExpenseViewDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ExpenseViewDao get() {
    return provideExpenseViewDao(dbProvider.get());
  }

  public static AppModule_ProvideExpenseViewDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideExpenseViewDaoFactory(dbProvider);
  }

  public static ExpenseViewDao provideExpenseViewDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideExpenseViewDao(db));
  }
}
