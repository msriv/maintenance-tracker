package com.moto.tracker.domain.usecase.maintenance;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class ComputeNextDueDateUseCase_Factory implements Factory<ComputeNextDueDateUseCase> {
  @Override
  public ComputeNextDueDateUseCase get() {
    return newInstance();
  }

  public static ComputeNextDueDateUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ComputeNextDueDateUseCase newInstance() {
    return new ComputeNextDueDateUseCase();
  }

  private static final class InstanceHolder {
    private static final ComputeNextDueDateUseCase_Factory INSTANCE = new ComputeNextDueDateUseCase_Factory();
  }
}
