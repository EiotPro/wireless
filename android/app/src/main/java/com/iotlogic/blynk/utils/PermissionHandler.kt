package com.iotlogic.blynk.utils

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Composable for handling permission requests with user-friendly UI
 */
@Composable
fun PermissionRequestScreen(
    permissionManager: PermissionManager,
    onPermissionsGranted: () -> Unit,
    onPermissionsDenied: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showRationale by remember { mutableStateOf(false) }
    var deniedPermissions by remember { mutableStateOf<List<String>>(emptyList()) }
    
    // Get required permissions
    val requiredPermissions = remember {
        permissionManager.getAllRequiredPermissions()
    }
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val denied = permissions.filter { !it.value }.keys.toList()
        
        if (denied.isEmpty()) {
            onPermissionsGranted()
        } else {
            deniedPermissions = denied
            onPermissionsDenied(denied)
        }
    }
    
    // Check current permission status
    val permissionStatus = remember(requiredPermissions) {
        requiredPermissions.associateWith { permission ->
            permissionManager.isPermissionGranted(permission)
        }
    }
    
    val hasAllPermissions = permissionStatus.values.all { it }
    
    LaunchedEffect(hasAllPermissions) {
        if (hasAllPermissions) {
            onPermissionsGranted()
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        // Header
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = "Permissions",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Permissions Required",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "IoTLogic needs these permissions to access hardware and provide IoT functionality",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Permission groups
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(PermissionManager.PermissionGroup.values()) { group ->
                PermissionGroupCard(
                    group = group,
                    permissionManager = permissionManager,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Action buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val missingPermissions = permissionManager.getAllMissingPermissions(
                        PermissionManager.PermissionGroup.values().toList()
                    )
                    if (missingPermissions.isNotEmpty()) {
                        permissionLauncher.launch(missingPermissions.toTypedArray())
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !hasAllPermissions
            ) {
                Text("Grant Permissions")
            }
            
            if (deniedPermissions.isNotEmpty()) {
                OutlinedButton(
                    onClick = { showRationale = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Why are these needed?")
                }
            }
            
            if (hasAllPermissions) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "All permissions granted",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "All permissions granted!",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
    
    // Rationale dialog
    if (showRationale) {
        PermissionRationaleDialog(
            permissions = deniedPermissions,
            permissionManager = permissionManager,
            onDismiss = { showRationale = false },
            onRetry = {
                showRationale = false
                permissionLauncher.launch(deniedPermissions.toTypedArray())
            }
        )
    }
}

@Composable
private fun PermissionGroupCard(
    group: PermissionManager.PermissionGroup,
    permissionManager: PermissionManager,
    modifier: Modifier = Modifier
) {
    val isGranted = permissionManager.isPermissionGroupGranted(group)
    val missingPermissions = permissionManager.getMissingPermissions(group)
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getIconForPermissionGroup(group),
                        contentDescription = null,
                        tint = if (isGranted) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = group.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Icon(
                    imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (isGranted) "Granted" else "Not granted",
                    tint = if (isGranted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = group.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (missingPermissions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Missing: ${missingPermissions.size} permission(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun PermissionRationaleDialog(
    permissions: List<String>,
    permissionManager: PermissionManager,
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Permission Details")
        },
        text = {
            LazyColumn {
                items(permissions) { permission ->
                    Column(
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = permission.split(".").last(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = permissionManager.getPermissionDescription(permission),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text("Grant Permissions")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun getIconForPermissionGroup(group: PermissionManager.PermissionGroup): androidx.compose.ui.graphics.vector.ImageVector {
    return when (group) {
        PermissionManager.PermissionGroup.BLUETOOTH -> Icons.Default.Bluetooth
        PermissionManager.PermissionGroup.LOCATION, 
        PermissionManager.PermissionGroup.BACKGROUND_LOCATION -> Icons.Default.LocationOn
        PermissionManager.PermissionGroup.WIFI -> Icons.Default.Wifi
        PermissionManager.PermissionGroup.CAMERA -> Icons.Default.Camera
        PermissionManager.PermissionGroup.STORAGE -> Icons.Default.Storage
        PermissionManager.PermissionGroup.FOREGROUND_SERVICE -> Icons.Default.Settings
        PermissionManager.PermissionGroup.NOTIFICATIONS -> Icons.Default.Notifications
    }
}