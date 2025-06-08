package com.example.playlistmaker.player.presentation

data class PlayerState(
    val state: State = State.DEFAULT,
    val currentTime: String = "00:00"
) {
    enum class State {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED
    }
}