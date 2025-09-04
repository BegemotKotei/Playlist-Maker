package com.example.playlistmaker.playlist_create.presentation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayListUI(
    val id: Long = 0,
    val namePlayList: String,
    val aboutPlayList: String?,
    val roadToFileImage: String,
    val count: Int = 0,
) : Parcelable
