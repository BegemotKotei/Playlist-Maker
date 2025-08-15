package com.example.playlistmaker.search.data

import com.example.playlistmaker.db.dao.TrackDao
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.sharedpref.SharedPrefsRepository

class SharedPrefsRepositoryImpl(
    private val storage: SearchHistoryStorage,
    private val trackDao: TrackDao
) : SharedPrefsRepository {

    override suspend fun saveReadClear(
        use: String,
        track: Track?
    ): ArrayList<Track> {
        return when (use) {
            USE_READ -> trackDtoToTrack(storage.read())
            USE_WRITE -> trackDtoToTrack(storage.addTrackToHistory(track))
            USE_CLEAR -> {
                storage.clearHistory()
                ArrayList()
            }

            else -> throw IllegalArgumentException("Unknown operation: $use")
        }
    }

    private suspend fun trackDtoToTrack(list: List<TrackDto>): ArrayList<Track> {
        val tracksIdList = trackDao.getTracksIdList()
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
                previewUrl = dto.previewUrl,
                isLiked = isFavoriteBoolean(dto.trackId, tracksIdList)
            )
        }.toCollection(ArrayList())
    }

    private fun isFavoriteBoolean(trackId: String, tracksId: List<String>): Boolean {
        var answer = false
        tracksId.forEach { likedTrack ->
            if (likedTrack == trackId) {
                answer = true
            }
        }
        return answer
    }

    private companion object {
        const val USE_CLEAR = "clear"
        const val USE_READ = "read"
        const val USE_WRITE = "write"
    }
}