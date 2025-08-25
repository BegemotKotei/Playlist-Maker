package com.example.playlistmaker.db.mappers

import com.example.playlistmaker.db.entity.PlayListEntity
import com.example.playlistmaker.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.playlist_create.domain.models.PlayList
import com.example.playlistmaker.search.domain.models.Track

class PlayListDbMapper {
    fun mapToPlayList(playList: PlayListEntity): PlayList {
        return PlayList(
            id = playList.idPlayList,
            namePlayList = playList.namePlayList,
            aboutPlayList = playList.aboutPlayList,
            roadToFileImage = playList.roadToFileImage
        )
    }

    fun mapToPlayListEntity(playList: PlayList): PlayListEntity {
        return PlayListEntity(
            namePlayList = playList.namePlayList,
            aboutPlayList = playList.aboutPlayList,
            roadToFileImage = playList.roadToFileImage
        )
    }

    fun mapToListTrack(playListTrackEntity: List<PlaylistTrackEntity>): List<Track> {
        return playListTrackEntity.map { playListTrack ->
            Track(
                trackName = playListTrack.trackName,
                artistName = playListTrack.artistName,
                trackTimeMillis = playListTrack.trackTimeMillis,
                artworkUrl100 = playListTrack.artworkUrl100,
                trackId = playListTrack.trackId,
                collectionName = playListTrack.collectionName,
                releaseDate = playListTrack.releaseDate,
                primaryGenreName = playListTrack.primaryGenreName,
                country = playListTrack.country,
                previewUrl = playListTrack.previewUrl,
                isLiked = false
            )
        }
    }
}
