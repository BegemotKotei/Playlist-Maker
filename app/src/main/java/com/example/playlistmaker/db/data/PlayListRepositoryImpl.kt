package com.example.playlistmaker.db.data

import com.example.playlistmaker.db.dao.PlayListDao
import com.example.playlistmaker.db.domain.PlayListRepository
import com.example.playlistmaker.db.entity.PlayListEntity
import com.example.playlistmaker.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.db.mappers.PlayListDbMapper
import com.example.playlistmaker.playlist_create.domain.models.PlayList
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlayListRepositoryImpl(
    private val playListDao: PlayListDao,
    private val playListDbMapper: PlayListDbMapper,
) : PlayListRepository {
    override suspend fun newPlayList(playList: PlayList) =
        playListDao.createPlaylist(playListDbMapper.mapToPlayListEntity(playList))

    override fun listPlayList(): Flow<List<PlayList>> = flow {
        emit(mapIntoPlayList(playListDao.getPlaylists()))
    }

    override suspend fun getCountTracks(id: Long): Int {
        return playListDao.getTracksCount(id)
    }

    override suspend fun updatePlayList(track: Track, id: Long): Boolean {
        val trackAlreadyExists =
            playListDao.findTrack(trackId = track.trackId, playlistId = id).isNotEmpty()
        if (!trackAlreadyExists) {
            playListDao.addTracksToPlaylist(
                PlaylistTrackEntity(
                    trackId = track.trackId,
                    playlistId = id,
                    trackName = track.trackName,
                    artistName = track.artistName,
                    trackTimeMillis = track.trackTimeMillis,
                    artworkUrl100 = track.artworkUrl100,
                    collectionName = track.collectionName,
                    releaseDate = track.releaseDate,
                    primaryGenreName = track.primaryGenreName,
                    country = track.country,
                    previewUrl = track.previewUrl
                )
            )
        }
        return !trackAlreadyExists
    }

    private fun mapIntoPlayList(playList: List<PlayListEntity>): List<PlayList> {
        return playList.map { playListEntity -> playListDbMapper.mapToPlayList(playListEntity) }
    }
}