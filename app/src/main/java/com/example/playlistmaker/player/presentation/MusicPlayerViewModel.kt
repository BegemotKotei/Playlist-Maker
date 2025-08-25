package com.example.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.LikeTrackInteractor
import com.example.playlistmaker.db.domain.PlayListInteractor
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.playlist_create.domain.models.PlayList
import com.example.playlistmaker.search.presentation.mapper.TrackMapper
import com.example.playlistmaker.search.presentation.models.TrackUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MusicPlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val likeTrackInteractor: LikeTrackInteractor,
    private val playListInteractor: PlayListInteractor,
    private val trackUI: TrackUI
) : ViewModel() {
    private val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    private var progressUpdateJob: Job? = null
    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    val playerState: LiveData<PlayerState> = _playerState
    private var _isLiked = MutableLiveData<Boolean>()
    val isLiked: LiveData<Boolean>
        get() = _isLiked
    private val _playList = MutableLiveData<List<PlayList>>()
    val playLists: LiveData<List<PlayList>>
        get() = _playList
    private var _trackAddedFlow = MutableSharedFlow<ToastState>()
    val trackAddedFlow = _trackAddedFlow.asSharedFlow()
    private var isPlayerPrepared = false


    override fun onCleared() {
        super.onCleared()
        progressUpdateJob?.cancel()
        releasePlayer()
        isPlayerPrepared = false
    }

    fun update() {
        viewModelScope.launch {
            playListInteractor.listPlayList().collect { playLists ->
                _playList.postValue(
                    playLists.map { playlist ->
                        playlist.copy(count = playListInteractor.getCountTracks(playlist.id))
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

    fun getLikeStatus(id: String) = viewModelScope.launch(Dispatchers.IO) {
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

    fun preparePlayer(url: String) {
        if (isPlayerPrepared) {
            return
        }
        if (playerInteractor.isPlaying()) {
            playerInteractor.releasePlayer()
        }
        try {
            playerInteractor.prepareTrack(
                url = url,
                onPrepared = {
                    _playerState.postValue(PlayerState.Prepared())
                },
                onCompletion = {
                    _playerState.postValue(PlayerState.Prepared())
                }
            )
        } catch (_: Exception) {
            _playerState.postValue(PlayerState.Default())
        }
    }

    fun pausePlayer() {
        playerInteractor.pause()
        progressUpdateJob?.cancel()
        _playerState.postValue(
            when (val currentState = _playerState.value) {
                is PlayerState.Playing -> PlayerState.Paused(currentState.progress)
                else -> PlayerState.Paused(PlayerState.DEFAULT_TIME)
            }
        )
    }

    fun releasePlayer() {
        progressUpdateJob?.cancel()
        playerInteractor.releasePlayer()
        isPlayerPrepared = false
        _playerState.postValue(PlayerState.Default())
    }

    fun playbackControl() {
        when (_playerState.value) {
            is PlayerState.Playing -> pausePlayer()
            is PlayerState.Prepared, is PlayerState.Paused -> startPlayer()
            else -> Unit
        }
    }

    private fun startPlayer() {
        playerInteractor.play()
        _playerState.postValue(
            when (val currentState = _playerState.value) {
                is PlayerState.Prepared -> PlayerState.Playing(PlayerState.DEFAULT_TIME)
                is PlayerState.Paused -> PlayerState.Playing(currentState.progress)
                else -> PlayerState.Playing(PlayerState.DEFAULT_TIME)
            }
        )
        startProgressUpdates()
    }

    private fun startProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = viewModelScope.launch {
            while (isActive && playerInteractor.isPlaying()) {
                updateProgress()
                delay(DELAY)
            }
        }
    }

    private fun updateProgress() {
        val currentPosition = playerInteractor.getCurrentPosition()
        val currentTime = dateFormat.format(currentPosition)

        if (currentPosition < MUSIC_TIME) {
            _playerState.postValue(PlayerState.Playing(currentTime))
        } else {
            playerInteractor.seekTo(0)
            playerInteractor.playPause()
            _playerState.postValue(PlayerState.Prepared())
        }
    }

    private companion object {
        const val MUSIC_TIME = 29900
        const val DELAY = 300L
    }
}