package com.iotlogic.blynk.di;

import com.iotlogic.blynk.data.local.IoTLogicDatabase;
import com.iotlogic.blynk.data.local.dao.DeviceDao;
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
public final class AppModule_ProvideDeviceDaoFactory implements Factory<DeviceDao> {
  private final Provider<IoTLogicDatabase> databaseProvider;

  public AppModule_ProvideDeviceDaoFactory(Provider<IoTLogicDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public DeviceDao get() {
    return provideDeviceDao(databaseProvider.get());
  }

  public static AppModule_ProvideDeviceDaoFactory create(
      Provider<IoTLogicDatabase> databaseProvider) {
    return new AppModule_ProvideDeviceDaoFactory(databaseProvider);
  }

  public static DeviceDao provideDeviceDao(IoTLogicDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideDeviceDao(database));
  }
}
