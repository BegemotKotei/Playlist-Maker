package com.example.playlistmaker.media.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.LikeTrackInteractor
import com.example.playlistmaker.db.domain.PlayListInteractor
import com.example.playlistmaker.playlist_create.presentation.mapper.PlayListMapper
import com.example.playlistmaker.playlist_create.presentation.models.PlayListUI
import com.example.playlistmaker.search.presentation.mapper.TrackMapper
import com.example.playlistmaker.search.presentation.models.TrackUI
import kotlinx.coroutines.launch

class MediaLibraryViewModel(
    private val likeTrackInteractor: LikeTrackInteractor,
    private val playlistInteractor: PlayListInteractor
) : ViewModel() {
    private val _tracks = MutableLiveData<List<TrackUI>>()
    val tracks: LiveData<List<TrackUI>> get() = _tracks

    private val _playlists = MutableLiveData<List<PlayListUI>>()
    val playlists: LiveData<List<PlayListUI>> get() = _playlists

    fun getFavoriteTracks() {
        viewModelScope.launch {
            likeTrackInteractor.likeTrackList().collect { trackModels ->
                _tracks.postValue(TrackMapper.mapToTrackUIList(trackModels))
            }
        }
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor.listPlayList().collect { playlistList ->
                val uiPlaylists = playlistList.map { playlist ->
                    val count = playlistInteractor.getCountTracks(playlist.id)
                    PlayListMapper.mapToPlayListUI(playlist.copy(count = count))
                }
                _playlists.postValue(uiPlaylists)
            }
        }
    }
}
