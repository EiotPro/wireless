package com.iotlogic.blynk.di;

import com.iotlogic.blynk.data.local.dao.TelemetryDao;
import com.iotlogic.blynk.data.remote.ApiClient;
import com.iotlogic.blynk.domain.repository.TelemetryRepository;
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
public final class AppModule_ProvideTelemetryRepositoryFactory implements Factory<TelemetryRepository> {
  private final Provider<TelemetryDao> telemetryDaoProvider;

  private final Provider<ApiClient> apiClientProvider;

  public AppModule_ProvideTelemetryRepositoryFactory(Provider<TelemetryDao> telemetryDaoProvider,
      Provider<ApiClient> apiClientProvider) {
    this.telemetryDaoProvider = telemetryDaoProvider;
    this.apiClientProvider = apiClientProvider;
  }

  @Override
  public TelemetryRepository get() {
    return provideTelemetryRepository(telemetryDaoProvider.get(), apiClientProvider.get());
  }

  public static AppModule_ProvideTelemetryRepositoryFactory create(
      Provider<TelemetryDao> telemetryDaoProvider, Provider<ApiClient> apiClientProvider) {
    return new AppModule_ProvideTelemetryRepositoryFactory(telemetryDaoProvider, apiClientProvider);
  }

  public static TelemetryRepository provideTelemetryRepository(TelemetryDao telemetryDao,
      ApiClient apiClient) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideTelemetryRepository(telemetryDao, apiClient));
  }
}
