package com.iotlogic.blynk.di;

import com.iotlogic.blynk.data.local.dao.DeviceDao;
import com.iotlogic.blynk.data.remote.ApiClient;
import com.iotlogic.blynk.domain.repository.DeviceRepository;
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
public final class AppModule_ProvideDeviceRepositoryFactory implements Factory<DeviceRepository> {
  private final Provider<DeviceDao> deviceDaoProvider;

  private final Provider<ApiClient> apiClientProvider;

  public AppModule_ProvideDeviceRepositoryFactory(Provider<DeviceDao> deviceDaoProvider,
      Provider<ApiClient> apiClientProvider) {
    this.deviceDaoProvider = deviceDaoProvider;
    this.apiClientProvider = apiClientProvider;
  }

  @Override
  public DeviceRepository get() {
    return provideDeviceRepository(deviceDaoProvider.get(), apiClientProvider.get());
  }

  public static AppModule_ProvideDeviceRepositoryFactory create(
      Provider<DeviceDao> deviceDaoProvider, Provider<ApiClient> apiClientProvider) {
    return new AppModule_ProvideDeviceRepositoryFactory(deviceDaoProvider, apiClientProvider);
  }

  public static DeviceRepository provideDeviceRepository(DeviceDao deviceDao, ApiClient apiClient) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideDeviceRepository(deviceDao, apiClient));
  }
}
