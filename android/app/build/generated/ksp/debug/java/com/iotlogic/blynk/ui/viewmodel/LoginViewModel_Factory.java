package com.iotlogic.blynk.ui.viewmodel;

import android.content.Context;
import com.iotlogic.blynk.auth.AuthenticationManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class LoginViewModel_Factory implements Factory<LoginViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<AuthenticationManager> authManagerProvider;

  public LoginViewModel_Factory(Provider<Context> contextProvider,
      Provider<AuthenticationManager> authManagerProvider) {
    this.contextProvider = contextProvider;
    this.authManagerProvider = authManagerProvider;
  }

  @Override
  public LoginViewModel get() {
    return newInstance(contextProvider.get(), authManagerProvider.get());
  }

  public static LoginViewModel_Factory create(Provider<Context> contextProvider,
      Provider<AuthenticationManager> authManagerProvider) {
    return new LoginViewModel_Factory(contextProvider, authManagerProvider);
  }

  public static LoginViewModel newInstance(Context context, AuthenticationManager authManager) {
    return new LoginViewModel(context, authManager);
  }
}
