package com.example.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MusicPlayerViewModel(
    private val playerInteractor: PlayerInteractor,
) : ViewModel() {
    private val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    private var progressUpdateJob: Job? = null

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    val playerState: LiveData<PlayerState> = _playerState

    override fun onCleared() {
        super.onCleared()
        progressUpdateJob?.cancel()
        releasePlayer()
    }

    fun preparePlayer(url: String) {
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
        } catch (e: Exception) {
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