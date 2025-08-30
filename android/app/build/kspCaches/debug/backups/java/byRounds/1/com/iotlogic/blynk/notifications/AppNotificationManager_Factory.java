package com.iotlogic.blynk.notifications;

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
public final class AppNotificationManager_Factory implements Factory<AppNotificationManager> {
  private final Provider<Context> contextProvider;

  public AppNotificationManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AppNotificationManager get() {
    return newInstance(contextProvider.get());
  }

  public static AppNotificationManager_Factory create(Provider<Context> contextProvider) {
    return new AppNotificationManager_Factory(contextProvider);
  }

  public static AppNotificationManager newInstance(Context context) {
    return new AppNotificationManager(context);
  }
}
