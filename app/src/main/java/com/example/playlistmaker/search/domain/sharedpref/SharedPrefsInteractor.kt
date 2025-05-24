package com.example.playlistmaker.search.domain.sharedpref

import com.example.playlistmaker.search.domain.models.Track

interface SharedPrefsInteractor {
    fun readWriteClear(
        use: String,
        track: Track?,
        consumer: SharedPrefsConsumer
    )

    interface SharedPrefsConsumer {
        fun consume(foundSharedPrefs: ArrayList<Track>)
    }
}