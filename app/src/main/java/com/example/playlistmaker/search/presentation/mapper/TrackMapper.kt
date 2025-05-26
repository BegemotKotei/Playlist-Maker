package com.example.playlistmaker.search.presentation.mapper

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.models.TrackUI

object TrackMapper {
    fun mapToTrackUI(track: Track): TrackUI {
        return TrackUI(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            trackId = track.trackId,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    fun mapToTrack(trackUi: TrackUI): Track {
        return Track(
            trackName = trackUi.trackName,
            artistName = trackUi.artistName,
            trackTimeMillis = trackUi.trackTimeMillis,
            artworkUrl100 = trackUi.artworkUrl100,
            trackId = trackUi.trackId,
            collectionName = trackUi.collectionName,
            releaseDate = trackUi.releaseDate,
            primaryGenreName = trackUi.primaryGenreName,
            country = trackUi.country,
            previewUrl = trackUi.previewUrl
        )
    }
}