package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.MediaPlayerRepository
import com.example.playlistmaker.player.domain.api.PlayerInteractor

class PlayerInteractorImpl(
    private val repository: MediaPlayerRepository
) : PlayerInteractor {

    override fun prepareTrack(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        repository.prepare(url, onPrepared, onCompletion)
    }

    override fun playPause(): Boolean {
        return if (isPlaying()) {
            pause()
            false
        } else {
            play()
            true
        }
    }

    override fun play() = repository.start()
    override fun pause() = repository.pause()
    override fun seekTo(position: Int) = repository.seekTo(position)
    override fun getCurrentPosition(): Int = repository.getCurrentPosition()
    override fun releasePlayer() = repository.release()
    override fun isPlaying(): Boolean = repository.isPlaying()
}