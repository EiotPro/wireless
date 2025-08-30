package com.iotlogic.blynk.camera

import android.content.Context
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QRCodeScanner @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    
    private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()
    
    private val _scanResults = Channel<QRScanResult>()
    val scanResults: Flow<QRScanResult> = _scanResults.receiveAsFlow()
    
    /**
     * Start camera preview and QR scanning
     */
    suspend fun startScanning(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ): Result<Unit> {
        return try {
            val cameraProvider = ProcessCameraProvider.getInstance(context).get()
            this.cameraProvider = cameraProvider
            
            // Preview
            preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            
            // Image analyzer for QR code detection
            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, QRCodeAnalyzer { result ->
                        _scanResults.trySend(result)
                    })
                }
            
            // Select camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                
                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
                
                Result.success(Unit)
            } catch (exc: Exception) {
                Result.failure(exc)
            }
        } catch (exc: Exception) {
            Result.failure(exc)
        }
    }
    
    /**
     * Stop camera scanning
     */
    fun stopScanning() {
        cameraProvider?.unbindAll()
        camera = null
        preview = null
        imageAnalyzer = null
    }
    
    /**
     * Toggle camera flash
     */
    fun toggleFlash(): Boolean {
        return camera?.let { cam ->
            val currentState = cam.cameraInfo.torchState.value == TorchState.ON
            cam.cameraControl.enableTorch(!currentState)
            !currentState
        } ?: false
    }
    
    /**
     * Check if flash is available
     */
    fun hasFlash(): Boolean {
        return camera?.cameraInfo?.hasFlashUnit() ?: false
    }
    
    /**
     * Get current flash state
     */
    fun isFlashOn(): Boolean {
        return camera?.cameraInfo?.torchState?.value == TorchState.ON
    }
    
    /**
     * Release resources
     */
    fun release() {
        stopScanning()
        cameraExecutor.shutdown()
        barcodeScanner.close()
    }
    
    /**
     * Image analyzer for QR code detection
     */
    private inner class QRCodeAnalyzer(
        private val onQRCodeDetected: (QRScanResult) -> Unit
    ) : ImageAnalysis.Analyzer {
        
        @androidx.camera.core.ExperimentalGetImage
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                )
                
                barcodeScanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        for (barcode in barcodes) {
                            when (barcode.format) {
                                Barcode.FORMAT_QR_CODE -> {
                                    val result = processQRCode(barcode)
                                    if (result != null) {
                                        onQRCodeDetected(result)
                                    }
                                }
                                else -> {
                                    // Handle other barcode formats if needed
                                }
                            }
                        }
                    }
                    .addOnFailureListener {
                        // Handle failure
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }
        
        /**
         * Process QR code and extract device information
         */
        private fun processQRCode(barcode: Barcode): QRScanResult? {
            val rawValue = barcode.rawValue ?: return null
            
            return try {
                // Try to parse as JSON first (for IoT device provisioning)
                val deviceInfo = parseDeviceProvisioningQR(rawValue)
                if (deviceInfo != null) {
                    QRScanResult.DeviceProvisioning(deviceInfo)
                } else {
                    // Try to parse as WiFi config
                    val wifiInfo = parseWiFiQR(rawValue)
                    if (wifiInfo != null) {
                        QRScanResult.WiFiConfiguration(wifiInfo)
                    } else {
                        // Generic text QR code
                        QRScanResult.Text(rawValue)
                    }
                }
            } catch (e: Exception) {
                QRScanResult.Text(rawValue)
            }
        }
        
        /**
         * Parse device provisioning QR code
         * Expected format: JSON with device information
         */
        private fun parseDeviceProvisioningQR(qrData: String): DeviceProvisioningInfo? {
            return try {
                // This would typically use a JSON parser like Gson
                // For now, we'll use a simple approach
                if (qrData.startsWith("{") && qrData.endsWith("}")) {
                    // Parse JSON manually for demo purposes
                    // In real implementation, use proper JSON parsing
                    DeviceProvisioningInfo(
                        deviceId = extractJsonValue(qrData, "deviceId"),
                        deviceName = extractJsonValue(qrData, "name"),
                        deviceType = extractJsonValue(qrData, "type"),
                        protocol = extractJsonValue(qrData, "protocol"),
                        ipAddress = extractJsonValue(qrData, "ip"),
                        macAddress = extractJsonValue(qrData, "mac"),
                        securityKey = extractJsonValue(qrData, "key"),
                        provisioningToken = extractJsonValue(qrData, "token")
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
        
        /**
         * Parse WiFi configuration QR code
         * Expected format: WIFI:T:WPA;S:NetworkName;P:Password;H:false;;
         */
        private fun parseWiFiQR(qrData: String): WiFiConfigurationInfo? {
            return try {
                if (qrData.startsWith("WIFI:")) {
                    val parts = qrData.removePrefix("WIFI:").split(";")
                    var ssid = ""
                    var password = ""
                    var security = ""
                    var hidden = false
                    
                    parts.forEach { part ->
                        when {
                            part.startsWith("T:") -> security = part.removePrefix("T:")
                            part.startsWith("S:") -> ssid = part.removePrefix("S:")
                            part.startsWith("P:") -> password = part.removePrefix("P:")
                            part.startsWith("H:") -> hidden = part.removePrefix("H:").toBoolean()
                        }
                    }
                    
                    if (ssid.isNotEmpty()) {
                        WiFiConfigurationInfo(
                            ssid = ssid,
                            password = password,
                            security = security,
                            hidden = hidden
                        )
                    } else {
                        null
                    }
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
        
        /**
         * Simple JSON value extraction (for demo purposes)
         */
        private fun extractJsonValue(json: String, key: String): String? {
            val pattern = "\"$key\"\\s*:\\s*\"([^\"]*)\""
            val regex = Regex(pattern)
            return regex.find(json)?.groupValues?.get(1)
        }
    }
}

/**
 * QR scan result types
 */
sealed class QRScanResult {
    data class DeviceProvisioning(val deviceInfo: DeviceProvisioningInfo) : QRScanResult()
    data class WiFiConfiguration(val wifiInfo: WiFiConfigurationInfo) : QRScanResult()
    data class Text(val content: String) : QRScanResult()
}

/**
 * Device provisioning information from QR code
 */
data class DeviceProvisioningInfo(
    val deviceId: String?,
    val deviceName: String?,
    val deviceType: String?,
    val protocol: String?,
    val ipAddress: String?,
    val macAddress: String?,
    val securityKey: String?,
    val provisioningToken: String?
)

/**
 * WiFi configuration information from QR code
 */
data class WiFiConfigurationInfo(
    val ssid: String,
    val password: String,
    val security: String,
    val hidden: Boolean
)