package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.data.dto.TrackDto

interface SearchHistoryStorage {
    fun read(sharedPreferences: SharedPreferences): ArrayList<TrackDto>

    fun addTrackToHistory(
        sharedPreferences: SharedPreferences,
        track: Track?
    ): ArrayList<TrackDto>

    fun setHistory(
        sharedPreferences: SharedPreferences,
        trackList: ArrayList<TrackDto>
    )

    fun clearHistory(sharedPreferences: SharedPreferences): ArrayList<TrackDto>
}