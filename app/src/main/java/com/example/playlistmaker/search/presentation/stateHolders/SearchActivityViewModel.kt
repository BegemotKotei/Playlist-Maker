package com.example.playlistmaker.search.presentation.stateHolders

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator
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
    private val _tracksHistory = MutableLiveData<ArrayList<Track>>()
    val tracksHistory: LiveData<ArrayList<Track>>
        get() = _tracksHistory

    fun searchTracks(trackName: String) {
        _searchState.postValue(SearchState.Loading)

        tracksInteractor.searchTracks(
            trackName,
            consumer = object : TracksInteractor.TracksConsumer {
                override fun consume(foundTrack: List<Track>, status: ResponseStatus) {
                    when (status) {
                        ResponseStatus.SUCCESS -> {
                            if (foundTrack.isEmpty()) {
                                _searchState.postValue(SearchState.Empty)
                            } else {
                                _searchState.postValue(SearchState.Success(foundTrack))
                            }
                        }

                        ResponseStatus.ERROR -> {
                            _searchState.postValue(SearchState.Error(status))
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
                    _tracksHistory.postValue(foundSharedPrefs)
                }
            })
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val sharedPrefs = Creator.provideSharedPrefsInteractor(context)
            val tracksInteractor = Creator.provideTracksInteractor()
            return SearchActivityViewModel(
                sharedPrefsInteractor = sharedPrefs,
                tracksInteractor = tracksInteractor
            ) as T
        }
    }
}