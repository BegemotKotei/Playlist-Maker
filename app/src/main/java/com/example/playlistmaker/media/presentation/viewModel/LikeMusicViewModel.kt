package com.example.playlistmaker.media.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.LikeTrackInteractor
import com.example.playlistmaker.search.presentation.mapper.TrackMapper
import com.example.playlistmaker.search.presentation.models.TrackUI
import kotlinx.coroutines.launch

class LikeMusicViewModel(private val likeTrackInteractor: LikeTrackInteractor) : ViewModel() {
    private val _tracks = MutableLiveData<List<TrackUI>>()
    val tracksLiked: LiveData<List<TrackUI>>
        get() = _tracks

    fun update() {
        viewModelScope.launch {
            likeTrackInteractor.likeTrackList().collect {
                _tracks.postValue(TrackMapper.mapToTrackUIList(it))
            }
        }
    }
}