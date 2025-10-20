package com.example.playlistmaker.player.presentation

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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MusicPlayerViewModel(
    private val likeTrackInteractor: LikeTrackInteractor,
    private val playListInteractor: PlayListInteractor,
    private val trackUI: TrackUI
) : ViewModel() {
    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    val playerState: LiveData<PlayerState> = _playerState

    private var _isLiked = MutableLiveData<Boolean>()
    val isLiked: LiveData<Boolean> = _isLiked

    private val _playList = MutableLiveData<List<PlayListUI>>()
    val playLists: LiveData<List<PlayListUI>> = _playList

    private var _trackAddedFlow = MutableSharedFlow<ToastState>()
    val trackAddedFlow = _trackAddedFlow.asSharedFlow()

    fun updatePlaybackState(isPlaying: Boolean) {
        val currentState = _playerState.value
        _playerState.postValue(
            if (isPlaying) PlayerState.Playing(currentState?.progress ?: "00:00")
            else PlayerState.Paused(currentState?.progress ?: "00:00")
        )
    }

    fun update() {
        viewModelScope.launch {
            playListInteractor.listPlayList().collect { playLists ->
                _playList.postValue(
                    playLists.map { playlist ->
                        PlayListMapper.mapToPlayListUI(
                            playlist.copy(
                                count = playListInteractor.getCountTracks(playlist.id)
                            )
                        )
                    }
                )
            }
        }
    }

    fun insertInPlayList(id: Long, namePlayList: String) {
        viewModelScope.launch {
            val trackAdded =
                playListInteractor.updatePlayList(track = TrackMapper.mapToTrack(trackUI), id)
            _trackAddedFlow.emit(ToastState(trackAdded, namePlayList))
            update()
        }
    }

    fun getLikeStatus(id: String) = viewModelScope.launch {
        val like = likeTrackInteractor.isTrackLiked(id)
        _isLiked.postValue(like)
    }

    fun changeLikeStatus() {
        val newLikeStatus = _isLiked.value != true
        _isLiked.postValue(newLikeStatus)

        viewModelScope.launch {
            if (newLikeStatus) {
                likeTrackInteractor.addLikeTrack(track = TrackMapper.mapToTrack(trackUI))
            } else {
                likeTrackInteractor.deleteLikeTrack(track = TrackMapper.mapToTrack(trackUI))
            }
        }
    }
}