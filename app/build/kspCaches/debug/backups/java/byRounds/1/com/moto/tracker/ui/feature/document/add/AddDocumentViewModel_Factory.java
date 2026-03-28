package com.moto.tracker.ui.feature.document.add;

import android.content.Context;
import androidx.lifecycle.SavedStateHandle;
import com.moto.tracker.domain.usecase.document.AddDocumentUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class AddDocumentViewModel_Factory implements Factory<AddDocumentViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<Context> contextProvider;

  private final Provider<AddDocumentUseCase> addDocumentProvider;

  public AddDocumentViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<Context> contextProvider, Provider<AddDocumentUseCase> addDocumentProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.contextProvider = contextProvider;
    this.addDocumentProvider = addDocumentProvider;
  }

  @Override
  public AddDocumentViewModel get() {
    return newInstance(savedStateHandleProvider.get(), contextProvider.get(), addDocumentProvider.get());
  }

  public static AddDocumentViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider, Provider<Context> contextProvider,
      Provider<AddDocumentUseCase> addDocumentProvider) {
    return new AddDocumentViewModel_Factory(savedStateHandleProvider, contextProvider, addDocumentProvider);
  }

  public static AddDocumentViewModel newInstance(SavedStateHandle savedStateHandle, Context context,
      AddDocumentUseCase addDocument) {
    return new AddDocumentViewModel(savedStateHandle, context, addDocument);
  }
}
