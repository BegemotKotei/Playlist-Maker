package com.example.playlistmaker.about_playlist.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.resourceManager.IResourceManager
import com.example.playlistmaker.db.domain.PlayListInteractor
import com.example.playlistmaker.playlist_create.presentation.mapper.PlayListMapper
import com.example.playlistmaker.playlist_create.presentation.models.PlayListUI
import com.example.playlistmaker.search.presentation.mapper.TrackMapper
import com.example.playlistmaker.search.presentation.models.TrackUI
import kotlinx.coroutines.launch

class AboutPlayListFragmentViewModel(
    private val playListInteractor: PlayListInteractor,
    private val resourceManager: IResourceManager

) : ViewModel() {
    private val _aboutPlayListState = MutableLiveData<AboutPlaylistState>()
    val aboutPlayListState: LiveData<AboutPlaylistState>
        get() = _aboutPlayListState
    private val _playlist = MutableLiveData<PlayListUI>()
    val playList: LiveData<PlayListUI>
        get() = _playlist

    fun update(id: Long) {
        viewModelScope.launch {
            val tracks = ArrayList<TrackUI>()
            val time = playListInteractor.getTimesTracks(id)
            val count = resourceManager.getTracksPlural(playListInteractor.getCountTracks(id))
            playListInteractor.tracksInPlayList(id).collect {
                tracks.addAll(TrackMapper.mapToTrackUIList(it))
            }
            _aboutPlayListState.postValue(AboutPlaylistState(tracks, time, count))
        }
    }
    fun updatePlayList(id: Long) {
        viewModelScope.launch {
            _playlist.postValue(
                PlayListMapper.mapToPlayListUI(playListInteractor.getPlayList(id))
            )
        }
    }

    fun deleteTrack(trackId: String, playListId: Long) {
        viewModelScope.launch {
            playListInteractor.deleteTrackFromPlaylist(trackId, playListId)
            update(playListId)
        }
    }

    fun shareTracks(id: Long) {
        viewModelScope.launch {
            playListInteractor.shareTracks(id)
        }
    }

     suspend fun deletePlaylist(id: Long) {
            playListInteractor.deletePlayList(id)
    }
}