package com.example.playlistmaker.db.data

import com.example.playlistmaker.db.domain.LikeTrackRepository
import com.example.playlistmaker.db.mappers.TrackDbMapper
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LikeTrackRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbMapper: TrackDbMapper
) : LikeTrackRepository {
    override suspend fun addLikeTrack(track: Track) {
        appDatabase.trackDao().insertTrack(trackDbMapper.mapToTrackEntity(track))
    }

    override suspend fun deleteLikeTrack(track: Track) {
        appDatabase.trackDao().deleteTrackEntity(trackDbMapper.mapToTrackEntity(track))
    }

    override fun likeTrackList(): Flow<List<Track>> = flow {
        emit(trackDbMapper.mapToListTrack(appDatabase.trackDao().getLikedTracks()))
    }
}