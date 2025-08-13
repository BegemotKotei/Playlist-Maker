package com.example.playlistmaker.search.domain.sharedpref

import com.example.playlistmaker.search.domain.models.Track

interface SharedPrefsRepository {
    suspend fun saveReadClear(
        use: String,
        track: Track?
    ): ArrayList<Track>
}