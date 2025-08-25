package com.example.playlistmaker.settings.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.domain.SettingsRepository

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    private val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    override fun getThemeSettings() =
        sharedPrefs.getInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

    override fun updateThemeSetting(theme: Int) =
        sharedPrefs.edit { putInt(THEME_KEY, theme) }

    override fun getAppShareLink() = context.getString(R.string.share_ht)

    override fun getUserPolicy() = context.getString(R.string.user_policy_web)

    override fun getSupportEmail() = context.getString(R.string.email)

    override fun getSupportEmailTop() = context.getString(R.string.email_subject)

    override fun getSupportEmailText() = context.getString(R.string.email_text)

    private companion object {
        const val PREF_NAME = "PREF_SETTINGS"
        const val THEME_KEY = "THEME_KEY"
    }
}