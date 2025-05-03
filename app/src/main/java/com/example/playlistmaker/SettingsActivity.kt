package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolBar()

        val sharedPrefs = getSharedPreferences(App.getThemePreferences(), MODE_PRIVATE)
        val themeDayOrNight = false

        binding.switchDayOrNight.isChecked =
            sharedPrefs.getBoolean(App.getDayNight(), themeDayOrNight)

        binding.toolBarSettings.setNavigationOnClickListener {
            finish()
        }

        binding.buttonShareApp.setOnClickListener {
            val shareButton: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.shareHT))
                type = "text/plain"
            }
            startActivity(shareButton)
        }

        binding.buttonWriteSupport.setOnClickListener {
            Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.emailSubject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.emailText))
                startActivity(this)
            }
        }

        binding.buttonUserAgreement.setOnClickListener {
            val userAgrOpen =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.userPolicWeb)))
            startActivity(userAgrOpen)
        }

        binding.switchDayOrNight.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }

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

    private fun setupToolBar() {
        setSupportActionBar(binding.toolBarSettings)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
    }
}