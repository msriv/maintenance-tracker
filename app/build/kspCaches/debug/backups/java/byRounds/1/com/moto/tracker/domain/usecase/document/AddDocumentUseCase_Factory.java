package com.moto.tracker.domain.usecase.document;

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
public final class AddDocumentUseCase_Factory implements Factory<AddDocumentUseCase> {
  private final Provider<DocumentRepository> repositoryProvider;

  public AddDocumentUseCase_Factory(Provider<DocumentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public AddDocumentUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static AddDocumentUseCase_Factory create(Provider<DocumentRepository> repositoryProvider) {
    return new AddDocumentUseCase_Factory(repositoryProvider);
  }

  public static AddDocumentUseCase newInstance(DocumentRepository repository) {
    return new AddDocumentUseCase(repository);
  }
}
