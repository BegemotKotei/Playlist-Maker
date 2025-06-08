package com.example.playlistmaker.search.data.sharedstorage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.data.SearchHistoryStorage
import com.example.playlistmaker.search.data.dto.TrackDto
import com.google.gson.Gson

class SearchHistoryStorageImpl(private val context: Context) : SearchHistoryStorage {

    override fun read(): ArrayList<TrackDto> {
        val sharedPreferences = context.getSharedPreferences(HISTORY_MAIN, MODE_PRIVATE)
        val json = sharedPreferences.getString(HISTORY, null) ?: return ArrayList()
        return Gson().fromJson(json, Array<TrackDto>::class.java).toCollection(ArrayList())
    }

    override fun addTrackToHistory(
        track: Track?
    ): ArrayList<TrackDto> {
        var trackList = read()
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
            trackList = ArrayList(trackList.subList(0, MAX_HISTORY_SIZE))
        }

        setHistory(
            trackList = trackList
        )
        return trackList
    }

    override fun setHistory(
        trackList: ArrayList<TrackDto>
    ) {
        val sharedPreferences = context.getSharedPreferences(HISTORY_MAIN, MODE_PRIVATE)
        val json = Gson().toJson(trackList)
        sharedPreferences.edit { putString(HISTORY, json) }
    }

    override fun clearHistory(): ArrayList<TrackDto> {
        val sharedPreferences = context.getSharedPreferences(HISTORY_MAIN, MODE_PRIVATE)
        sharedPreferences.edit { remove(HISTORY) }
        return ArrayList()
    }

    private companion object {
        const val HISTORY = "history"
        const val HISTORY_MAIN = "historyMain"
        const val MAX_HISTORY_SIZE = 10

    }
}