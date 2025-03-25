package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    fun read(): ArrayList<Track> {
        val json = sharedPreferences.getString(HISTORY, null)
        return if (json.isNullOrEmpty()) {
            ArrayList<Track>()
        } else {
            Gson().fromJson(json, Array<Track>::class.java).toCollection(ArrayList())
        }
    }

    fun setHistory(trackList: ArrayList<Track>) {
        val json = Gson().toJson(trackList)
        sharedPreferences.edit().putString(HISTORY, json).apply()
    }

    fun addTrackToHistory(trackList: ArrayList<Track>, addTrack: Track): ArrayList<Track> {
        // Нахожу индекс трека, если он уже есть в списке, если нет – возвращаю -1.
        val existingTrackIndex = trackList.indexOfFirst { it.trackId == addTrack.trackId }

        // Проверяю, есть ли трек в истории
        if (existingTrackIndex != -1) {
            // Если есть – удаляю его
            trackList.removeAt(existingTrackIndex)
        }

        // Добавляю трек в начало
        trackList.add(0, addTrack)

        // Ограничиваю размер истории
        if (trackList.size > MAX_HISTORY_SIZE) {
            trackList.removeAt(trackList.size - 1)
        }

        val json = Gson().toJson(trackList)
        sharedPreferences.edit().putString(HISTORY, json).apply()
        return trackList
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