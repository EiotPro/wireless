package com.iotlogic.blynk.data.sync;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.iotlogic.blynk.data.local.preferences.AuthPreferences;
import dagger.internal.DaggerGenerated;
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
public final class SyncWorker_Factory {
  private final Provider<SyncManager> syncManagerProvider;

  private final Provider<AuthPreferences> authPreferencesProvider;

  public SyncWorker_Factory(Provider<SyncManager> syncManagerProvider,
      Provider<AuthPreferences> authPreferencesProvider) {
    this.syncManagerProvider = syncManagerProvider;
    this.authPreferencesProvider = authPreferencesProvider;
  }

  public SyncWorker get(Context context, WorkerParameters workerParams) {
    return newInstance(context, workerParams, syncManagerProvider.get(), authPreferencesProvider.get());
  }

  public static SyncWorker_Factory create(Provider<SyncManager> syncManagerProvider,
      Provider<AuthPreferences> authPreferencesProvider) {
    return new SyncWorker_Factory(syncManagerProvider, authPreferencesProvider);
  }

  public static SyncWorker newInstance(Context context, WorkerParameters workerParams,
      SyncManager syncManager, AuthPreferences authPreferences) {
    return new SyncWorker(context, workerParams, syncManager, authPreferences);
  }
}
