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
        return when (use) {
            USE_READ -> trackDtoToTrack(storage.read(sharedPreferences))
            USE_WRITE -> trackDtoToTrack(storage.addTrackToHistory(sharedPreferences, track))
            USE_CLEAR -> {
                storage.clearHistory(sharedPreferences)
                ArrayList()
            }
            else -> throw IllegalArgumentException("Unknown operation: $use")
        }
    }

    private fun trackDtoToTrack(list: List<TrackDto>): ArrayList<Track> {
        return list.map { dto ->
            Track(
                trackName = dto.trackName,
                artistName = dto.artistName,
                trackTimeMillis = dto.trackTimeMillis,
                artworkUrl100 = dto.artworkUrl100,
                trackId = dto.trackId,
                collectionName = dto.collectionName,
                releaseDate = dto.releaseDate,
                primaryGenreName = dto.primaryGenreName,
                country = dto.country,
                previewUrl = dto.previewUrl
            )
        }.toCollection(ArrayList())
    }

    private companion object {
        const val USE_CLEAR = "clear"
        const val USE_READ = "read"
        const val USE_WRITE = "write"
    }
}