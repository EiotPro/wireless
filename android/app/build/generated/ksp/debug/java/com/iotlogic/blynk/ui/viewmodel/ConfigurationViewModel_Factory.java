package com.iotlogic.blynk.ui.viewmodel;

import com.iotlogic.blynk.domain.repository.ConfigurationRepository;
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
    "KotlinInternalInJava"
})
public final class ConfigurationViewModel_Factory implements Factory<ConfigurationViewModel> {
  private final Provider<ConfigurationRepository> configurationRepositoryProvider;

  public ConfigurationViewModel_Factory(
      Provider<ConfigurationRepository> configurationRepositoryProvider) {
    this.configurationRepositoryProvider = configurationRepositoryProvider;
  }

  @Override
  public ConfigurationViewModel get() {
    return newInstance(configurationRepositoryProvider.get());
  }

  public static ConfigurationViewModel_Factory create(
      Provider<ConfigurationRepository> configurationRepositoryProvider) {
    return new ConfigurationViewModel_Factory(configurationRepositoryProvider);
  }

  public static ConfigurationViewModel newInstance(
      ConfigurationRepository configurationRepository) {
    return new ConfigurationViewModel(configurationRepository);
  }
}
