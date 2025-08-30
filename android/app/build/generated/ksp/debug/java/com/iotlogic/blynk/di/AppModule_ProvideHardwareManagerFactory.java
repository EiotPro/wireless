package com.iotlogic.blynk.di;

import android.content.Context;
import com.iotlogic.blynk.hardware.HardwareManager;
import com.iotlogic.blynk.hardware.bluetooth.BluetoothLeManager;
import com.iotlogic.blynk.hardware.mqtt.MqttConnectionManager;
import com.iotlogic.blynk.hardware.usb.UsbSerialManager;
import com.iotlogic.blynk.hardware.wifi.WiFiDeviceManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AppModule_ProvideHardwareManagerFactory implements Factory<HardwareManager> {
  private final Provider<Context> contextProvider;

  private final Provider<BluetoothLeManager> bluetoothLeManagerProvider;

  private final Provider<WiFiDeviceManager> wifiDeviceManagerProvider;

  private final Provider<UsbSerialManager> usbSerialManagerProvider;

  private final Provider<MqttConnectionManager> mqttConnectionManagerProvider;

  public AppModule_ProvideHardwareManagerFactory(Provider<Context> contextProvider,
      Provider<BluetoothLeManager> bluetoothLeManagerProvider,
      Provider<WiFiDeviceManager> wifiDeviceManagerProvider,
      Provider<UsbSerialManager> usbSerialManagerProvider,
      Provider<MqttConnectionManager> mqttConnectionManagerProvider) {
    this.contextProvider = contextProvider;
    this.bluetoothLeManagerProvider = bluetoothLeManagerProvider;
    this.wifiDeviceManagerProvider = wifiDeviceManagerProvider;
    this.usbSerialManagerProvider = usbSerialManagerProvider;
    this.mqttConnectionManagerProvider = mqttConnectionManagerProvider;
  }

  @Override
  public HardwareManager get() {
    return provideHardwareManager(contextProvider.get(), bluetoothLeManagerProvider.get(), wifiDeviceManagerProvider.get(), usbSerialManagerProvider.get(), mqttConnectionManagerProvider.get());
  }

  public static AppModule_ProvideHardwareManagerFactory create(Provider<Context> contextProvider,
      Provider<BluetoothLeManager> bluetoothLeManagerProvider,
      Provider<WiFiDeviceManager> wifiDeviceManagerProvider,
      Provider<UsbSerialManager> usbSerialManagerProvider,
      Provider<MqttConnectionManager> mqttConnectionManagerProvider) {
    return new AppModule_ProvideHardwareManagerFactory(contextProvider, bluetoothLeManagerProvider, wifiDeviceManagerProvider, usbSerialManagerProvider, mqttConnectionManagerProvider);
  }

  public static HardwareManager provideHardwareManager(Context context,
      BluetoothLeManager bluetoothLeManager, WiFiDeviceManager wifiDeviceManager,
      UsbSerialManager usbSerialManager, MqttConnectionManager mqttConnectionManager) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideHardwareManager(context, bluetoothLeManager, wifiDeviceManager, usbSerialManager, mqttConnectionManager));
  }
}
