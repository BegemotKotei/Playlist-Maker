package com.example.playlistmaker.domain.sharedpref

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track

interface SharedPrefsInteractor {
    fun readWriteClear(
        sharedPreferences: SharedPreferences,
        use: String,
        track: Track?,
        consumer: SharedPrefsConsumer
    )

    interface SharedPrefsConsumer {
        fun consume(foundSharedPrefs: ArrayList<Track>)
    }
}