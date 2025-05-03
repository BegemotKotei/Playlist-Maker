package com.example.playlistmaker.presentation.player

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class MusicPlayerViewModel: ViewModel() {
    private var mediaPlayer: MediaPlayer? = null
    private var playerState = PlayerState.DEFAULT
    private var currentPosition = 0
    private val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    fun initMediaPlayer(url: String?, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        if (url.isNullOrBlank()) return

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(url)
                setOnPreparedListener {
                    playerState = PlayerState.PREPARED
                    onPrepared()
                }
                setOnCompletionListener {
                    playerState = PlayerState.PREPARED
                    onCompletion()
                }
                setOnErrorListener { _, _, _ ->
                    false
                }
                prepareAsync()
            } catch (e: IOException) {
                playerState = PlayerState.DEFAULT
            }
        }
    }

    fun playbackControl() {
        when (playerState) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            else -> Unit
        }
    }

    fun getCurrentPosition(): String {
        return mediaPlayer?.let {
            dateFormat.format(it.currentPosition.toLong())
        } ?: "00:00"
    }

    fun isPlaying() = playerState == PlayerState.PLAYING

    fun getFormattedDate(inputDate: String?) = DateFormatter.formatReleaseDate(inputDate)

    private fun startPlayer() {
        mediaPlayer?.let {
            it.seekTo(currentPosition)
            it.start()
            playerState = PlayerState.PLAYING
        }
    }

    private fun pausePlayer() {
        mediaPlayer?.let {
            currentPosition = it.currentPosition
            it.pause()
            playerState = PlayerState.PAUSED
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

}