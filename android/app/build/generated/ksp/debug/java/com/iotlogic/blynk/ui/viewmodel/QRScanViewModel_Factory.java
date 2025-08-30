package com.iotlogic.blynk.ui.viewmodel;

import com.iotlogic.blynk.camera.QRCodeScanner;
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
public final class QRScanViewModel_Factory implements Factory<QRScanViewModel> {
  private final Provider<QRCodeScanner> qrCodeScannerProvider;

  public QRScanViewModel_Factory(Provider<QRCodeScanner> qrCodeScannerProvider) {
    this.qrCodeScannerProvider = qrCodeScannerProvider;
  }

  @Override
  public QRScanViewModel get() {
    return newInstance(qrCodeScannerProvider.get());
  }

  public static QRScanViewModel_Factory create(Provider<QRCodeScanner> qrCodeScannerProvider) {
    return new QRScanViewModel_Factory(qrCodeScannerProvider);
  }

  public static QRScanViewModel newInstance(QRCodeScanner qrCodeScanner) {
    return new QRScanViewModel(qrCodeScanner);
  }
}
