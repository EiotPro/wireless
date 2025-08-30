package com.iotlogic.blynk.ui.screens.devices

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iotlogic.blynk.domain.model.Device
import com.iotlogic.blynk.domain.model.Telemetry
import com.iotlogic.blynk.ui.components.ErrorMessage
import com.iotlogic.blynk.ui.components.LoadingIndicator
import com.iotlogic.blynk.ui.components.PullToRefreshBox
import com.iotlogic.blynk.ui.theme.IoTLogicTheme
import com.iotlogic.blynk.ui.viewmodel.DeviceViewModel
import com.iotlogic.blynk.ui.viewmodel.TelemetryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailScreen(
    deviceId: String,
    onNavigateBack: () -> Unit,
    onEditDevice: (Device) -> Unit,
    onConfigureDevice: (Device) -> Unit,
    deviceViewModel: DeviceViewModel = hiltViewModel(),
    telemetryViewModel: TelemetryViewModel = hiltViewModel()
) {
    val deviceUiState by deviceViewModel.uiState.collectAsStateWithLifecycle()
    val device by deviceViewModel.selectedDevice.collectAsStateWithLifecycle()
    val isConnected by deviceViewModel.isDeviceConnected(deviceId).collectAsStateWithLifecycle(false)
    
    val telemetryUiState by telemetryViewModel.uiState.collectAsStateWithLifecycle()
    val telemetryData by telemetryViewModel.getDeviceTelemetry(deviceId).collectAsStateWithLifecycle(emptyList())
    
    LaunchedEffect(deviceId) {
        deviceViewModel.selectDevice(deviceId)
        telemetryViewModel.loadTelemetryForDevice(deviceId)
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    text = device?.name ?: "Device Details",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
                device?.let { currentDevice ->
                    IconButton(onClick = { onConfigureDevice(currentDevice) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configure device"
                        )
                    }
                    IconButton(onClick = { onEditDevice(currentDevice) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit device"
                        )
                    }
                }
            }
        )
        
        // Content
        when {
            deviceUiState.isLoading && device == null -> {
                LoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    message = "Loading device details..."
                )
            }
            
            deviceUiState.error != null -> {
                ErrorMessage(
                    message = deviceUiState.error,
                    onRetry = { deviceViewModel.selectDevice(deviceId) },
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            }
            
            device != null -> {
                PullToRefreshBox(
                    isRefreshing = deviceUiState.isLoading || telemetryUiState.isLoading,
                    onRefresh = {
                        deviceViewModel.refreshDevices()
                        telemetryViewModel.refreshTelemetry(deviceId)
                    }
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Device Info Card
                        item {
                            DeviceInfoCard(
                                device = device!!,
                                isConnected = isConnected,
                                onToggleConnection = {
                                    if (isConnected) {
                                        deviceViewModel.disconnectDevice(deviceId)
                                    } else {
                                        deviceViewModel.connectDevice(deviceId)
                                    }
                                }
                            )
                        }
                        
                        // Device Controls Card
                        if (isConnected) {
                            item {
                                DeviceControlsCard(
                                    device = device!!,
                                    onSendCommand = { command, value ->
                                        deviceViewModel.sendDeviceCommand(deviceId, command, value)
                                    }
                                )
                            }
                        }
                        
                        // Telemetry Card
                        item {
                            TelemetryCard(
                                telemetryData = telemetryData,
                                isLoading = telemetryUiState.isLoading,
                                error = telemetryUiState.error,
                                onRefresh = { telemetryViewModel.refreshTelemetry(deviceId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceInfoCard(
    device: Device,
    isConnected: Boolean,
    onToggleConnection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Device Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Button(
                    onClick = onToggleConnection,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isConnected) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (isConnected) Icons.Default.CloudOff else Icons.Default.CloudQueue,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isConnected) "Disconnect" else "Connect")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Device info grid
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DeviceInfoRow("Name", device.name)
                DeviceInfoRow("Type", device.type)
                DeviceInfoRow("Protocol", device.protocol)
                if (device.location.isNotEmpty()) {
                    DeviceInfoRow("Location", device.location)
                }
                if (device.ipAddress.isNotEmpty()) {
                    DeviceInfoRow("IP Address", device.ipAddress)
                }
                if (device.macAddress.isNotEmpty()) {
                    DeviceInfoRow("MAC Address", device.macAddress)
                }
                if (device.description.isNotEmpty()) {
                    DeviceInfoRow("Description", device.description)
                }
                DeviceInfoRow("Status", if (isConnected) "Connected" else "Disconnected")
                if (device.lastSeen > 0) {
                    DeviceInfoRow("Last Seen", formatTimestamp(device.lastSeen))
                }
            }
        }
    }
}

@Composable
private fun DeviceInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DeviceControlsCard(
    device: Device,
    onSendCommand: (String, Any?) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Device Controls",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Dynamic controls based on device type
            when (device.type.lowercase()) {
                "light", "led" -> {
                    LightControls(onSendCommand)
                }
                "sensor" -> {
                    SensorControls(onSendCommand)
                }
                "switch", "relay" -> {
                    SwitchControls(onSendCommand)
                }
                else -> {
                    GenericControls(onSendCommand)
                }
            }
        }
    }
}

