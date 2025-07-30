package com.example.playlistmaker.search.presentation.stateHolders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.ResponseStatus
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.sharedpref.SharedPrefsInteractor
import com.example.playlistmaker.search.presentation.mapper.TrackMapper
import com.example.playlistmaker.search.presentation.models.TrackUI
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragmentViewModel(
    private val sharedPrefsInteractor: SharedPrefsInteractor,
    private val tracksInteractor: TracksInteractor
) : ViewModel() {
    private var searchJob: Job? = null
    private var latestSearchText: String? = null
    private val _isClickAllowed = MutableLiveData<Boolean>()
    val isClickAllowed: LiveData<Boolean>
        get() = _isClickAllowed

    private var searchText = ""
    private val historyTracks: List<Track>
        get() = getHistory()

    private val _showHistory = MutableLiveData(false)
    val showHistory: LiveData<Boolean>
        get() = _showHistory

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>>
        get() = _tracks

    private val _searchStatus = MutableLiveData<ResponseStatus>()
    val searchStatus: LiveData<ResponseStatus>
        get() = _searchStatus

    fun showHistoryBoolean(answer: Boolean, rewrite: Boolean = true) {
        if (answer) {
            if (historyTracks.isNotEmpty()) {
                _tracks.postValue(historyTracks)
                _showHistory.postValue(answer)
            } else {
                _showHistory.postValue(false)
            }
        } else if (rewrite) {
            _showHistory.postValue(false)
            _tracks.postValue(emptyList())
        }
    }

    fun setSearchText(text: String) {
        searchText = text
    }

    fun clickDebounce() {
        _isClickAllowed.value?.let { isAllowed ->
            if (isAllowed) {
                _isClickAllowed.postValue(false)
                viewModelScope.launch {
                    delay(CLICK_DEBOUNCE_DELAY)
                    _isClickAllowed.postValue(true)
                }
            }
        }
    }

    fun searchDebounce() {
        if (latestSearchText == searchText) {
            return
        }
        latestSearchText = searchText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchTracks(searchText)
        }
    }

    fun searchTracks(trackName: String) {
        _searchStatus.postValue(ResponseStatus.LOADING)
        viewModelScope.launch {
            tracksInteractor
                .searchTracks(trackName)
                .collect { pair ->
                    _searchStatus.postValue(pair.second)
                    _tracks.postValue(pair.first)
                    if (pair.second == ResponseStatus.SUCCESS) {
                        _showHistory.postValue(false)
                    }
                }
        }
    }

    private fun getHistory() = sharedPrefsInteractor.readWriteClearWithoutConsumer(USE_READ, null)

    fun writeHistory(trackUI: TrackUI?) {
        val track = trackUI?.let { TrackMapper.mapToTrack(it) }
        sharedPrefsWork(USE_WRITE, track)
    }

    fun clearHistory() {
        sharedPrefsWork(USE_CLEAR)
        _tracks.postValue(historyTracks)
    }

    fun resume() {
        if (_showHistory.value == true) {
            _tracks.postValue(historyTracks)
        }

        _isClickAllowed.postValue(true)
    }

    private fun sharedPrefsWork(uses: String, track: Track? = null) {
        sharedPrefsInteractor.readWriteClear(
            uses,
            track,
            consumer = object : SharedPrefsInteractor.SharedPrefsConsumer {
                override fun consume(foundSharedPrefs: ArrayList<Track>) {

                }
            })
    }

    private companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val USE_CLEAR = "clear"
        const val USE_READ = "read"
        const val USE_WRITE = "write"
    }
}