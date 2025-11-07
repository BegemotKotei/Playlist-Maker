package com.example.playlistmaker.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {
    private val _theme = MutableLiveData<Boolean>()
    val theme: LiveData<Boolean>
        get() = _theme

    init {
        getTheme()
    }

    fun updateTheme(isDark: Boolean) {
        val themeValue = if (isDark) 1 else 0
        settingsInteractor.updateThemeSetting(themeValue)
        getTheme()
    }

    private fun getTheme() {
        val isDarkTheme = settingsInteractor.getThemeSettings() == 1
        _theme.postValue(isDarkTheme)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun sendToSupport() {
        sharingInteractor.openSupport()
    }

    fun userPolicy() {
        sharingInteractor.openTerms()
    }
}