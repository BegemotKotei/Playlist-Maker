package com.example.playlistmaker.search.presentation.stateHolders

import com.example.playlistmaker.search.domain.models.ResponseStatus
import com.example.playlistmaker.search.domain.models.Track

sealed class SearchState {
    data class Success(val tracks: List<Track>) : SearchState()
    data class Error(val status: ResponseStatus) : SearchState()
    data object Loading : SearchState()
    data object Empty : SearchState()
}
