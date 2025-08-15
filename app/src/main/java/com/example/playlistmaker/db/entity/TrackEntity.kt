package com.example.playlistmaker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_table")
data class TrackEntity(
    @PrimaryKey
    val trackId: String,
    val previewUrl: String?,
    val trackName: String,
    val artistName: String,
    val collectionName: String?,
    val releaseDate: String?,
    val genreName: String?,
    val country: String?,
    val trackTimeMillis: String,
    val artworkUrl100: String?,
    var isLiked: Boolean = false
)