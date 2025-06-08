package com.example.playlistmaker.search.domain.sharedpref

import com.example.playlistmaker.search.domain.models.Track

interface SharedPrefsRepository {
    fun saveReadClear(
        use: String,
        track: Track?
    ): ArrayList<Track>
}