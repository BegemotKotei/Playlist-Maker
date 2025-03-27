package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private var darkTheme = false
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        sharedPrefs = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)
        switchTheme(sharedPrefs.getBoolean(DAY_NIGHT, darkTheme))
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
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