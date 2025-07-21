package com.allubie.nana.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.allubie.nana.data.repository.SettingsRepository
import com.allubie.nana.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {
    
    private val _currentTheme = MutableStateFlow(ThemeMode.LIGHT)
    val currentTheme: StateFlow<ThemeMode> = _currentTheme.asStateFlow()
    
    private val _notificationsEnabled = MutableStateFlow(false)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()
    
    private val _autoBackupEnabled = MutableStateFlow(false)
    val autoBackupEnabled: StateFlow<Boolean> = _autoBackupEnabled.asStateFlow()
    
    private val _currencyFormat = MutableStateFlow("USD ($)")
    val currencyFormat: StateFlow<String> = _currencyFormat.asStateFlow()
    
    private val _is24HourFormat = MutableStateFlow(true)
    val is24HourFormat: StateFlow<Boolean> = _is24HourFormat.asStateFlow()
    
    private val _monthlyBudget = MutableStateFlow(1000.0)
    val monthlyBudget: StateFlow<Double> = _monthlyBudget.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.themeMode.collect { themeName ->
                _currentTheme.value = try {
                    ThemeMode.valueOf(themeName)
                } catch (e: IllegalArgumentException) {
                    ThemeMode.LIGHT
                }
            }
        }
        
        viewModelScope.launch {
            settingsRepository.notificationsEnabled.collect { enabled ->
                _notificationsEnabled.value = enabled
            }
        }
        
        viewModelScope.launch {
            settingsRepository.autoBackupEnabled.collect { enabled ->
                _autoBackupEnabled.value = enabled
            }
        }
        
        viewModelScope.launch {
            settingsRepository.currencyFormat.collect { currency ->
                _currencyFormat.value = currency
            }
        }
        
        viewModelScope.launch {
            settingsRepository.is24HourFormat.collect { is24H ->
                _is24HourFormat.value = is24H
            }
        }
        
        viewModelScope.launch {
            settingsRepository.monthlyBudget.collect { budget ->
                _monthlyBudget.value = budget
            }
        }
    }
    
    fun updateTheme(theme: ThemeMode) {
        viewModelScope.launch {
            _currentTheme.value = theme
            settingsRepository.setTheme(theme.name)
        }
    }
    
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _notificationsEnabled.value = enabled
            settingsRepository.setNotificationsEnabled(enabled)
        }
    }
    
    fun setAutoBackupEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _autoBackupEnabled.value = enabled
            settingsRepository.setAutoBackupEnabled(enabled)
        }
    }
    
    fun setCurrencyFormat(currency: String) {
        viewModelScope.launch {
            _currencyFormat.value = currency
            settingsRepository.setCurrencyFormat(currency)
        }
    }
    
    fun setTimeFormat24H(is24H: Boolean) {
        viewModelScope.launch {
            _is24HourFormat.value = is24H
            settingsRepository.setTimeFormat24H(is24H)
        }
    }
    
    fun setMonthlyBudget(budget: Double) {
        viewModelScope.launch {
            _monthlyBudget.value = budget
            settingsRepository.setMonthlyBudget(budget)
        }
    }
}

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(SettingsRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
