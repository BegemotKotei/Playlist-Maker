package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MediaLibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_media_library)

        val backButton = findViewById<ImageView>(R.id.toolBar)

        backButton.setOnClickListener {
            finish()
        }
    }
}