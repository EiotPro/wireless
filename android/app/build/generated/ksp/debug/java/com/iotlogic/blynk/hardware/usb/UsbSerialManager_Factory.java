package com.iotlogic.blynk.hardware.usb;

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
public final class UsbSerialManager_Factory implements Factory<UsbSerialManager> {
  private final Provider<Context> contextProvider;

  public UsbSerialManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public UsbSerialManager get() {
    return newInstance(contextProvider.get());
  }

  public static UsbSerialManager_Factory create(Provider<Context> contextProvider) {
    return new UsbSerialManager_Factory(contextProvider);
  }

  public static UsbSerialManager newInstance(Context context) {
    return new UsbSerialManager(context);
  }
}
