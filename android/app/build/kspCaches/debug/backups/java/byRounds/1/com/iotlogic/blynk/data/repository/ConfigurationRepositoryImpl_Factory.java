package com.iotlogic.blynk.data.repository;

import com.iotlogic.blynk.data.local.dao.ConfigurationDao;
import com.iotlogic.blynk.data.remote.ApiClient;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ConfigurationRepositoryImpl_Factory implements Factory<ConfigurationRepositoryImpl> {
  private final Provider<ConfigurationDao> configurationDaoProvider;

  private final Provider<ApiClient> apiClientProvider;

  public ConfigurationRepositoryImpl_Factory(Provider<ConfigurationDao> configurationDaoProvider,
      Provider<ApiClient> apiClientProvider) {
    this.configurationDaoProvider = configurationDaoProvider;
    this.apiClientProvider = apiClientProvider;
  }

  @Override
  public ConfigurationRepositoryImpl get() {
    return newInstance(configurationDaoProvider.get(), apiClientProvider.get());
  }

  public static ConfigurationRepositoryImpl_Factory create(
      Provider<ConfigurationDao> configurationDaoProvider, Provider<ApiClient> apiClientProvider) {
    return new ConfigurationRepositoryImpl_Factory(configurationDaoProvider, apiClientProvider);
  }

  public static ConfigurationRepositoryImpl newInstance(ConfigurationDao configurationDao,
      ApiClient apiClient) {
    return new ConfigurationRepositoryImpl(configurationDao, apiClient);
  }
}
