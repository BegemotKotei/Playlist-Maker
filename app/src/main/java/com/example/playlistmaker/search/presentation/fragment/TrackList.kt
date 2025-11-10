package com.example.playlistmaker.search.presentation.fragment

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.playlistmaker.search.presentation.models.TrackUI

@Composable
fun TrackList(tracks: List<TrackUI>, onTrackClick: (TrackUI) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(tracks) { track ->
            Log.e("Track in list Click", "Track list done")
            TrackItem(track = track, onClick = { onTrackClick(track) })
        }
    }
}