package com.iotlogic.blynk.notifications;

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
public final class IoTLogicFirebaseMessagingService_MembersInjector implements MembersInjector<IoTLogicFirebaseMessagingService> {
  private final Provider<AppNotificationManager> notificationManagerProvider;

  private final Provider<FCMTokenManager> fcmTokenManagerProvider;

  public IoTLogicFirebaseMessagingService_MembersInjector(
      Provider<AppNotificationManager> notificationManagerProvider,
      Provider<FCMTokenManager> fcmTokenManagerProvider) {
    this.notificationManagerProvider = notificationManagerProvider;
    this.fcmTokenManagerProvider = fcmTokenManagerProvider;
  }

  public static MembersInjector<IoTLogicFirebaseMessagingService> create(
      Provider<AppNotificationManager> notificationManagerProvider,
      Provider<FCMTokenManager> fcmTokenManagerProvider) {
    return new IoTLogicFirebaseMessagingService_MembersInjector(notificationManagerProvider, fcmTokenManagerProvider);
  }

  @Override
  public void injectMembers(IoTLogicFirebaseMessagingService instance) {
    injectNotificationManager(instance, notificationManagerProvider.get());
    injectFcmTokenManager(instance, fcmTokenManagerProvider.get());
  }

  @InjectedFieldSignature("com.iotlogic.blynk.notifications.IoTLogicFirebaseMessagingService.notificationManager")
  public static void injectNotificationManager(IoTLogicFirebaseMessagingService instance,
      AppNotificationManager notificationManager) {
    instance.notificationManager = notificationManager;
  }

  @InjectedFieldSignature("com.iotlogic.blynk.notifications.IoTLogicFirebaseMessagingService.fcmTokenManager")
  public static void injectFcmTokenManager(IoTLogicFirebaseMessagingService instance,
      FCMTokenManager fcmTokenManager) {
    instance.fcmTokenManager = fcmTokenManager;
  }
}
