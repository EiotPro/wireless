package com.iotlogic.blynk.hardware.mqtt;

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
public final class MqttConnectionManager_Factory implements Factory<MqttConnectionManager> {
  private final Provider<Context> contextProvider;

  public MqttConnectionManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public MqttConnectionManager get() {
    return newInstance(contextProvider.get());
  }

  public static MqttConnectionManager_Factory create(Provider<Context> contextProvider) {
    return new MqttConnectionManager_Factory(contextProvider);
  }

  public static MqttConnectionManager newInstance(Context context) {
    return new MqttConnectionManager(context);
  }
}
