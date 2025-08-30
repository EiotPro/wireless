package com.iotlogic.blynk.utils;

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
public final class PermissionManager_Factory implements Factory<PermissionManager> {
  private final Provider<Context> contextProvider;

  public PermissionManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PermissionManager get() {
    return newInstance(contextProvider.get());
  }

  public static PermissionManager_Factory create(Provider<Context> contextProvider) {
    return new PermissionManager_Factory(contextProvider);
  }

  public static PermissionManager newInstance(Context context) {
    return new PermissionManager(context);
  }
}
