package com.example.playlistmaker.playlist_create.presentation

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.PlayListInteractor
import com.example.playlistmaker.playlist_create.domain.SaveImageToMemoryInteractor
import com.example.playlistmaker.playlist_create.presentation.mapper.PlayListMapper
import com.example.playlistmaker.playlist_create.presentation.models.PlayListUI
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CreatePlayListFragmentViewModel(
    private val playListInteractor: PlayListInteractor,
    private val saveImageInteractor: SaveImageToMemoryInteractor
) : ViewModel() {

    fun createNewPlayList(name: String, about: String, road: Uri?) {
        viewModelScope.launch {
            playListInteractor.newPlayList(
                PlayListMapper.mapToPlayList(
                    PlayListUI(
                        namePlayList = name,
                        aboutPlayList = about,
                        roadToFileImage = saveImageToFile(road)
                    )
                )
            )
        }
    }

    fun editNewPlayList(oldRoad: Uri?, id: Long, name: String, about: String, road: Uri?) {
        viewModelScope.launch {
            playListInteractor.editPlayList(
                PlayListMapper.mapToPlayList(
                    PlayListUI(
                        id = id,
                        namePlayList = name,
                        aboutPlayList = about,
                        roadToFileImage = oldOrNewUri(oldRoad, road)
                    )
                )
            )
        }
    }

    private fun oldOrNewUri(oldRoad: Uri?, road: Uri?): String {
        return if (oldRoad == road) {
            oldRoad.toString()
        } else {
            saveImageToFile(road)
        }
    }

    private val _nameImage = MutableLiveData<String>()
    val nameImage: LiveData<String>
        get() = _nameImage

    fun saveImageToFile(uri: Uri?): String {
        var road = ""
        if (uri != null) {
            runBlocking { road = saveImageInteractor.saveImageToFile(uri.toString()) }
        }
        return road
    }
}