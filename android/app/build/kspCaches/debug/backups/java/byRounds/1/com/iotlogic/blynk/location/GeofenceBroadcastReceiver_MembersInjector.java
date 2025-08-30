package com.iotlogic.blynk.location;

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
public final class GeofenceBroadcastReceiver_MembersInjector implements MembersInjector<GeofenceBroadcastReceiver> {
  private final Provider<LocationManager> locationManagerProvider;

  public GeofenceBroadcastReceiver_MembersInjector(
      Provider<LocationManager> locationManagerProvider) {
    this.locationManagerProvider = locationManagerProvider;
  }

  public static MembersInjector<GeofenceBroadcastReceiver> create(
      Provider<LocationManager> locationManagerProvider) {
    return new GeofenceBroadcastReceiver_MembersInjector(locationManagerProvider);
  }

  @Override
  public void injectMembers(GeofenceBroadcastReceiver instance) {
    injectLocationManager(instance, locationManagerProvider.get());
  }

  @InjectedFieldSignature("com.iotlogic.blynk.location.GeofenceBroadcastReceiver.locationManager")
  public static void injectLocationManager(GeofenceBroadcastReceiver instance,
      LocationManager locationManager) {
    instance.locationManager = locationManager;
  }
}
