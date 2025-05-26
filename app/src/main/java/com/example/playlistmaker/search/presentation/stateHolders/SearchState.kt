package com.example.playlistmaker.search.presentation.stateHolders

import com.example.playlistmaker.search.domain.models.ResponseStatus
import com.example.playlistmaker.search.domain.models.Track

sealed class SearchState {
    data class Success(val tracks: List<Track>, val history: List<Track>) : SearchState()
    data class Error(val status: ResponseStatus, val history: List<Track>) : SearchState()
    data class Loading(val history: List<Track>) : SearchState()
    data class Empty(val history: List<Track>) : SearchState()
}
