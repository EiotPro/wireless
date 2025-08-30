package com.iotlogic.blynk.ui.viewmodel;

import android.content.Context;
import com.iotlogic.blynk.notifications.AppNotificationManager;
import com.iotlogic.blynk.notifications.FCMTokenManager;
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
public final class NotificationSettingsViewModel_Factory implements Factory<NotificationSettingsViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<AppNotificationManager> notificationManagerProvider;

  private final Provider<FCMTokenManager> fcmTokenManagerProvider;

  public NotificationSettingsViewModel_Factory(Provider<Context> contextProvider,
      Provider<AppNotificationManager> notificationManagerProvider,
      Provider<FCMTokenManager> fcmTokenManagerProvider) {
    this.contextProvider = contextProvider;
    this.notificationManagerProvider = notificationManagerProvider;
    this.fcmTokenManagerProvider = fcmTokenManagerProvider;
  }

  @Override
  public NotificationSettingsViewModel get() {
    return newInstance(contextProvider.get(), notificationManagerProvider.get(), fcmTokenManagerProvider.get());
  }

  public static NotificationSettingsViewModel_Factory create(Provider<Context> contextProvider,
      Provider<AppNotificationManager> notificationManagerProvider,
      Provider<FCMTokenManager> fcmTokenManagerProvider) {
    return new NotificationSettingsViewModel_Factory(contextProvider, notificationManagerProvider, fcmTokenManagerProvider);
  }

  public static NotificationSettingsViewModel newInstance(Context context,
      AppNotificationManager notificationManager, FCMTokenManager fcmTokenManager) {
    return new NotificationSettingsViewModel(context, notificationManager, fcmTokenManager);
  }
}
