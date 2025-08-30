package com.iotlogic.blynk.data.repository;

import com.iotlogic.blynk.data.local.dao.TelemetryDao;
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
public final class TelemetryRepositoryImpl_Factory implements Factory<TelemetryRepositoryImpl> {
  private final Provider<TelemetryDao> telemetryDaoProvider;

  private final Provider<ApiClient> apiClientProvider;

  public TelemetryRepositoryImpl_Factory(Provider<TelemetryDao> telemetryDaoProvider,
      Provider<ApiClient> apiClientProvider) {
    this.telemetryDaoProvider = telemetryDaoProvider;
    this.apiClientProvider = apiClientProvider;
  }

  @Override
  public TelemetryRepositoryImpl get() {
    return newInstance(telemetryDaoProvider.get(), apiClientProvider.get());
  }

  public static TelemetryRepositoryImpl_Factory create(Provider<TelemetryDao> telemetryDaoProvider,
      Provider<ApiClient> apiClientProvider) {
    return new TelemetryRepositoryImpl_Factory(telemetryDaoProvider, apiClientProvider);
  }

  public static TelemetryRepositoryImpl newInstance(TelemetryDao telemetryDao,
      ApiClient apiClient) {
    return new TelemetryRepositoryImpl(telemetryDao, apiClient);
  }
}
