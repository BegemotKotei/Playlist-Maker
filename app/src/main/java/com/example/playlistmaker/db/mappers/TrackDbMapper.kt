package com.example.playlistmaker.db.mappers

import com.example.playlistmaker.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.models.Track

class TrackDbMapper {
    fun mapToTrack(trackEntity: TrackEntity): Track {
        return Track(
            trackName = trackEntity.trackName,
            artistName = trackEntity.artistName,
            trackTimeMillis = trackEntity.trackTimeMillis,
            artworkUrl100 = trackEntity.artworkUrl100,
            trackId = trackEntity.trackId,
            collectionName = trackEntity.collectionName,
            releaseDate = trackEntity.releaseDate,
            primaryGenreName = trackEntity.genreName,
            country = trackEntity.country,
            previewUrl = trackEntity.previewUrl,
            isLiked = trackEntity.isLiked
        )
    }

    fun mapToTrackEntity(track: Track): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            previewUrl = track.previewUrl,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            genreName = track.primaryGenreName,
            country = track.country,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            isLiked = track.isLiked
        )
    }

    fun mapToListTrack(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { trackEntity -> mapToTrack(trackEntity) }
    }
}