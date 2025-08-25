package com.example.playlistmaker.playlist_create.presentation.models

data class PlayListUI(
    val id: Long = 0,
    val namePlayList: String,
    val aboutPlayList: String?,
    val roadToFileImage: String,
    val count: Int = 0,
)
