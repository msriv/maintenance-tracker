package com.moto.tracker.domain.usecase.analytics;

import com.moto.tracker.domain.repository.ExpenseRepository;
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
public final class GetExpenseSummaryUseCase_Factory implements Factory<GetExpenseSummaryUseCase> {
  private final Provider<ExpenseRepository> repositoryProvider;

  public GetExpenseSummaryUseCase_Factory(Provider<ExpenseRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetExpenseSummaryUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetExpenseSummaryUseCase_Factory create(
      Provider<ExpenseRepository> repositoryProvider) {
    return new GetExpenseSummaryUseCase_Factory(repositoryProvider);
  }

  public static GetExpenseSummaryUseCase newInstance(ExpenseRepository repository) {
    return new GetExpenseSummaryUseCase(repository);
  }
}
