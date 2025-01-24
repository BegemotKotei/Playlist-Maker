package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val searchButton =  findViewById<Button>(R.id.buttonPanelSearch)
        val mediaLibraryButton =  findViewById<Button>(R.id.buttonPanelMediaLibrary)
        val settingsButton =  findViewById<Button>(R.id.buttonPanelSettings)

        searchButton.setOnClickListener {
            val displayIntent = Intent(this,SearchActivity::class.java)
            startActivity(displayIntent)
        }

        mediaLibraryButton.setOnClickListener {
            val displayIntent = Intent(this, MediaLibraryActivity::class.java)
            startActivity(displayIntent)
        }

        settingsButton.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }


    }
}