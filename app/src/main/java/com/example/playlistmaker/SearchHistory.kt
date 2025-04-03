package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    fun read(): MutableList<Track> {
        val json = sharedPreferences.getString(HISTORY, null)
        return if (json.isNullOrEmpty()) {
            mutableListOf()
        } else {
            Gson().fromJson(json, Array<Track>::class.java).toMutableList()
        }
    }

    fun addTrackToHistory(addTrack: Track): MutableList<Track> {
        var trackList = read()
        // Проверяю, есть ли трек в истории, если есть удаляю его
        trackList.find {
            it.trackId == addTrack.trackId
        }?.let { trackList.remove(it) }

        // Добавляю трек в начало
        trackList.add(0, addTrack)

        // Ограничиваю размер истории
        if (trackList.size > MAX_HISTORY_SIZE) {
            trackList = trackList.subList(0, MAX_HISTORY_SIZE - 1)
        }

        setHistory(trackList)
        return trackList
    }

    fun setHistory(trackList: MutableList<Track>) {
        val json = Gson().toJson(trackList)
        sharedPreferences.edit().putString(HISTORY, json).apply()
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY).apply()
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