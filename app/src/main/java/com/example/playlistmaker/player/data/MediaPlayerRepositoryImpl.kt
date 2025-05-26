package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.MediaPlayerRepository

class MediaPlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : MediaPlayerRepository {

    override fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener { onPrepared() }
        mediaPlayer.setOnCompletionListener { onCompletion() }
    }

    override fun start() = mediaPlayer.start()
    override fun pause() = mediaPlayer.pause()
    override fun release() = mediaPlayer.release()
    override fun seekTo(position: Int) = mediaPlayer.seekTo(position)
    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition
    override fun isPlaying(): Boolean = mediaPlayer.isPlaying
}