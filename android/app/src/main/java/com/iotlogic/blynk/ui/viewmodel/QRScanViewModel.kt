package com.iotlogic.blynk.ui.viewmodel

import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iotlogic.blynk.camera.QRCodeScanner
import com.iotlogic.blynk.camera.QRScanResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QRScanViewModel @Inject constructor(
    private val qrCodeScanner: QRCodeScanner
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(QRScanUiState())
    val uiState: StateFlow<QRScanUiState> = _uiState.asStateFlow()
    
    // Last scan result
    private val _lastScanResult = MutableStateFlow<QRScanResult?>(null)
    val lastScanResult: StateFlow<QRScanResult?> = _lastScanResult.asStateFlow()
    
    // Camera preview view
    private var previewView: PreviewView? = null
    
    init {
        // Collect scan results
        viewModelScope.launch {
            qrCodeScanner.scanResults.collect { result ->
                _lastScanResult.value = result
                // Clear result after a delay to allow for new scans
                kotlinx.coroutines.delay(3000)
                _lastScanResult.value = null
            }
        }
    }
    
    /**
     * Set camera preview view
     */
    fun setCameraPreview(previewView: PreviewView) {
        this.previewView = previewView
    }
    
    /**
     * Start QR code scanning
     */
    fun startScanning(lifecycleOwner: LifecycleOwner) {
        val preview = previewView
        if (preview != null) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val result = qrCodeScanner.startScanning(lifecycleOwner, preview)
                
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isScanning = true,
                        hasFlash = qrCodeScanner.hasFlash(),
                        isFlashOn = qrCodeScanner.isFlashOn()
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isScanning = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to start camera"
                    )
                }
            }
        }
    }
    
    /**
     * Stop QR code scanning
     */
    fun stopScanning() {
        qrCodeScanner.stopScanning()
        _uiState.value = _uiState.value.copy(
            isScanning = false,
            isLoading = false
        )
    }
    
    /**
     * Toggle camera flash
     */
    fun toggleFlash() {
        if (_uiState.value.hasFlash) {
            val isFlashOn = qrCodeScanner.toggleFlash()
            _uiState.value = _uiState.value.copy(isFlashOn = isFlashOn)
        }
    }
    
    /**
     * Clear last scan result
     */
    fun clearScanResult() {
        _lastScanResult.value = null
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    override fun onCleared() {
        super.onCleared()
        qrCodeScanner.release()
    }
}

/**
 * UI state for QR scanning screen
 */
data class QRScanUiState(
    val isLoading: Boolean = false,
    val isScanning: Boolean = false,
    val hasFlash: Boolean = false,
    val isFlashOn: Boolean = false,
    val error: String? = null
)