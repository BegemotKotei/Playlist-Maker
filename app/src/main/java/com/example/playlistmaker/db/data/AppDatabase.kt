package com.example.playlistmaker.db.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.db.dao.PlayListDao
import com.example.playlistmaker.db.dao.TrackDao
import com.example.playlistmaker.db.entity.PlayListEntity
import com.example.playlistmaker.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.db.entity.TrackEntity

@Database(
    version = 6,
    entities = [
        TrackEntity::class,
        PlayListEntity::class,
        PlaylistTrackEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playListDao(): PlayListDao
}