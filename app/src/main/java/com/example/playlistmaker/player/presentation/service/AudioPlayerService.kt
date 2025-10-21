package com.example.playlistmaker.player.presentation.service

import com.example.playlistmaker.search.presentation.models.TrackUI
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerService{
    fun preparePlayer(track: TrackUI)
    fun play()
    fun pause()
    fun stop()
    fun getIsPlaying(): StateFlow<Boolean>
    fun getCurrentTime(): StateFlow<String>
    fun togglePlayback()
    fun showNotification()
    fun showNotificationIfPlaying()
    fun hideNotification()
}