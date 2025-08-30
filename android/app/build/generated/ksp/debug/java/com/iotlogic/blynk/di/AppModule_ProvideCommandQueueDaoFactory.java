package com.iotlogic.blynk.di;

import com.iotlogic.blynk.data.local.IoTLogicDatabase;
import com.iotlogic.blynk.data.local.dao.CommandQueueDao;
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
public final class AppModule_ProvideCommandQueueDaoFactory implements Factory<CommandQueueDao> {
  private final Provider<IoTLogicDatabase> databaseProvider;

  public AppModule_ProvideCommandQueueDaoFactory(Provider<IoTLogicDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public CommandQueueDao get() {
    return provideCommandQueueDao(databaseProvider.get());
  }

  public static AppModule_ProvideCommandQueueDaoFactory create(
      Provider<IoTLogicDatabase> databaseProvider) {
    return new AppModule_ProvideCommandQueueDaoFactory(databaseProvider);
  }

  public static CommandQueueDao provideCommandQueueDao(IoTLogicDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideCommandQueueDao(database));
  }
}
