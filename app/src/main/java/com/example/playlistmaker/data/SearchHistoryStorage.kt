package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.data.dto.TrackDto

interface SearchHistoryStorage {
    fun read(sharedPreferences: SharedPreferences): MutableList<TrackDto>

    fun addTrackToHistory(
        sharedPreferences: SharedPreferences,
        track: Track?
    ): MutableList<TrackDto>

    fun setHistory(
        sharedPreferences: SharedPreferences,
        trackList: MutableList<TrackDto>
    )

    fun clearHistory(sharedPreferences: SharedPreferences)
}