package com.iotlogic.blynk.di;

import com.iotlogic.blynk.data.local.IoTLogicDatabase;
import com.iotlogic.blynk.data.local.dao.ConfigurationDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
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
public final class AppModule_ProvideConfigurationDaoFactory implements Factory<ConfigurationDao> {
  private final Provider<IoTLogicDatabase> databaseProvider;

  public AppModule_ProvideConfigurationDaoFactory(Provider<IoTLogicDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public ConfigurationDao get() {
    return provideConfigurationDao(databaseProvider.get());
  }

  public static AppModule_ProvideConfigurationDaoFactory create(
      Provider<IoTLogicDatabase> databaseProvider) {
    return new AppModule_ProvideConfigurationDaoFactory(databaseProvider);
  }

  public static ConfigurationDao provideConfigurationDao(IoTLogicDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideConfigurationDao(database));
  }
}
