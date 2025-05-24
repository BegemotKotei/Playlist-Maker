package com.example.playlistmaker.settings.presentation

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingActivity : AppCompatActivity() {
    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModel.Factory(this)
    }
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            toolBarSettings.setOnClickListener {
                finish()
            }
            buttonShareApp.setOnClickListener {
                viewModel.shareApp()
            }
            buttonWriteSupport.setOnClickListener {
                viewModel.sendToSupport()
            }
            buttonUserAgreement.setOnClickListener {
                viewModel.userPolicy()
            }
            switchDayOrNight.setOnCheckedChangeListener { _, checked ->
                val theme = if (checked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
                viewModel.updateTheme(theme)
            }
        }

        viewModel.theme.observe(this) {
            val checked = if (it == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
                getSystemNightMode()
            } else {
                it == AppCompatDelegate.MODE_NIGHT_YES
            }

            binding.switchDayOrNight.isChecked = checked

            switchTheme(it)
        }
        setupInsets()
    }

    private fun getSystemNightMode() = resources
        .configuration
        .uiMode
        .and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    private fun switchTheme(theme: Int) {
        AppCompatDelegate.setDefaultNightMode(
            theme
        )
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) {v, insets ->
            val systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBar.left,
                systemBar.top,
                systemBar.right,
                systemBar.bottom
            )
            insets
        }
    }
}