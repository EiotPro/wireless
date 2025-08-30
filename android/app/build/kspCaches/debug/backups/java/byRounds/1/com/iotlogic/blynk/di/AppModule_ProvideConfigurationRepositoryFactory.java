package com.iotlogic.blynk.di;

import com.iotlogic.blynk.data.local.dao.ConfigurationDao;
import com.iotlogic.blynk.data.remote.ApiClient;
import com.iotlogic.blynk.domain.repository.ConfigurationRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideConfigurationRepositoryFactory implements Factory<ConfigurationRepository> {
  private final Provider<ConfigurationDao> configurationDaoProvider;

  private final Provider<ApiClient> apiClientProvider;

  public AppModule_ProvideConfigurationRepositoryFactory(
      Provider<ConfigurationDao> configurationDaoProvider, Provider<ApiClient> apiClientProvider) {
    this.configurationDaoProvider = configurationDaoProvider;
    this.apiClientProvider = apiClientProvider;
  }

  @Override
  public ConfigurationRepository get() {
    return provideConfigurationRepository(configurationDaoProvider.get(), apiClientProvider.get());
  }

  public static AppModule_ProvideConfigurationRepositoryFactory create(
      Provider<ConfigurationDao> configurationDaoProvider, Provider<ApiClient> apiClientProvider) {
    return new AppModule_ProvideConfigurationRepositoryFactory(configurationDaoProvider, apiClientProvider);
  }

  public static ConfigurationRepository provideConfigurationRepository(
      ConfigurationDao configurationDao, ApiClient apiClient) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideConfigurationRepository(configurationDao, apiClient));
  }
}
