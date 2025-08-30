package com.iotlogic.blynk;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class IoTLogicApplication_MembersInjector implements MembersInjector<IoTLogicApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public IoTLogicApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<IoTLogicApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new IoTLogicApplication_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(IoTLogicApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.iotlogic.blynk.IoTLogicApplication.workerFactory")
  public static void injectWorkerFactory(IoTLogicApplication instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
