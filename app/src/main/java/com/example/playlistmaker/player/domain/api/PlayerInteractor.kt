package com.example.playlistmaker.player.domain.api

interface PlayerInteractor {
    fun prepareTrack(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun playPause(): Boolean
    fun play()
    fun pause()
    fun seekTo(position: Int)
    fun getCurrentPosition(): Int
    fun releasePlayer()
    fun isPlaying(): Boolean
}