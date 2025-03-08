package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBarSettings)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(true)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tool_bar_settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
        binding.switchDayOrNight.setOnClickListener {
            if (binding.switchDayOrNight.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}