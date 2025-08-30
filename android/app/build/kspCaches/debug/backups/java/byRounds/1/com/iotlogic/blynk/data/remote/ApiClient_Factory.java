package com.iotlogic.blynk.data.remote;

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
public final class ApiClient_Factory implements Factory<ApiClient> {
  private final Provider<ApiService> apiServiceProvider;

  public ApiClient_Factory(Provider<ApiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public ApiClient get() {
    return newInstance(apiServiceProvider.get());
  }

  public static ApiClient_Factory create(Provider<ApiService> apiServiceProvider) {
    return new ApiClient_Factory(apiServiceProvider);
  }

  public static ApiClient newInstance(ApiService apiService) {
    return new ApiClient(apiService);
  }
}
