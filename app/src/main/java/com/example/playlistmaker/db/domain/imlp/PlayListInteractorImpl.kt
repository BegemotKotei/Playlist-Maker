package com.example.playlistmaker.db.domain.imlp

import com.example.playlistmaker.db.domain.PlayListInteractor
import com.example.playlistmaker.db.domain.PlayListRepository
import com.example.playlistmaker.playlist_create.domain.models.PlayList
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlayListInteractorImpl(private val repository: PlayListRepository) :
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

}