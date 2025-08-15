package com.example.playlistmaker.db.data

import com.example.playlistmaker.db.dao.TrackDao
import com.example.playlistmaker.db.domain.LikeTrackRepository
import com.example.playlistmaker.db.mappers.TrackDbMapper
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LikeTrackRepositoryImpl(
    private val trackDao: TrackDao,
    private val trackDbMapper: TrackDbMapper
) : LikeTrackRepository {
    override suspend fun addLikeTrack(track: Track) {
        trackDao.insertTrack(trackDbMapper.mapToTrackEntity(track))
    }

    override suspend fun deleteLikeTrack(track: Track) {
        trackDao.deleteTrackEntity(trackDbMapper.mapToTrackEntity(track))
    }

    override suspend fun isTrackLiked(trackId: String): Boolean {
        return trackDao.isTrackLiked(trackId)
    }

    override fun likeTrackList(): Flow<List<Track>> = flow {
        emit(trackDbMapper.mapToListTrack(trackDao.getLikedTracks()))
    }
}