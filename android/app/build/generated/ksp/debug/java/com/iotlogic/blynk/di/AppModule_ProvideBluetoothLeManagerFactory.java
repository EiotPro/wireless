package com.iotlogic.blynk.di;

import android.content.Context;
import com.iotlogic.blynk.hardware.bluetooth.BluetoothLeManager;
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
public final class AppModule_ProvideBluetoothLeManagerFactory implements Factory<BluetoothLeManager> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideBluetoothLeManagerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public BluetoothLeManager get() {
    return provideBluetoothLeManager(contextProvider.get());
  }

  public static AppModule_ProvideBluetoothLeManagerFactory create(
      Provider<Context> contextProvider) {
    return new AppModule_ProvideBluetoothLeManagerFactory(contextProvider);
  }

  public static BluetoothLeManager provideBluetoothLeManager(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBluetoothLeManager(context));
  }
}