@Composable
private fun LightControls(onSendCommand: (String, Any?) -> Unit) {
    var isOn by remember { mutableStateOf(false) }
    var brightness by remember { mutableStateOf(50f) }
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Power", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = isOn,
                onCheckedChange = { 
                    isOn = it
                    onSendCommand("power", it)
                }
            )
        }
        
        if (isOn) {
            Column {
                Text("Brightness", style = MaterialTheme.typography.bodyLarge)
                Slider(
                    value = brightness,
                    onValueChange = { brightness = it },
                    onValueChangeFinished = {
                        onSendCommand("brightness", brightness.toInt())
                    },
                    valueRange = 0f..100f,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${brightness.toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SensorControls(onSendCommand: (String, Any?) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = { onSendCommand("read_sensors", null) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Read Sensors")
        }
        
        OutlinedButton(
            onClick = { onSendCommand("calibrate", null) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.Tune, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Calibrate")
        }
    }
}

@Composable
private fun SwitchControls(onSendCommand: (String, Any?) -> Unit) {
    var isOn by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Switch State", style = MaterialTheme.typography.bodyLarge)
        Switch(
            checked = isOn,
            onCheckedChange = { 
                isOn = it
                onSendCommand("switch", it)
            }
        )
    }
}

@Composable
private fun GenericControls(onSendCommand: (String, Any?) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = { onSendCommand("status", null) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.Info, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Get Status")
        }
        
        OutlinedButton(
            onClick = { onSendCommand("restart", null) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Restart Device")
        }
    }
}

@Composable
private fun TelemetryCard(
    telemetryData: List<Telemetry>,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Telemetry",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh telemetry"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when {
                isLoading -> {
                    LoadingIndicator(message = "Loading telemetry...")
                }
                
                error != null -> {
                    ErrorMessage(
                        message = error,
                        onRetry = onRefresh
                    )
                }
                
                telemetryData.isEmpty() -> {
                    Text(
                        text = "No telemetry data available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        telemetryData.take(5).forEach { telemetry ->
                            TelemetryRow(telemetry = telemetry)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetryRow(
    telemetry: Telemetry,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = telemetry.sensorType.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatTimestamp(telemetry.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = "${telemetry.value} ${telemetry.unit ?: ""}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = when (telemetry.quality) {
                    "GOOD" -> Color.Green
                    "QUESTIONABLE" -> Color.Yellow
                    "BAD" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        else -> "${diff / 86400_000}d ago"
    }
}

@Preview(showBackground = true)
@Composable
private fun DeviceDetailScreenPreview() {
    IoTLogicTheme {
        // Preview would require mock data
    }
}