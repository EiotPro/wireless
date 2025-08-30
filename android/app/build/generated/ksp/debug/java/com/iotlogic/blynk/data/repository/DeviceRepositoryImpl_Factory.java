package com.iotlogic.blynk.data.repository;

import com.iotlogic.blynk.data.local.dao.DeviceDao;
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
public final class DeviceRepositoryImpl_Factory implements Factory<DeviceRepositoryImpl> {
  private final Provider<DeviceDao> deviceDaoProvider;

  private final Provider<ApiClient> apiClientProvider;

  public DeviceRepositoryImpl_Factory(Provider<DeviceDao> deviceDaoProvider,
      Provider<ApiClient> apiClientProvider) {
    this.deviceDaoProvider = deviceDaoProvider;
    this.apiClientProvider = apiClientProvider;
  }

  @Override
  public DeviceRepositoryImpl get() {
    return newInstance(deviceDaoProvider.get(), apiClientProvider.get());
  }

  public static DeviceRepositoryImpl_Factory create(Provider<DeviceDao> deviceDaoProvider,
      Provider<ApiClient> apiClientProvider) {
    return new DeviceRepositoryImpl_Factory(deviceDaoProvider, apiClientProvider);
  }

  public static DeviceRepositoryImpl newInstance(DeviceDao deviceDao, ApiClient apiClient) {
    return new DeviceRepositoryImpl(deviceDao, apiClient);
  }
}
