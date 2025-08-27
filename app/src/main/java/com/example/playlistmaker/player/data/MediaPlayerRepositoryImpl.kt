package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.MediaPlayerRepository

class MediaPlayerRepositoryImpl : MediaPlayerRepository {
    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared = false

    override fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                isPrepared = true
                onPrepared()
            }
            setOnCompletionListener {
                onCompletion()
            }
            prepareAsync()
        }
    }

    override fun start() {
        if (isPrepared) {
            mediaPlayer?.start()
        }
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
    }

    override fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying ?: false
        } catch (_: IllegalStateException) {
            false
        }
    }
}