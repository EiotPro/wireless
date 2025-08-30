package com.iotlogic.blynk.data.sync;

import android.content.Context;
import com.iotlogic.blynk.data.local.dao.CommandQueueDao;
import com.iotlogic.blynk.data.local.dao.DeviceDao;
import com.iotlogic.blynk.data.local.dao.TelemetryDao;
import com.iotlogic.blynk.data.local.preferences.AuthPreferences;
import com.iotlogic.blynk.data.remote.ApiService;
import com.iotlogic.blynk.hardware.HardwareManager;
import com.iotlogic.blynk.utils.NetworkUtils;
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
public final class SyncManager_Factory implements Factory<SyncManager> {
  private final Provider<Context> contextProvider;

  private final Provider<CommandQueueDao> commandQueueDaoProvider;

  private final Provider<DeviceDao> deviceDaoProvider;

  private final Provider<TelemetryDao> telemetryDaoProvider;

  private final Provider<ApiService> apiServiceProvider;

  private final Provider<HardwareManager> hardwareManagerProvider;

  private final Provider<NetworkUtils> networkUtilsProvider;

  private final Provider<AuthPreferences> authPreferencesProvider;

  public SyncManager_Factory(Provider<Context> contextProvider,
      Provider<CommandQueueDao> commandQueueDaoProvider, Provider<DeviceDao> deviceDaoProvider,
      Provider<TelemetryDao> telemetryDaoProvider, Provider<ApiService> apiServiceProvider,
      Provider<HardwareManager> hardwareManagerProvider,
      Provider<NetworkUtils> networkUtilsProvider,
      Provider<AuthPreferences> authPreferencesProvider) {
    this.contextProvider = contextProvider;
    this.commandQueueDaoProvider = commandQueueDaoProvider;
    this.deviceDaoProvider = deviceDaoProvider;
    this.telemetryDaoProvider = telemetryDaoProvider;
    this.apiServiceProvider = apiServiceProvider;
    this.hardwareManagerProvider = hardwareManagerProvider;
    this.networkUtilsProvider = networkUtilsProvider;
    this.authPreferencesProvider = authPreferencesProvider;
  }

  @Override
  public SyncManager get() {
    return newInstance(contextProvider.get(), commandQueueDaoProvider.get(), deviceDaoProvider.get(), telemetryDaoProvider.get(), apiServiceProvider.get(), hardwareManagerProvider.get(), networkUtilsProvider.get(), authPreferencesProvider.get());
  }

  public static SyncManager_Factory create(Provider<Context> contextProvider,
      Provider<CommandQueueDao> commandQueueDaoProvider, Provider<DeviceDao> deviceDaoProvider,
      Provider<TelemetryDao> telemetryDaoProvider, Provider<ApiService> apiServiceProvider,
      Provider<HardwareManager> hardwareManagerProvider,
      Provider<NetworkUtils> networkUtilsProvider,
      Provider<AuthPreferences> authPreferencesProvider) {
    return new SyncManager_Factory(contextProvider, commandQueueDaoProvider, deviceDaoProvider, telemetryDaoProvider, apiServiceProvider, hardwareManagerProvider, networkUtilsProvider, authPreferencesProvider);
  }

  public static SyncManager newInstance(Context context, CommandQueueDao commandQueueDao,
      DeviceDao deviceDao, TelemetryDao telemetryDao, ApiService apiService,
      HardwareManager hardwareManager, NetworkUtils networkUtils, AuthPreferences authPreferences) {
    return new SyncManager(context, commandQueueDao, deviceDao, telemetryDao, apiService, hardwareManager, networkUtils, authPreferences);
  }
}
