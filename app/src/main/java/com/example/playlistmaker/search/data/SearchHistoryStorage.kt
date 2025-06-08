package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryStorage {
    fun read(): ArrayList<TrackDto>

    fun addTrackToHistory(
        track: Track?
    ): ArrayList<TrackDto>

    fun setHistory(
        trackList: ArrayList<TrackDto>
    )

    fun clearHistory(): ArrayList<TrackDto>
}