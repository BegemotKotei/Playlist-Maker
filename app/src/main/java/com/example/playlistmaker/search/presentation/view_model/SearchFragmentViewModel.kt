package com.example.playlistmaker.search.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.ResponseStatus
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.sharedpref.SharedPrefsInteractor
import com.example.playlistmaker.search.presentation.mapper.TrackMapper
import com.example.playlistmaker.search.presentation.models.TrackUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SearchScreenState(
    val searchText: String = "",
    val tracks: List<TrackUI> = emptyList(),
    val historyTracks: List<TrackUI> = emptyList(),
    val searchStatus: ResponseStatus = ResponseStatus.DEFAULT,
    val showHistory: Boolean = false,
)

class SearchFragmentViewModel(
    private val sharedPrefsInteractor: SharedPrefsInteractor,
    private val tracksInteractor: TracksInteractor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchScreenState())
    val uiState: StateFlow<SearchScreenState> = _uiState.asStateFlow()

    private var isClickAllowed = true

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val history = getHistory().map { TrackMapper.mapToTrackUI(it) }
            _uiState.update { currentState ->
                currentState.copy(
                    historyTracks = history,
                    showHistory = currentState.searchText.isEmpty() && history.isNotEmpty()
                )
            }
        }
    }

    private suspend fun getHistory(): List<Track> {
        return withContext(Dispatchers.IO) {
            sharedPrefsInteractor.readWriteClearWithoutConsumer(USE_READ, null)
        }
    }

    fun onQueryChange(text: String) {
        _uiState.update { it.copy(searchText = text) }

        if (text.isEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    tracks = emptyList(),
                    searchStatus = ResponseStatus.DEFAULT,
                    showHistory = currentState.historyTracks.isNotEmpty()
                )
            }
        } else {
            _uiState.update { it.copy(showHistory = false) }
        }
    }

    fun searchTracks(searchQuery: String) {
        if (searchQuery.isEmpty()) return

        _uiState.update { it.copy(searchStatus = ResponseStatus.LOADING) }

        viewModelScope.launch {
            tracksInteractor.searchTracks(searchQuery).collect { pair ->
                val tracks = pair.first.map { TrackMapper.mapToTrackUI(it) }
                _uiState.update {
                    it.copy(
                        tracks = tracks,
                        searchStatus = pair.second
                    )
                }
            }
        }
    }

    fun addTrackToHistory(track: TrackUI) {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPrefsInteractor.readWriteClear(
                use = USE_WRITE,
                track = TrackMapper.mapToTrack(track),
                consumer = object : SharedPrefsInteractor.SharedPrefsConsumer {
                    override fun consume(foundSharedPrefs: ArrayList<Track>) {
                        val history = foundSharedPrefs.map { TrackMapper.mapToTrackUI(it) }
                        _uiState.update { it.copy(historyTracks = history) }
                    }
                }
            )
        }
    }

    fun clearHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPrefsInteractor.readWriteClear(
                use = USE_CLEAR,
                track = null,
                consumer = object : SharedPrefsInteractor.SharedPrefsConsumer {
                    override fun consume(foundSharedPrefs: ArrayList<Track>) {
                        _uiState.update {
                            it.copy(
                                historyTracks = emptyList(),
                                showHistory = false
                            )
                        }
                    }
                }
            )
        }
    }

    fun onTrackClick(): Boolean {
        if (!isClickAllowed) return false
        isClickAllowed = false
        viewModelScope.launch {
            delay(CLICK_DEBOUNCE_DELAY)
            isClickAllowed = true
        }
        return true
    }

    private companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val USE_CLEAR = "clear"
        const val USE_READ = "read"
        const val USE_WRITE = "write"
    }
}
