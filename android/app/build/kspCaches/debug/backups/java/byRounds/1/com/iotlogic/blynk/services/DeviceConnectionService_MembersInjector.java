package com.iotlogic.blynk.services;

import com.iotlogic.blynk.domain.repository.DeviceRepository;
import com.iotlogic.blynk.domain.repository.TelemetryRepository;
import com.iotlogic.blynk.hardware.HardwareManager;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class DeviceConnectionService_MembersInjector implements MembersInjector<DeviceConnectionService> {
  private final Provider<HardwareManager> hardwareManagerProvider;

  private final Provider<DeviceRepository> deviceRepositoryProvider;

  private final Provider<TelemetryRepository> telemetryRepositoryProvider;

  public DeviceConnectionService_MembersInjector(Provider<HardwareManager> hardwareManagerProvider,
      Provider<DeviceRepository> deviceRepositoryProvider,
      Provider<TelemetryRepository> telemetryRepositoryProvider) {
    this.hardwareManagerProvider = hardwareManagerProvider;
    this.deviceRepositoryProvider = deviceRepositoryProvider;
    this.telemetryRepositoryProvider = telemetryRepositoryProvider;
  }

  public static MembersInjector<DeviceConnectionService> create(
      Provider<HardwareManager> hardwareManagerProvider,
      Provider<DeviceRepository> deviceRepositoryProvider,
      Provider<TelemetryRepository> telemetryRepositoryProvider) {
    return new DeviceConnectionService_MembersInjector(hardwareManagerProvider, deviceRepositoryProvider, telemetryRepositoryProvider);
  }

  @Override
  public void injectMembers(DeviceConnectionService instance) {
    injectHardwareManager(instance, hardwareManagerProvider.get());
    injectDeviceRepository(instance, deviceRepositoryProvider.get());
    injectTelemetryRepository(instance, telemetryRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.iotlogic.blynk.services.DeviceConnectionService.hardwareManager")
  public static void injectHardwareManager(DeviceConnectionService instance,
      HardwareManager hardwareManager) {
    instance.hardwareManager = hardwareManager;
  }

  @InjectedFieldSignature("com.iotlogic.blynk.services.DeviceConnectionService.deviceRepository")
  public static void injectDeviceRepository(DeviceConnectionService instance,
      DeviceRepository deviceRepository) {
    instance.deviceRepository = deviceRepository;
  }

  @InjectedFieldSignature("com.iotlogic.blynk.services.DeviceConnectionService.telemetryRepository")
  public static void injectTelemetryRepository(DeviceConnectionService instance,
      TelemetryRepository telemetryRepository) {
    instance.telemetryRepository = telemetryRepository;
  }
}
