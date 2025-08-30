package com.iotlogic.blynk.ui.screens.map

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.iotlogic.blynk.location.DeviceLocation
import com.iotlogic.blynk.location.GeofenceEvent
import com.iotlogic.blynk.location.NearbyDevice
import com.iotlogic.blynk.ui.components.LoadingIndicator
import com.iotlogic.blynk.ui.theme.IoTLogicTheme
import com.iotlogic.blynk.ui.viewmodel.DeviceMapViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DeviceMapScreen(
    onNavigateBack: () -> Unit,
    onDeviceClick: (String) -> Unit,
    viewModel: DeviceMapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()
    val deviceLocations by viewModel.deviceLocations.collectAsStateWithLifecycle()
    val nearbyDevices by viewModel.nearbyDevices.collectAsStateWithLifecycle()
    val geofenceEvents by viewModel.geofenceEvents.collectAsStateWithLifecycle()
    
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    
    var showDeviceList by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        if (locationPermissionsState.allPermissionsGranted) {
            viewModel.startLocationTracking()
            viewModel.loadDeviceLocations()
        }
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    text = "Device Map",
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
                IconButton(
                    onClick = { showDeviceList = !showDeviceList }
                ) {
                    Icon(
                        imageVector = if (showDeviceList) Icons.Default.Map else Icons.Default.List,
                        contentDescription = if (showDeviceList) "Show map" else "Show device list"
                    )
                }
                
                IconButton(
                    onClick = { viewModel.refreshLocation() }
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Refresh location"
                    )
                }
            }
        )
        
        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                !locationPermissionsState.allPermissionsGranted -> {
                    LocationPermissionContent(
                        onRequestPermissions = { locationPermissionsState.launchMultiplePermissionRequest() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                uiState.isLoading -> {
                    LoadingIndicator(
                        message = "Loading device locations...",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error,
                        onRetry = { 
                            viewModel.clearError()
                            viewModel.loadDeviceLocations()
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                showDeviceList -> {
                    DeviceListContent(
                        nearbyDevices = nearbyDevices,
                        onDeviceClick = onDeviceClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                else -> {
                    MapContent(
                        currentLocation = currentLocation,
                        deviceLocations = deviceLocations,
                        nearbyDevices = nearbyDevices,
                        onDeviceClick = onDeviceClick,
                        onLocationClick = { latLng ->
                            viewModel.addGeofenceAtLocation(latLng)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            // Location info overlay
            if (currentLocation != null && !showDeviceList) {
                LocationInfoCard(
                    latitude = currentLocation!!.latitude,
                    longitude = currentLocation!!.longitude,
                    accuracy = currentLocation!!.accuracy,
                    nearbyDevicesCount = nearbyDevices.size,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )
            }
            
            // Geofence events
            geofenceEvents.lastOrNull()?.let { event ->
                GeofenceEventCard(
                    event = event,
                    onDismiss = { viewModel.clearGeofenceEvent() },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun LocationPermissionContent(
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Location Permission Required",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "This app needs location access to show device locations on the map and provide geofencing features.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onRequestPermissions) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Grant Location Permission")
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
            text = "Location Error",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
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
private fun MapContent(
    currentLocation: android.location.Location?,
    deviceLocations: List<DeviceLocation>,
    nearbyDevices: List<NearbyDevice>,
    onDeviceClick: (String) -> Unit,
    onLocationClick: (LatLng) -> Unit,
    modifier: Modifier = Modifier
) {
    val defaultLocation = LatLng(37.7749, -122.4194) // San Francisco
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            currentLocation?.let { LatLng(it.latitude, it.longitude) } ?: defaultLocation,
            12f
        )
    }
    
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        onMapClick = onLocationClick,
        properties = MapProperties(
            isMyLocationEnabled = currentLocation != null
        )
    ) {
        // Current location marker
        currentLocation?.let { location ->
            Marker(
                state = MarkerState(position = LatLng(location.latitude, location.longitude)),
                title = "Your Location",
                snippet = "Current position"
            )
        }
        
        // Device markers
        deviceLocations.forEach { device ->
            val isNearby = nearbyDevices.any { it.deviceId == device.deviceId }
            
            Marker(
                state = MarkerState(position = LatLng(device.latitude, device.longitude)),
                title = device.deviceName,
                snippet = "Device ID: ${device.deviceId}",
                onClick = {
                    onDeviceClick(device.deviceId)
                    true
                }
            )
            
            // Geofence circle for nearby devices
            if (isNearby) {
                Circle(
                    center = LatLng(device.latitude, device.longitude),
                    radius = 100.0, // 100 meters
                    strokeColor = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2f,
                    fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
private fun DeviceListContent(
    nearbyDevices: List<NearbyDevice>,
    onDeviceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Nearby Devices (${nearbyDevices.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        if (nearbyDevices.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No nearby devices found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(nearbyDevices) { device ->
                NearbyDeviceCard(
                    device = device,
                    onClick = { onDeviceClick(device.deviceId) }
                )
            }
        }
    }
}

@Composable
private fun NearbyDeviceCard(
    device: NearbyDevice,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DeviceHub,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.deviceName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Distance: ${String.format("%.1f", device.distance)}m",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LocationInfoCard(
    latitude: Double,
    longitude: Double,
    accuracy: Float,
    nearbyDevicesCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Location Info",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Lat: ${String.format("%.6f", latitude)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Lng: ${String.format("%.6f", longitude)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Accuracy: ${accuracy.toInt()}m",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Nearby: $nearbyDevicesCount devices",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun GeofenceEventCard(
    event: GeofenceEvent,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (event.transition.name == "ENTER") Icons.Default.LocationOn else Icons.Default.LocationOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${event.transition.name} ${event.geofenceName}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Device geofence alert",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeviceMapScreenPreview() {
    IoTLogicTheme {
        // Preview would require mock data
    }
}