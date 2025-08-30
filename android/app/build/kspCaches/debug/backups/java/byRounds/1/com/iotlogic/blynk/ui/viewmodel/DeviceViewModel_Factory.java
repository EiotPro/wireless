package com.iotlogic.blynk.ui.viewmodel;

import com.iotlogic.blynk.domain.repository.DeviceRepository;
import com.iotlogic.blynk.hardware.HardwareManager;
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
public final class DeviceViewModel_Factory implements Factory<DeviceViewModel> {
  private final Provider<DeviceRepository> deviceRepositoryProvider;

  private final Provider<HardwareManager> hardwareManagerProvider;

  public DeviceViewModel_Factory(Provider<DeviceRepository> deviceRepositoryProvider,
      Provider<HardwareManager> hardwareManagerProvider) {
    this.deviceRepositoryProvider = deviceRepositoryProvider;
    this.hardwareManagerProvider = hardwareManagerProvider;
  }

  @Override
  public DeviceViewModel get() {
    return newInstance(deviceRepositoryProvider.get(), hardwareManagerProvider.get());
  }

  public static DeviceViewModel_Factory create(Provider<DeviceRepository> deviceRepositoryProvider,
      Provider<HardwareManager> hardwareManagerProvider) {
    return new DeviceViewModel_Factory(deviceRepositoryProvider, hardwareManagerProvider);
  }

  public static DeviceViewModel newInstance(DeviceRepository deviceRepository,
      HardwareManager hardwareManager) {
    return new DeviceViewModel(deviceRepository, hardwareManager);
  }
}
