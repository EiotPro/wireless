package com.iotlogic.blynk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iotlogic.blynk.domain.model.Configuration
import com.iotlogic.blynk.domain.model.ConfigCategory
import com.iotlogic.blynk.domain.model.ValidationResult
import com.iotlogic.blynk.domain.repository.ConfigurationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val configurationRepository: ConfigurationRepository
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(ConfigurationUiState())
    val uiState: StateFlow<ConfigurationUiState> = _uiState.asStateFlow()
    
    // Selected device
    private val _selectedDeviceId = MutableStateFlow<String?>(null)
    val selectedDeviceId: StateFlow<String?> = _selectedDeviceId.asStateFlow()
    
    // Device configurations
    val deviceConfigurations: StateFlow<List<Configuration>> = _selectedDeviceId
        .filterNotNull()
        .flatMapLatest { deviceId ->
            configurationRepository.getConfigurationsByDevice(deviceId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Selected category filter
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()
    
    // Filtered configurations by category
    val filteredConfigurations: StateFlow<List<Configuration>> = combine(
        deviceConfigurations,
        _selectedCategory
    ) { configurations, category ->
        if (category == null) {
            configurations
        } else {
            configurations.filter { it.category == category }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    // Available categories
    val availableCategories: StateFlow<List<String>> = deviceConfigurations
        .map { configurations ->
            configurations.map { it.category }.distinct().sorted()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Pending changes
    private val _pendingChanges = MutableStateFlow<Map<String, String>>(emptyMap())
    val pendingChanges: StateFlow<Map<String, String>> = _pendingChanges.asStateFlow()
    
    /**
     * Set selected device for configuration management
     */
    fun selectDevice(deviceId: String?) {
        _selectedDeviceId.value = deviceId
        _selectedCategory.value = null
        _pendingChanges.value = emptyMap()
        
        if (deviceId != null) {
            loadCategoriesForDevice(deviceId)
        }
    }
    
    /**
     * Set category filter
     */
    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }
    
    /**
     * Load categories for device
     */
    private fun loadCategoriesForDevice(deviceId: String) {
        viewModelScope.launch {
            try {
                val categories = configurationRepository.getCategoriesForDevice(deviceId)
                _uiState.value = _uiState.value.copy(availableCategories = categories)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    /**
     * Get configuration by key
     */
    fun getConfiguration(deviceId: String, configKey: String): Flow<Configuration?> {
        return configurationRepository.getConfiguration(deviceId, configKey)
    }
    
    /**
     * Get configurations by category
     */
    fun getConfigurationsByCategory(deviceId: String, category: String): Flow<List<Configuration>> {
        return configurationRepository.getConfigurationsByDeviceAndCategory(deviceId, category)
    }
    
    /**
     * Update configuration value with validation
     */
    fun updateConfigurationValue(configuration: Configuration, newValue: String) {
        val validationResult = configuration.validateValue(newValue)
        
        when (validationResult) {
            is ValidationResult.Valid -> {
                val currentChanges = _pendingChanges.value.toMutableMap()
                currentChanges[configuration.configKey] = newValue
                _pendingChanges.value = currentChanges
                
                _uiState.value = _uiState.value.copy(
                    validationErrors = _uiState.value.validationErrors - configuration.configKey
                )
            }
            is ValidationResult.Invalid -> {
                val currentErrors = _uiState.value.validationErrors.toMutableMap()
                currentErrors[configuration.configKey] = validationResult.message
                _uiState.value = _uiState.value.copy(validationErrors = currentErrors)
            }
        }
    }
    
    /**
     * Save pending configuration changes
     */
    fun savePendingChanges() {
        val deviceId = _selectedDeviceId.value
        val changes = _pendingChanges.value
        
        if (deviceId != null && changes.isNotEmpty()) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isSaving = true)
                
                try {
                    val result = configurationRepository.updateDeviceConfigurations(deviceId, changes)
                    
                    if (result.isSuccess) {
                        _pendingChanges.value = emptyMap()
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            message = "Configuration saved successfully"
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            error = result.exceptionOrNull()?.message
                        )
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = e.message
                    )
                }
            }
        }
    }
    
    /**
     * Discard pending changes
     */
    fun discardPendingChanges() {
        _pendingChanges.value = emptyMap()
        _uiState.value = _uiState.value.copy(
            validationErrors = emptyMap(),
            message = "Changes discarded"
        )
    }
    
    /**
     * Save single configuration
     */
    fun saveConfiguration(configuration: Configuration) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            
            val result = configurationRepository.saveDeviceConfig(configuration)
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    message = "Configuration saved successfully"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
    
    /**
     * Reset device configurations to defaults
     */
    fun resetToDefaults(deviceId: String, defaultConfigurations: List<Configuration>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            
            val result = configurationRepository.resetDeviceConfigurations(deviceId, defaultConfigurations)
            
            if (result.isSuccess) {
                _pendingChanges.value = emptyMap()
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    message = "Configurations reset to defaults"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
    
    /**
     * Delete configuration
     */
    fun deleteConfiguration(deviceId: String, configKey: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val result = configurationRepository.deleteConfiguration(deviceId, configKey)
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Configuration deleted"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
    
    /**
     * Sync configurations with backend
     */
    fun syncConfigurations(token: String) {
        val deviceId = _selectedDeviceId.value
        
        if (deviceId != null) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isSyncing = true)
                
                val result = configurationRepository.syncConfigurations(token, deviceId)
                
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isSyncing = false,
                        message = "Configurations synced successfully"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSyncing = false,
                        error = result.exceptionOrNull()?.message
                    )
                }
            }
        }
    }
    
    /**
     * Get editable configurations
     */
    suspend fun getEditableConfigurations(deviceId: String): List<Configuration> {
        return configurationRepository.getEditableConfigurations(deviceId)
    }
    
    /**
     * Get read-only configurations
     */
    suspend fun getReadOnlyConfigurations(deviceId: String): List<Configuration> {
        return configurationRepository.getReadOnlyConfigurations(deviceId)
    }
    
    /**
     * Get configuration statistics
     */
    suspend fun getConfigurationStatistics(deviceId: String): ConfigurationStatistics {
        val totalConfigs = configurationRepository.getConfigurationCountForDevice(deviceId)
        val editableConfigs = getEditableConfigurations(deviceId).size
        val readOnlyConfigs = getReadOnlyConfigurations(deviceId).size
        val categories = configurationRepository.getCategoriesForDevice(deviceId)
        
        return ConfigurationStatistics(
            totalConfigurations = totalConfigs,
            editableConfigurations = editableConfigs,
            readOnlyConfigurations = readOnlyConfigs,
            categoriesCount = categories.size,
            categories = categories
        )
    }
    
    /**
     * Group configurations by category
     */
    fun getConfigurationsByCategory(): Map<ConfigCategory, List<Configuration>> {
        return deviceConfigurations.value.groupBy { it.getCategoryType() }
    }
    
    /**
     * Check if there are pending changes
     */
    fun hasPendingChanges(): Boolean {
        return _pendingChanges.value.isNotEmpty()
    }
    
    /**
     * Check if configuration has pending changes
     */
    fun hasPendingChange(configKey: String): Boolean {
        return _pendingChanges.value.containsKey(configKey)
    }
    
    /**
     * Get pending value for configuration
     */
    fun getPendingValue(configKey: String): String? {
        return _pendingChanges.value[configKey]
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * Clear message
     */
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
    
    /**
     * Clear validation error for specific key
     */
    fun clearValidationError(configKey: String) {
        val currentErrors = _uiState.value.validationErrors.toMutableMap()
        currentErrors.remove(configKey)
        _uiState.value = _uiState.value.copy(validationErrors = currentErrors)
    }
}

/**
 * UI state for configuration screen
 */
data class ConfigurationUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSyncing: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val availableCategories: List<String> = emptyList(),
    val validationErrors: Map<String, String> = emptyMap()
)

/**
 * Configuration statistics
 */
data class ConfigurationStatistics(
    val totalConfigurations: Int,
    val editableConfigurations: Int,
    val readOnlyConfigurations: Int,
    val categoriesCount: Int,
    val categories: List<String>
)