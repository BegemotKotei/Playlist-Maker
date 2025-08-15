package com.example.playlistmaker.db.domain.imlp

import com.example.playlistmaker.db.domain.LikeTrackInteractor
import com.example.playlistmaker.db.domain.LikeTrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LikeTrackInteractorImpl(private val repository: LikeTrackRepository) :
    LikeTrackInteractor {
    override suspend fun addLikeTrack(track: Track) {
        repository.addLikeTrack(track)
    }

    override suspend fun deleteLikeTrack(track: Track) {
        repository.deleteLikeTrack(track)
    }

    override suspend fun isTrackLiked(trackId: String): Boolean {
        return repository.isTrackLiked(trackId)
    }

    override fun likeTrackList(): Flow<List<Track>> {
        return repository.likeTrackList().map { tracks ->
            tracks.reversed()
        }
    }
}