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
            previewUrl = track.previewUrl,
            isLiked = track.isLiked
        )
    }

    fun mapToTrack(trackUI: TrackUI): Track {
        return Track(
            trackName = trackUI.trackName,
            artistName = trackUI.artistName,
            trackTimeMillis = trackUI.trackTimeMillis,
            artworkUrl100 = trackUI.artworkUrl100,
            trackId = trackUI.trackId,
            collectionName = trackUI.collectionName,
            releaseDate = trackUI.releaseDate,
            primaryGenreName = trackUI.primaryGenreName,
            country = trackUI.country,
            previewUrl = trackUI.previewUrl,
            isLiked = trackUI.isLiked
        )
    }

    fun mapToTrackUIList(tracks: List<Track>): List<TrackUI> {
        return tracks.map { track -> mapToTrackUI(track) }
    }
}