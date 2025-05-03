package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.sharedpref.SharedPrefsRepository

class SharedPrefsRepositoryImpl(private val storage: SearchHistoryStorage) : SharedPrefsRepository {
    override fun saveReadClear(
        sharedPreferences: SharedPreferences,
        use: String,
        track: Track?
    ): ArrayList<Track> {
        val answer = ArrayList<Track>()

        when (use) {
            USE_READ -> answer.addAll(trackDtoToTrack(storage.read(sharedPreferences)))
            USE_WRITE -> answer.addAll(
                trackDtoToTrack(
                    storage.addTrackToHistory(
                        sharedPreferences,
                        track
                    )
                )
            )

            USE_CLEAR -> {
                storage.clearHistory(sharedPreferences)
                answer.addAll(trackDtoToTrack(storage.read(sharedPreferences)))
            }
        }
        return answer
    }

    private fun trackDtoToTrack(list: MutableList<TrackDto>): ArrayList<Track> {
        val answer = list.map {
            Track(
                trackName = it.trackName,
                artistName = it.artistName,
                trackTimeMillis = it.trackTimeMillis,
                artworkUrl100 = it.artworkUrl100,
                trackId = it.trackId,
                collectionName = it.collectionName,
                releaseDate = it.releaseDate,
                primaryGenreName = it.primaryGenreName,
                country = it.country,
                previewUrl = it.previewUrl
            )
        }
        return answer.toCollection(ArrayList())
    }

    companion object {
        private const val USE_CLEAR = "clear"
        private const val USE_READ = "read"
        private const val USE_WRITE = "write"
    }
}