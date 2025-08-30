package com.iotlogic.blynk.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iotlogic.blynk.ui.theme.IoTLogicTheme
import com.iotlogic.blynk.ui.viewmodel.NotificationSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.loadNotificationSettings()
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    text = "Notification Settings",
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
            }
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // System notifications status
            item {
                NotificationSystemStatusCard(
                    areNotificationsEnabled = uiState.areSystemNotificationsEnabled,
                    onOpenSettings = { viewModel.openSystemNotificationSettings() }
                )
            }
            
            // General notification categories
            item {
                Text(
                    text = "Notification Categories",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                NotificationCategoryCard(
                    title = "Device Alerts",
                    description = "Critical alerts from your IoT devices",
                    icon = Icons.Default.DeviceHub,
                    isEnabled = uiState.deviceAlertsEnabled,
                    onToggle = { viewModel.toggleDeviceAlerts(it) }
                )
            }
            
            item {
                NotificationCategoryCard(
                    title = "System Updates",
                    description = "App updates and system maintenance notifications",
                    icon = Icons.Default.SystemUpdate,
                    isEnabled = uiState.systemUpdatesEnabled,
                    onToggle = { viewModel.toggleSystemUpdates(it) }
                )
            }
            
            item {
                NotificationCategoryCard(
                    title = "Location Alerts",
                    description = "Geofence entry/exit notifications",
                    icon = Icons.Default.LocationOn,
                    isEnabled = uiState.geofenceAlertsEnabled,
                    onToggle = { viewModel.toggleGeofenceAlerts(it) }
                )
            }
            
            item {
                NotificationCategoryCard(
                    title = "Connection Status",
                    description = "Device connection and disconnection alerts",
                    icon = Icons.Default.Wifi,
                    isEnabled = uiState.connectionAlertsEnabled,
                    onToggle = { viewModel.toggleConnectionAlerts(it) }
                )
            }
            
            // Advanced settings
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Advanced Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                QuietHoursCard(
                    isEnabled = uiState.quietHoursEnabled,
                    startTime = uiState.quietHoursStart,
                    endTime = uiState.quietHoursEnd,
                    onToggle = { viewModel.toggleQuietHours(it) },
                    onStartTimeChanged = { viewModel.setQuietHoursStart(it) },
                    onEndTimeChanged = { viewModel.setQuietHoursEnd(it) }
                )
            }
            
            item {
                PriorityFilterCard(
                    minimumPriority = uiState.minimumPriority,
                    onPriorityChanged = { viewModel.setMinimumPriority(it) }
                )
            }
            
            // Notification history and management
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Management",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                ManagementActionsCard(
                    onClearHistory = { viewModel.clearNotificationHistory() },
                    onTestNotification = { viewModel.sendTestNotification() },
                    onResetSettings = { viewModel.resetToDefaults() }
                )
            }
            
            // FCM status
            item {
                FCMStatusCard(
                    fcmToken = uiState.fcmToken,
                    isTokenRegistered = uiState.isTokenRegistered,
                    onRefreshToken = { viewModel.refreshFCMToken() }
                )
            }
        }
    }
}

@Composable
private fun NotificationSystemStatusCard(
    areNotificationsEnabled: Boolean,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = if (areNotificationsEnabled) {
            CardDefaults.cardColors()
        } else {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (areNotificationsEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (areNotificationsEnabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (areNotificationsEnabled) "Notifications Enabled" else "Notifications Disabled",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (areNotificationsEnabled) {
                        "Your device can receive push notifications"
                    } else {
                        "Enable notifications in system settings to receive alerts"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (!areNotificationsEnabled) {
                TextButton(onClick = onOpenSettings) {
                    Text("Open Settings")
                }
            }
        }
    }
}

@Composable
private fun NotificationCategoryCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle
            )
        }
    }
}

@Composable
private fun QuietHoursCard(
    isEnabled: Boolean,
    startTime: String,
    endTime: String,
    onToggle: (Boolean) -> Unit,
    onStartTimeChanged: (String) -> Unit,
    onEndTimeChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.NightlightRound,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Quiet Hours",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Disable non-critical notifications during specified hours",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggle
                )
            }
            
            if (isEnabled) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { /* Open time picker for start time */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Start: $startTime")
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    OutlinedButton(
                        onClick = { /* Open time picker for end time */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("End: $endTime")
                    }
                }
            }
        }
    }
}

@Composable
private fun PriorityFilterCard(
    minimumPriority: String,
    onPriorityChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PriorityHigh,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Priority Filter",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Only show notifications above selected priority level",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            var expanded by remember { mutableStateOf(false) }
            val priorities = listOf("All", "Low", "Medium", "High", "Critical")
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = minimumPriority,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Minimum Priority") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    priorities.forEach { priority ->
                        DropdownMenuItem(
                            text = { Text(priority) },
                            onClick = {
                                onPriorityChanged(priority)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ManagementActionsCard(
    onClearHistory: () -> Unit,
    onTestNotification: () -> Unit,
    onResetSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = onTestNotification,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Test Notification")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = onClearHistory,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear Notification History")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = onResetSettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.RestoreFromTrash,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset to Defaults")
            }
        }
    }
}

@Composable
private fun FCMStatusCard(
    fcmToken: String?,
    isTokenRegistered: Boolean,
    onRefreshToken: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Firebase Cloud Messaging",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isTokenRegistered) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = if (isTokenRegistered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isTokenRegistered) "Token Registered" else "Token Not Registered",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isTokenRegistered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            
            if (fcmToken != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Token: ${fcmToken.take(20)}...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = onRefreshToken,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Refresh Token")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationSettingsScreenPreview() {
    IoTLogicTheme {
        // Preview would require mock data
    }
}