package com.example.playlistmaker.data.sharedstorage

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.data.SearchHistoryStorage
import com.example.playlistmaker.data.dto.TrackDto
import com.google.gson.Gson
import androidx.core.content.edit

class SearchHistoryStorageImpl : SearchHistoryStorage {

    override fun read(sharedPreferences: SharedPreferences): MutableList<TrackDto> {
        val json = sharedPreferences.getString(HISTORY, null)
        return if (json.isNullOrEmpty()) {
            mutableListOf()
        } else {
            Gson().fromJson(json, Array<TrackDto>::class.java).toMutableList()
        }
    }

    override fun addTrackToHistory(
        sharedPreferences: SharedPreferences,
        track: Track?
    ): MutableList<TrackDto> {
        var trackList = read(sharedPreferences)
        val addTrackDto = TrackDto(
            track?.trackName.toString(),
            track?.artistName.toString(),
            track?.trackTimeMillis.toString(),
            track?.artworkUrl100.toString(),
            track?.trackId.toString(),
            track?.collectionName.toString(),
            track?.releaseDate.toString(),
            track?.primaryGenreName.toString(),
            track?.country.toString(),
            track?.previewUrl.toString()
        )
        // Проверяю, есть ли трек в истории, если есть удаляю его
        trackList.find {
            it.trackId == track?.trackId
        }?.let { trackList.remove(it) }

        // Добавляю трек в начало
        trackList.add(0, addTrackDto)

        // Ограничиваю размер истории
        if (trackList.size > MAX_HISTORY_SIZE) {
            trackList = trackList.subList(0, MAX_HISTORY_SIZE - 1)
        }

        setHistory(
            sharedPreferences = sharedPreferences,
            trackList = trackList
        )
        return trackList
    }

    override fun setHistory(
        sharedPreferences: SharedPreferences,
        trackList: MutableList<TrackDto>
    ) {
        val json = Gson().toJson(trackList)
        sharedPreferences.edit() { putString(HISTORY, json) }
    }

    override fun clearHistory(sharedPreferences: SharedPreferences) {
        sharedPreferences.edit() { remove(HISTORY) }
    }

    companion object {
        private const val HISTORY = "history"
        private const val HISTORY_MAIN = "historyMain"
        private const val MAX_HISTORY_SIZE = 10

        fun getHistoryMain(): String {
            return HISTORY_MAIN
        }
    }
}