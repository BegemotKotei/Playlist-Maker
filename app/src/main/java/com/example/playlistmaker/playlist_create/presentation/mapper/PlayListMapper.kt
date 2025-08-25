package com.example.playlistmaker.playlist_create.presentation.mapper

import com.example.playlistmaker.playlist_create.domain.models.PlayList
import com.example.playlistmaker.playlist_create.presentation.models.PlayListUI

object PlayListMapper {
    fun mapToPlayListUI(playList: PlayList): PlayListUI {
        return PlayListUI(
            id = playList.id,
            namePlayList = playList.namePlayList,
            aboutPlayList = playList.aboutPlayList,
            roadToFileImage = playList.roadToFileImage,
            count = playList.count
        )
    }

    fun mapToPlayList(playListUI: PlayListUI): PlayList {
        return PlayList(
            id = playListUI.id,
            namePlayList = playListUI.namePlayList,
            aboutPlayList = playListUI.aboutPlayList,
            roadToFileImage = playListUI.roadToFileImage,
        )
    }

    fun mapToPlayListUIList(playLists: List<PlayList>): List<PlayListUI> {
        return playLists.map { playList ->
            mapToPlayListUI(playList)
        }
    }
}