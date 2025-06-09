package com.example.playlistmaker.search.presentation.stateHolders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.ResponseStatus
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.sharedpref.SharedPrefsInteractor

class SearchActivityViewModel(
    private val sharedPrefsInteractor: SharedPrefsInteractor,
    private val tracksInteractor: TracksInteractor
) : ViewModel() {
    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState>
        get() = _searchState
    private var currentHistory: List<Track> = emptyList()

    fun searchTracks(trackName: String) {
        _searchState.postValue(SearchState.Loading(currentHistory))

        tracksInteractor.searchTracks(
            trackName,
            consumer = object : TracksInteractor.TracksConsumer {
                override fun consume(foundTrack: List<Track>, status: ResponseStatus) {
                    when (status) {
                        ResponseStatus.SUCCESS -> {
                            if (foundTrack.isEmpty()) {
                                _searchState.postValue(SearchState.Empty(currentHistory))
                            } else {
                                _searchState.postValue(
                                    SearchState.Success(
                                        foundTrack,
                                        currentHistory
                                    )
                                )
                            }
                        }

                        ResponseStatus.ERROR -> {
                            _searchState.postValue(SearchState.Error(status, currentHistory))
                        }
                    }
                }
            })
    }

    fun sharedPrefsWork(uses: String, track: Track? = null) {
        sharedPrefsInteractor.readWriteClear(
            uses,
            track,
            consumer = object : SharedPrefsInteractor.SharedPrefsConsumer {
                override fun consume(foundSharedPrefs: ArrayList<Track>) {
                    currentHistory = foundSharedPrefs
                    _searchState.value?.let { currentState ->
                        val newState = when (currentState) {
                            is SearchState.Success -> currentState.copy(history = foundSharedPrefs)
                            is SearchState.Error -> currentState.copy(history = foundSharedPrefs)
                            is SearchState.Loading -> SearchState.Loading(foundSharedPrefs)
                            is SearchState.Empty -> SearchState.Empty(foundSharedPrefs)
                        }
                        _searchState.postValue(newState)
                    }
                }
            })
    }
}