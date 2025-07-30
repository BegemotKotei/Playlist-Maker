package com.example.playlistmaker.player.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class MusicPlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    private val mainThreadHandler by lazy { Handler(Looper.getMainLooper()) }

    private val _playerState = MutableLiveData<PlayerState>().apply {
        value = PlayerState()
    }
    val playerState: LiveData<PlayerState>
        get() = _playerState

    private fun createUpdateTimerMusicTask(): Runnable {
        return object : Runnable {
            override fun run() {
                val currentPosition = playerInteractor.getCurrentPosition()
                val currentTime = dateFormat.format(currentPosition)

                if (currentPosition < MUSIC_TIME) {
                    _playerState.postValue(
                        _playerState.value?.copy(
                            currentTime = currentTime
                        )
                    )
                } else {
                    playerInteractor.seekTo(0)
                    playerInteractor.playPause()
                    _playerState.postValue(
                        PlayerState(
                            state = PlayerState.State.PREPARED,
                            currentTime = "00:00"
                        )
                    )
                }
                if (playerInteractor.isPlaying()) {
                    mainThreadHandler.postDelayed(this, DELAY)
                }
            }
        }
    }

    private fun startTimerMusic() {
        mainThreadHandler.post(createUpdateTimerMusicTask())
    }

    override fun onCleared() {
        super.onCleared()
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
                    _playerState.postValue(PlayerState(state = PlayerState.State.PREPARED))
                },
                onCompletion = {
                    _playerState.postValue(
                        PlayerState(
                            state = PlayerState.State.PREPARED,
                            currentTime = "00:00"
                        )
                    )
                }
            )
        } catch (e: Exception) {
            _playerState.postValue(PlayerState(state = PlayerState.State.DEFAULT))
        }
    }

    private fun startPlayer() {
        playerInteractor.play()
        _playerState.postValue(
            _playerState.value?.copy(
                state = PlayerState.State.PLAYING
            )
        )
        startTimerMusic()
    }

    fun pausePlayer() {
        playerInteractor.pause()
        mainThreadHandler.removeCallbacksAndMessages(null)
        _playerState.postValue(
            _playerState.value?.copy(
                state = PlayerState.State.PAUSED
            )
        )
    }

    fun releasePlayer() {
        mainThreadHandler.removeCallbacksAndMessages(null)
        playerInteractor.releasePlayer()
        _playerState.postValue(PlayerState())
    }

    fun playbackControl() {
        when (_playerState.value?.state) {
            PlayerState.State.PLAYING -> pausePlayer()
            PlayerState.State.PREPARED,
            PlayerState.State.PAUSED -> startPlayer()

            else -> Unit
        }
    }

    private companion object {
        const val MUSIC_TIME = 29900
        const val DELAY = 300L
    }
}