package com.iotlogic.blynk.di;

import android.content.Context;
import com.iotlogic.blynk.data.local.IoTLogicDatabase;
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
public final class AppModule_ProvideIoTLogicDatabaseFactory implements Factory<IoTLogicDatabase> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideIoTLogicDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public IoTLogicDatabase get() {
    return provideIoTLogicDatabase(contextProvider.get());
  }

  public static AppModule_ProvideIoTLogicDatabaseFactory create(Provider<Context> contextProvider) {
    return new AppModule_ProvideIoTLogicDatabaseFactory(contextProvider);
  }

  public static IoTLogicDatabase provideIoTLogicDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideIoTLogicDatabase(context));
  }
}
