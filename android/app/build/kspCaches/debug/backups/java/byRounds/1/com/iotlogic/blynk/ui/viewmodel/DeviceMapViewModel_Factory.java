package com.iotlogic.blynk.ui.viewmodel;

import com.iotlogic.blynk.domain.repository.DeviceRepository;
import com.iotlogic.blynk.location.LocationManager;
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
public final class DeviceMapViewModel_Factory implements Factory<DeviceMapViewModel> {
  private final Provider<LocationManager> locationManagerProvider;

  private final Provider<DeviceRepository> deviceRepositoryProvider;

  public DeviceMapViewModel_Factory(Provider<LocationManager> locationManagerProvider,
      Provider<DeviceRepository> deviceRepositoryProvider) {
    this.locationManagerProvider = locationManagerProvider;
    this.deviceRepositoryProvider = deviceRepositoryProvider;
  }

  @Override
  public DeviceMapViewModel get() {
    return newInstance(locationManagerProvider.get(), deviceRepositoryProvider.get());
  }

  public static DeviceMapViewModel_Factory create(Provider<LocationManager> locationManagerProvider,
      Provider<DeviceRepository> deviceRepositoryProvider) {
    return new DeviceMapViewModel_Factory(locationManagerProvider, deviceRepositoryProvider);
  }

  public static DeviceMapViewModel newInstance(LocationManager locationManager,
      DeviceRepository deviceRepository) {
    return new DeviceMapViewModel(locationManager, deviceRepository);
  }
}
