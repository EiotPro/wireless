package com.iotlogic.blynk.di;

import com.iotlogic.blynk.data.remote.ApiClient;
import com.iotlogic.blynk.data.remote.ApiService;
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
public final class AppModule_ProvideApiClientFactory implements Factory<ApiClient> {
  private final Provider<ApiService> apiServiceProvider;

  public AppModule_ProvideApiClientFactory(Provider<ApiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public ApiClient get() {
    return provideApiClient(apiServiceProvider.get());
  }

  public static AppModule_ProvideApiClientFactory create(Provider<ApiService> apiServiceProvider) {
    return new AppModule_ProvideApiClientFactory(apiServiceProvider);
  }

  public static ApiClient provideApiClient(ApiService apiService) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideApiClient(apiService));
  }
}
