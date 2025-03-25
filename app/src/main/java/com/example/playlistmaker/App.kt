package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)
        switchTheme(sharedPrefs.getBoolean(DAY_NIGHT, darkTheme))
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        val sharedPrefs = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)
        sharedPrefs.edit().putBoolean(DAY_NIGHT, darkTheme).apply()
    }

    companion object {
        private const val THEME_PREFERENCES = "theme"
        private const val DAY_NIGHT = "dayornight"

        fun getThemePreferences(): String {
            return THEME_PREFERENCES
        }

        fun getDayNight(): String {
            return DAY_NIGHT
        }
    }
}