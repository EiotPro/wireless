package com.iotlogic.blynk.hardware.wifi;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class WiFiDeviceManager_Factory implements Factory<WiFiDeviceManager> {
  private final Provider<Context> contextProvider;

  public WiFiDeviceManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public WiFiDeviceManager get() {
    return newInstance(contextProvider.get());
  }

  public static WiFiDeviceManager_Factory create(Provider<Context> contextProvider) {
    return new WiFiDeviceManager_Factory(contextProvider);
  }

  public static WiFiDeviceManager newInstance(Context context) {
    return new WiFiDeviceManager(context);
  }
}
