package com.iotlogic.blynk.di;

import com.iotlogic.blynk.data.local.IoTLogicDatabase;
import com.iotlogic.blynk.data.local.dao.TelemetryDao;
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
public final class AppModule_ProvideTelemetryDaoFactory implements Factory<TelemetryDao> {
  private final Provider<IoTLogicDatabase> databaseProvider;

  public AppModule_ProvideTelemetryDaoFactory(Provider<IoTLogicDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public TelemetryDao get() {
    return provideTelemetryDao(databaseProvider.get());
  }

  public static AppModule_ProvideTelemetryDaoFactory create(
      Provider<IoTLogicDatabase> databaseProvider) {
    return new AppModule_ProvideTelemetryDaoFactory(databaseProvider);
  }

  public static TelemetryDao provideTelemetryDao(IoTLogicDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideTelemetryDao(database));
  }
}
