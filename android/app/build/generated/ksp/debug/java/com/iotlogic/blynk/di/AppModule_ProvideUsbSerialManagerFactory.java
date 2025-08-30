package com.iotlogic.blynk.di;

import android.content.Context;
import com.iotlogic.blynk.hardware.usb.UsbSerialManager;
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
public final class AppModule_ProvideUsbSerialManagerFactory implements Factory<UsbSerialManager> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideUsbSerialManagerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public UsbSerialManager get() {
    return provideUsbSerialManager(contextProvider.get());
  }

  public static AppModule_ProvideUsbSerialManagerFactory create(Provider<Context> contextProvider) {
    return new AppModule_ProvideUsbSerialManagerFactory(contextProvider);
  }

  public static UsbSerialManager provideUsbSerialManager(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideUsbSerialManager(context));
  }
}
