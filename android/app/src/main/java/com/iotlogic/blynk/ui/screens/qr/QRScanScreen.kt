package com.iotlogic.blynk.ui.screens.qr

import android.Manifest
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.iotlogic.blynk.camera.DeviceProvisioningInfo
import com.iotlogic.blynk.camera.QRScanResult
import com.iotlogic.blynk.camera.WiFiConfigurationInfo
import com.iotlogic.blynk.ui.components.LoadingIndicator
import com.iotlogic.blynk.ui.theme.IoTLogicTheme
import com.iotlogic.blynk.ui.viewmodel.QRScanViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun QRScanScreen(
    onNavigateBack: () -> Unit,
    onDeviceProvisioned: (DeviceProvisioningInfo) -> Unit,
    onWiFiConfigured: (WiFiConfigurationInfo) -> Unit,
    viewModel: QRScanViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scanResult by viewModel.lastScanResult.collectAsStateWithLifecycle()
    
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    LaunchedEffect(Unit) {
        if (cameraPermissionState.status.isGranted) {
            viewModel.startScanning(lifecycleOwner)
        }
    }
    
    // Handle scan results
    LaunchedEffect(scanResult) {
        scanResult?.let { result ->
            when (result) {
                is QRScanResult.DeviceProvisioning -> {
                    onDeviceProvisioned(result.deviceInfo)
                }
                is QRScanResult.WiFiConfiguration -> {
                    onWiFiConfigured(result.wifiInfo)
                }
                is QRScanResult.Text -> {
                    // Handle generic text QR codes
                }
            }
        }
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    text = "Scan QR Code",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                if (uiState.hasFlash) {
                    IconButton(
                        onClick = { viewModel.toggleFlash() }
                    ) {
                        Icon(
                            imageVector = if (uiState.isFlashOn) Icons.Default.FlashOff else Icons.Default.FlashOn,
                            contentDescription = if (uiState.isFlashOn) "Turn off flash" else "Turn on flash"
                        )
                    }
                }
            }
        )
        
        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                !cameraPermissionState.status.isGranted -> {
                    CameraPermissionContent(
                        onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                uiState.isLoading -> {
                    LoadingIndicator(
                        message = "Starting camera...",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error,
                        onRetry = { viewModel.startScanning(lifecycleOwner) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                else -> {
                    // Camera preview with overlay
                    CameraPreviewWithOverlay(
                        onCameraPreviewReady = { previewView ->
                            viewModel.setCameraPreview(previewView)
                        },
                        scanResult = scanResult,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
    
    // Handle lifecycle
    DisposableEffect(lifecycleOwner) {
        onDispose {
            viewModel.stopScanning()
        }
    }
}

@Composable
private fun CameraPermissionContent(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Camera,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "This app needs camera access to scan QR codes for device provisioning.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onRequestPermission) {
            Icon(
                imageVector = Icons.Default.Camera,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Grant Camera Permission")
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Camera Error",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onRetry) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Try Again")
        }
    }
}

@Composable
private fun CameraPreviewWithOverlay(
    onCameraPreviewReady: (PreviewView) -> Unit,
    scanResult: QRScanResult?,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Camera preview
        AndroidView(
            factory = { context ->
                PreviewView(context).apply {
                    onCameraPreviewReady(this)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Scan overlay
        ScanOverlay(
            modifier = Modifier.fillMaxSize()
        )
        
        // Instructions
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            )
        ) {
            Text(
                text = "Point camera at QR code to scan",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
        
        // Scan result display
        scanResult?.let { result ->
            ScanResultCard(
                result = result,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun ScanOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Semi-transparent background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )
        
        // Scan area
        Box(
            modifier = Modifier
                .size(250.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            // Corner indicators
            val cornerSize = 20.dp
            val cornerThickness = 4.dp
            
            // Top-left corner
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(cornerSize)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cornerThickness)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(topStart = 16.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .width(cornerThickness)
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(topStart = 16.dp)
                        )
                )
            }
            
            // Top-right corner
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(cornerSize)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cornerThickness)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(topEnd = 16.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .width(cornerThickness)
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(topEnd = 16.dp)
                        )
                )
            }
            
            // Bottom-left corner
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(cornerSize)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .height(cornerThickness)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(bottomStart = 16.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .width(cornerThickness)
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(bottomStart = 16.dp)
                        )
                )
            }
            
            // Bottom-right corner
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(cornerSize)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .fillMaxWidth()
                        .height(cornerThickness)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(bottomEnd = 16.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .width(cornerThickness)
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(bottomEnd = 16.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun ScanResultCard(
    result: QRScanResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (result) {
                        is QRScanResult.DeviceProvisioning -> "Device Found"
                        is QRScanResult.WiFiConfiguration -> "WiFi Configuration"
                        is QRScanResult.Text -> "QR Code Scanned"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = when (result) {
                    is QRScanResult.DeviceProvisioning -> {
                        "Device: ${result.deviceInfo.deviceName ?: "Unknown"}\n" +
                        "Type: ${result.deviceInfo.deviceType ?: "Unknown"}"
                    }
                    is QRScanResult.WiFiConfiguration -> {
                        "Network: ${result.wifiInfo.ssid}\n" +
                        "Security: ${result.wifiInfo.security}"
                    }
                    is QRScanResult.Text -> {
                        result.content.take(100) + if (result.content.length > 100) "..." else ""
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QRScanScreenPreview() {
    IoTLogicTheme {
        // Preview would require mock data
    }
}