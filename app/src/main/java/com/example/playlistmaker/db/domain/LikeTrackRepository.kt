package com.example.playlistmaker.db.domain

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface LikeTrackRepository {
    suspend fun addLikeTrack(track: Track)
    suspend fun deleteLikeTrack(track: Track)
    suspend fun isTrackLiked(trackId: String): Boolean
    fun likeTrackList(): Flow<List<Track>>
}