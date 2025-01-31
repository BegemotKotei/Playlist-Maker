package com.example.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MediaLibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_library)

        val toolbar = findViewById<Toolbar>(R.id.tool_bar_media_library)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(true)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tool_bar_media_library)) { v, insets ->
            val systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBar.left, systemBar.top, systemBar.right, systemBar.bottom)
            insets
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}