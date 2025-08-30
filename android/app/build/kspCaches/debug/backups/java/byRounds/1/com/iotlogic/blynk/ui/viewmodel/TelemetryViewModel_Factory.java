package com.iotlogic.blynk.ui.viewmodel;

import com.iotlogic.blynk.domain.repository.TelemetryRepository;
import com.iotlogic.blynk.hardware.HardwareManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class TelemetryViewModel_Factory implements Factory<TelemetryViewModel> {
  private final Provider<TelemetryRepository> telemetryRepositoryProvider;

  private final Provider<HardwareManager> hardwareManagerProvider;

  public TelemetryViewModel_Factory(Provider<TelemetryRepository> telemetryRepositoryProvider,
      Provider<HardwareManager> hardwareManagerProvider) {
    this.telemetryRepositoryProvider = telemetryRepositoryProvider;
    this.hardwareManagerProvider = hardwareManagerProvider;
  }

  @Override
  public TelemetryViewModel get() {
    return newInstance(telemetryRepositoryProvider.get(), hardwareManagerProvider.get());
  }

  public static TelemetryViewModel_Factory create(
      Provider<TelemetryRepository> telemetryRepositoryProvider,
      Provider<HardwareManager> hardwareManagerProvider) {
    return new TelemetryViewModel_Factory(telemetryRepositoryProvider, hardwareManagerProvider);
  }

  public static TelemetryViewModel newInstance(TelemetryRepository telemetryRepository,
      HardwareManager hardwareManager) {
    return new TelemetryViewModel(telemetryRepository, hardwareManager);
  }
}
