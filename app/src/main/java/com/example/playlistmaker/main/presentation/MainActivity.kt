package com.example.playlistmaker.main.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.media.presentation.activity.MediaLibraryActivity
import com.example.playlistmaker.search.presentation.activity.SearchActivity
import com.example.playlistmaker.settings.presentation.SettingActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            buttonPanelSearch.setOnClickListener {
                val displayIntent = Intent(
                    this@MainActivity,
                    SearchActivity::class.java
                )
                startActivity(displayIntent)
            }

            buttonPanelMediaLibrary.setOnClickListener {
                val displayIntent = Intent(
                    this@MainActivity,
                    MediaLibraryActivity::class.java
                )
                startActivity(displayIntent)
            }

            buttonPanelSettings.setOnClickListener {
                val displayIntent = Intent(
                    this@MainActivity,
                    SettingActivity::class.java
                )
                startActivity(displayIntent)
            }
        }
        viewModel.theme.observe(this) {
            AppCompatDelegate.setDefaultNightMode(it)
        }
        viewModel.requestTheme()

        setupInsets()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
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