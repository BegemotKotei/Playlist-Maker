package com.example.playlistmaker.db.domain.imlp

import com.example.playlistmaker.core.resourceManager.IResourceManager
import com.example.playlistmaker.db.domain.PlayListInteractor
import com.example.playlistmaker.db.domain.PlayListRepository
import com.example.playlistmaker.playlist_create.domain.models.PlayList
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlayListInteractorImpl(
    private val repository: PlayListRepository,
    private val resourceManager: IResourceManager
) :
    PlayListInteractor {
    override suspend fun newPlayList(playList: PlayList) =
        repository.newPlayList(playList)


    override fun listPlayList(): Flow<List<PlayList>> {
        return repository.listPlayList().map { playList ->
            playList.reversed()
        }
    }

    override suspend fun updatePlayList(track: Track, id: Long) =
        repository.updatePlayList(
            Track(
                trackName = track.trackName,
                artistName = track.artistName,
                trackTimeMillis = track.trackTimeMillis,
                artworkUrl100 = track.artworkUrl100,
                trackId = track.trackId,
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                previewUrl = track.previewUrl,
                isLiked = false
            ), id
        )

    override suspend fun getCountTracks(id: Long) =
        repository.getCountTracks(id)


    override fun tracksInPlayList(id: Long): Flow<List<Track>> {
        return repository.listTrackPlaylist(id).map {
            it.reversed()
        }
    }

    override suspend fun getTimesTracks(id: Long): String {
        return resourceManager.formatMinutes(repository.getTimesTracks(id).toInt())
    }

    override suspend fun deleteTrack(id: Long) {
        repository.deleteTrack(id)
    }

    override suspend fun deleteTrackFromPlaylist(trackId: String, playlistId: Long) {
        repository.deleteTrackFromPlaylist(trackId, playlistId)
    }

    override suspend fun deletePlayList(id: Long) {
        repository.deletePlaylist(id)
    }

    override suspend fun shareTracks(id: Long) {
        repository.shareTracks(id)
    }

    override suspend fun editPlayList(playList: PlayList) {
        repository.editPlayList(playList)
    }

    override suspend fun getPlayList(id: Long): PlayList {
        return repository.getPlayList(id)
    }
}