package com.iotlogic.blynk.services;

import com.iotlogic.blynk.domain.repository.DeviceRepository;
import com.iotlogic.blynk.domain.repository.TelemetryRepository;
import com.iotlogic.blynk.hardware.mqtt.MqttConnectionManager;
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
public final class MqttBackgroundService_MembersInjector implements MembersInjector<MqttBackgroundService> {
  private final Provider<MqttConnectionManager> mqttConnectionManagerProvider;

  private final Provider<DeviceRepository> deviceRepositoryProvider;

  private final Provider<TelemetryRepository> telemetryRepositoryProvider;

  public MqttBackgroundService_MembersInjector(
      Provider<MqttConnectionManager> mqttConnectionManagerProvider,
      Provider<DeviceRepository> deviceRepositoryProvider,
      Provider<TelemetryRepository> telemetryRepositoryProvider) {
    this.mqttConnectionManagerProvider = mqttConnectionManagerProvider;
    this.deviceRepositoryProvider = deviceRepositoryProvider;
    this.telemetryRepositoryProvider = telemetryRepositoryProvider;
  }

  public static MembersInjector<MqttBackgroundService> create(
      Provider<MqttConnectionManager> mqttConnectionManagerProvider,
      Provider<DeviceRepository> deviceRepositoryProvider,
      Provider<TelemetryRepository> telemetryRepositoryProvider) {
    return new MqttBackgroundService_MembersInjector(mqttConnectionManagerProvider, deviceRepositoryProvider, telemetryRepositoryProvider);
  }

  @Override
  public void injectMembers(MqttBackgroundService instance) {
    injectMqttConnectionManager(instance, mqttConnectionManagerProvider.get());
    injectDeviceRepository(instance, deviceRepositoryProvider.get());
    injectTelemetryRepository(instance, telemetryRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.iotlogic.blynk.services.MqttBackgroundService.mqttConnectionManager")
  public static void injectMqttConnectionManager(MqttBackgroundService instance,
      MqttConnectionManager mqttConnectionManager) {
    instance.mqttConnectionManager = mqttConnectionManager;
  }

  @InjectedFieldSignature("com.iotlogic.blynk.services.MqttBackgroundService.deviceRepository")
  public static void injectDeviceRepository(MqttBackgroundService instance,
      DeviceRepository deviceRepository) {
    instance.deviceRepository = deviceRepository;
  }

  @InjectedFieldSignature("com.iotlogic.blynk.services.MqttBackgroundService.telemetryRepository")
  public static void injectTelemetryRepository(MqttBackgroundService instance,
      TelemetryRepository telemetryRepository) {
    instance.telemetryRepository = telemetryRepository;
  }
}
