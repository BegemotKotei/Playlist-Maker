package com.example.playlistmaker.player.presentation

import androidx.annotation.DrawableRes
import com.example.playlistmaker.R

sealed class PlayerState(
    val isPlayButtonEnabled: Boolean,
    @DrawableRes val buttonIconRes: Int,
    val progress: String,
) {
    class Default : PlayerState(
        isPlayButtonEnabled = false,
        buttonIconRes = R.drawable.ic_play,
        progress = DEFAULT_TIME
    )

    class Prepared : PlayerState(
        isPlayButtonEnabled = true,
        buttonIconRes = R.drawable.ic_play,
        progress = DEFAULT_TIME
    )

    class Playing(progress: String) : PlayerState(
        isPlayButtonEnabled = true,
        buttonIconRes = R.drawable.pause_ic,
        progress = progress
    )

    class Paused(progress: String) : PlayerState(
        isPlayButtonEnabled = true,
        buttonIconRes = R.drawable.ic_play,
        progress = progress
    )
    companion object {
        const val DEFAULT_TIME = "00:00"
    }
}