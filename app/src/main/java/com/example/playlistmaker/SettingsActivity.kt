package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.tool_bar_settings)
        val shareButton = findViewById<Button>(R.id.button_share_app)
        val userAgreementButton = findViewById<Button>(R.id.button_user_agreement)
        val writeSupportButton = findViewById<Button>(R.id.button_write_support)
        val switch = findViewById<Switch>(R.id.switch_day_or_night)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(true)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tool_bar_settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }

        shareButton.setOnClickListener {
            val ShareButton: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.shareHT))
                setType("text/plain")
            }
            startActivity(ShareButton)
        }

        writeSupportButton.setOnClickListener {
            Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("malito:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.emailSubject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.emailText))

            }
        }

        userAgreementButton.setOnClickListener {
            val userAgrOpen: Intent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.userPolicWeb)))
            startActivity(userAgrOpen)
        }

        switch.setOnClickListener {
            if (switch.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}