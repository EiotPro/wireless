package com.iotlogic.blynk.notifications;

import android.content.Context;
import com.iotlogic.blynk.data.local.preferences.AuthPreferences;
import com.iotlogic.blynk.data.remote.ApiService;
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
public final class FCMTokenManager_Factory implements Factory<FCMTokenManager> {
  private final Provider<Context> contextProvider;

  private final Provider<ApiService> apiServiceProvider;

  private final Provider<AuthPreferences> authPreferencesProvider;

  public FCMTokenManager_Factory(Provider<Context> contextProvider,
      Provider<ApiService> apiServiceProvider, Provider<AuthPreferences> authPreferencesProvider) {
    this.contextProvider = contextProvider;
    this.apiServiceProvider = apiServiceProvider;
    this.authPreferencesProvider = authPreferencesProvider;
  }

  @Override
  public FCMTokenManager get() {
    return newInstance(contextProvider.get(), apiServiceProvider.get(), authPreferencesProvider.get());
  }

  public static FCMTokenManager_Factory create(Provider<Context> contextProvider,
      Provider<ApiService> apiServiceProvider, Provider<AuthPreferences> authPreferencesProvider) {
    return new FCMTokenManager_Factory(contextProvider, apiServiceProvider, authPreferencesProvider);
  }

  public static FCMTokenManager newInstance(Context context, ApiService apiService,
      AuthPreferences authPreferences) {
    return new FCMTokenManager(context, apiService, authPreferences);
  }
}
