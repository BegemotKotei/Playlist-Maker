package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_serarch)

        val backButton = findViewById<ImageView>(R.id.toolBar)

        backButton.setOnClickListener {
            finish()
        }
    }
}