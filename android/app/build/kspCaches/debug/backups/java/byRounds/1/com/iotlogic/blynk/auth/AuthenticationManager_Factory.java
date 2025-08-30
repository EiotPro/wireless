package com.iotlogic.blynk.auth;

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
public final class AuthenticationManager_Factory implements Factory<AuthenticationManager> {
  private final Provider<Context> contextProvider;

  private final Provider<AuthPreferences> authPreferencesProvider;

  private final Provider<ApiService> apiServiceProvider;

  public AuthenticationManager_Factory(Provider<Context> contextProvider,
      Provider<AuthPreferences> authPreferencesProvider, Provider<ApiService> apiServiceProvider) {
    this.contextProvider = contextProvider;
    this.authPreferencesProvider = authPreferencesProvider;
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public AuthenticationManager get() {
    return newInstance(contextProvider.get(), authPreferencesProvider.get(), apiServiceProvider.get());
  }

  public static AuthenticationManager_Factory create(Provider<Context> contextProvider,
      Provider<AuthPreferences> authPreferencesProvider, Provider<ApiService> apiServiceProvider) {
    return new AuthenticationManager_Factory(contextProvider, authPreferencesProvider, apiServiceProvider);
  }

  public static AuthenticationManager newInstance(Context context, AuthPreferences authPreferences,
      ApiService apiService) {
    return new AuthenticationManager(context, authPreferences, apiService);
  }
}
