package com.iotlogic.blynk.ui.screens.devices

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iotlogic.blynk.domain.model.Configuration
import com.iotlogic.blynk.ui.components.ErrorMessage
import com.iotlogic.blynk.ui.components.LoadingIndicator
import com.iotlogic.blynk.ui.theme.IoTLogicTheme
import com.iotlogic.blynk.ui.viewmodel.ConfigurationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceConfigurationScreen(
    deviceId: String,
    onNavigateBack: () -> Unit,
    viewModel: ConfigurationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val configurations by viewModel.filteredConfigurations.collectAsStateWithLifecycle()
    val categories by viewModel.availableCategories.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val pendingChanges by viewModel.pendingChanges.collectAsStateWithLifecycle()
    
    LaunchedEffect(deviceId) {
        viewModel.selectDevice(deviceId)
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    text = "Device Configuration",
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
                if (pendingChanges.isNotEmpty()) {
                    TextButton(
                        onClick = { viewModel.discardPendingChanges() }
                    ) {
                        Text("Discard")
                    }
                    
                    Button(
                        onClick = { viewModel.savePendingChanges() },
                        enabled = !uiState.isSaving
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Save")
                        }
                    }
                }
                
                IconButton(
                    onClick = { viewModel.syncConfigurations("") } // Token would be provided
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Sync configurations"
                    )
                }
            }
        )
        
        // Category Filter
        if (categories.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { viewModel.selectCategory(null) },
                        label = { Text("All") }
                    )
                }
                
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { 
                            viewModel.selectCategory(
                                if (selectedCategory == category) null else category
                            )
                        },
                        label = { Text(category.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
        }
        
        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading && configurations.isEmpty() -> {
                    LoadingIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        message = "Loading configurations..."
                    )
                }
                
                uiState.error != null -> {
                    ErrorMessage(
                        message = uiState.error,
                        onRetry = { viewModel.selectDevice(deviceId) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                configurations.isEmpty() -> {
                    EmptyConfigurationsList(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Group configurations by category
                        val groupedConfigs = configurations.groupBy { it.category }
                        
                        groupedConfigs.forEach { (category, configList) ->
                            item {
                                Text(
                                    text = category.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            
                            items(
                                items = configList,
                                key = { config -> config.configKey }
                            ) { config ->
                                ConfigurationCard(
                                    configuration = config,
                                    pendingValue = viewModel.getPendingValue(config.configKey),
                                    hasError = uiState.validationErrors.containsKey(config.configKey),
                                    errorMessage = uiState.validationErrors[config.configKey],
                                    onValueChange = { newValue ->
                                        viewModel.updateConfigurationValue(config, newValue)
                                    },
                                    onClearError = {
                                        viewModel.clearValidationError(config.configKey)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Show messages
    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            // Show snackbar or toast
            viewModel.clearMessage()
        }
    }
}

@Composable
private fun ConfigurationCard(
    configuration: Configuration,
    pendingValue: String?,
    hasError: Boolean,
    errorMessage: String?,
    onValueChange: (String) -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = configuration.configKey.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    if (configuration.description.isNotEmpty()) {
                        Text(
                            text = configuration.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                
                if (configuration.isReadOnly) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Read Only",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Configuration input based on data type
            when (configuration.dataType) {
                "BOOLEAN" -> {
                    BooleanConfigInput(
                        value = pendingValue ?: configuration.configValue,
                        enabled = !configuration.isReadOnly,
                        onValueChange = onValueChange
                    )
                }
                
                "INTEGER" -> {
                    NumberConfigInput(
                        value = pendingValue ?: configuration.configValue,
                        enabled = !configuration.isReadOnly,
                        isInteger = true,
                        unit = configuration.unit,
                        hasError = hasError,
                        errorMessage = errorMessage,
                        onValueChange = onValueChange,
                        onClearError = onClearError
                    )
                }
                
                "DOUBLE" -> {
                    NumberConfigInput(
                        value = pendingValue ?: configuration.configValue,
                        enabled = !configuration.isReadOnly,
                        isInteger = false,
                        unit = configuration.unit,
                        hasError = hasError,
                        errorMessage = errorMessage,
                        onValueChange = onValueChange,
                        onClearError = onClearError
                    )
                }
                
                "STRING" -> {
                    StringConfigInput(
                        value = pendingValue ?: configuration.configValue,
                        enabled = !configuration.isReadOnly,
                        hasError = hasError,
                        errorMessage = errorMessage,
                        onValueChange = onValueChange,
                        onClearError = onClearError
                    )
                }
                
                else -> {
                    Text(
                        text = pendingValue ?: configuration.configValue,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // Show pending changes indicator
            if (pendingValue != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Pending changes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun BooleanConfigInput(
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val booleanValue = value.toBoolean()
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (booleanValue) "Enabled" else "Disabled",
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(
            checked = booleanValue,
            onCheckedChange = { onValueChange(it.toString()) },
            enabled = enabled
        )
    }
}

@Composable
private fun NumberConfigInput(
    value: String,
    enabled: Boolean,
    isInteger: Boolean,
    unit: String?,
    hasError: Boolean,
    errorMessage: String?,
    onValueChange: (String) -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
                if (hasError) onClearError()
            },
            enabled = enabled,
            isError = hasError,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isInteger) KeyboardType.Number else KeyboardType.Decimal
            ),
            suffix = unit?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        
        if (hasError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun StringConfigInput(
    value: String,
    enabled: Boolean,
    hasError: Boolean,
    errorMessage: String?,
    onValueChange: (String) -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
                if (hasError) onClearError()
            },
            enabled = enabled,
            isError = hasError,
            modifier = Modifier.fillMaxWidth()
        )
        
        if (hasError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun EmptyConfigurationsList(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No configurations available",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "This device has no configurable parameters",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeviceConfigurationScreenPreview() {
    IoTLogicTheme {
        // Preview would require mock data
    }
}