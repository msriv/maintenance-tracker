package com.moto.tracker.ui.feature.document.list;

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
public final class DocumentListViewModel_Factory implements Factory<DocumentListViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<DocumentRepository> documentRepositoryProvider;

  public DocumentListViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<DocumentRepository> documentRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.documentRepositoryProvider = documentRepositoryProvider;
  }

  @Override
  public DocumentListViewModel get() {
    return newInstance(savedStateHandleProvider.get(), documentRepositoryProvider.get());
  }

  public static DocumentListViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<DocumentRepository> documentRepositoryProvider) {
    return new DocumentListViewModel_Factory(savedStateHandleProvider, documentRepositoryProvider);
  }

  public static DocumentListViewModel newInstance(SavedStateHandle savedStateHandle,
      DocumentRepository documentRepository) {
    return new DocumentListViewModel(savedStateHandle, documentRepository);
  }
}
