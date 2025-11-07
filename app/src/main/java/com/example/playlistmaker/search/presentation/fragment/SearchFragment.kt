package com.example.playlistmaker.search.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.ResponseStatus
import com.example.playlistmaker.search.presentation.view_model.SearchFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private val viewModel: SearchFragmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val uiState by viewModel.uiState.collectAsState()

                SearchTheme(darkTheme = isSystemInDarkTheme()) {
                    SearchScreen(
                        query = uiState.searchText,
                        onQueryChange = { viewModel.onQueryChange(it) },
                        onClearQuery = { viewModel.onQueryChange("") },
                        tracks = if (uiState.showHistory) uiState.historyTracks else uiState.tracks,
                        isHistory = uiState.showHistory,
                        onClearHistory = { viewModel.clearHistory() },
                        onTrackClick = { track ->
                            if (viewModel.onTrackClick()) {
                                viewModel.addTrackToHistory(track)
                            }
                        },
                        isLoading = uiState.searchStatus == ResponseStatus.LOADING,
                        isErrorConnection = uiState.searchStatus == ResponseStatus.ERROR,
                        isNothingFound = uiState.searchStatus == ResponseStatus.EMPTY,
                        searchFinished = uiState.searchStatus != ResponseStatus.LOADING && uiState.searchStatus != ResponseStatus.DEFAULT,
                        navigateToPlayer = { track ->
                            findNavController().navigate(
                                R.id.action_searchFragment_to_musicPlayerFragment,
                                bundleOf(TRACK_KEY to track)
                            )
                        },
                        onSearch = { query ->
                            viewModel.searchTracks(query)
                        }
                    )
                }
            }
        }
    }

    companion object {
        private const val TRACK_KEY = "TRACK"
    }
}