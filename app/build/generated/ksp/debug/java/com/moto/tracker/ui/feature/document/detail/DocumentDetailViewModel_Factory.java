package com.moto.tracker.ui.feature.document.detail;

import androidx.lifecycle.SavedStateHandle;
import com.moto.tracker.domain.repository.DocumentRepository;
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
public final class DocumentDetailViewModel_Factory implements Factory<DocumentDetailViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<DocumentRepository> documentRepositoryProvider;

  public DocumentDetailViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<DocumentRepository> documentRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.documentRepositoryProvider = documentRepositoryProvider;
  }

  @Override
  public DocumentDetailViewModel get() {
    return newInstance(savedStateHandleProvider.get(), documentRepositoryProvider.get());
  }

  public static DocumentDetailViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<DocumentRepository> documentRepositoryProvider) {
    return new DocumentDetailViewModel_Factory(savedStateHandleProvider, documentRepositoryProvider);
  }

  public static DocumentDetailViewModel newInstance(SavedStateHandle savedStateHandle,
      DocumentRepository documentRepository) {
    return new DocumentDetailViewModel(savedStateHandle, documentRepository);
  }
}
