package com.example.playlistmaker.about_playlist.presentation.view_model

import com.example.playlistmaker.search.presentation.models.TrackUI

data class AboutPlaylistState(
    val tracks: List<TrackUI>,
    val time: String,
    val count: String,
)