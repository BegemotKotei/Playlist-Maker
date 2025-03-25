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

    fun write(trackList: ArrayList<Track>, addTrack: Track? = null): ArrayList<Track> {
        if (addTrack == null) {
            val json = Gson().toJson(trackList)
            sharedPreferences.edit().putString(HISTORY, json).apply()
        } else {
            val trackId = addTrack.trackId
            val iterator = trackList.iterator()
            for (items in iterator) {
                if (items.trackId == trackId) {
                    iterator.remove()
                }
            }

            trackList.add(addTrack)
            if (trackList.size > MAX_HISTORY_SIZE) {
                trackList.removeAt(0)
            }
            val json = Gson().toJson(trackList)
            sharedPreferences.edit().putString(HISTORY, json).apply()
        }
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