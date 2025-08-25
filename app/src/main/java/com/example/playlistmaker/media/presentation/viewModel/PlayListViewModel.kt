package com.example.playlistmaker.media.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.PlayListInteractor
import com.example.playlistmaker.playlist_create.presentation.mapper.PlayListMapper
import com.example.playlistmaker.playlist_create.presentation.models.PlayListUI
import kotlinx.coroutines.launch

class PlayListViewModel(private val playlistInteractor: PlayListInteractor) : ViewModel() {
    private val _playList = MutableLiveData<List<PlayListUI>>()
    val playLists: LiveData<List<PlayListUI>>
        get() = _playList

    fun update() {
        viewModelScope.launch {
            playlistInteractor.listPlayList().collect {
                _playList.postValue(
                    it.map { playlist ->
                        PlayListMapper.mapToPlayListUI(
                            playlist.copy(
                                count = playlistInteractor.getCountTracks(playlist.id)
                            )
                        )
                    }
                )
            }
        }
    }
}