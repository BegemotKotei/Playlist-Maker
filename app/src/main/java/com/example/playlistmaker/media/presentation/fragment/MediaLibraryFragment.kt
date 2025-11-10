package com.example.playlistmaker.media.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.about_playlist.presentation.fragment.AboutPlayListFragment
import com.example.playlistmaker.media.presentation.viewModel.MediaLibraryViewModel
import com.example.playlistmaker.playlist_create.presentation.models.PlayListUI
import com.example.playlistmaker.search.presentation.models.TrackUI
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryFragment : Fragment() {
    private val viewModel: MediaLibraryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val navController = findNavController()

        return ComposeView(requireContext()).apply {
            setContent {
                var pendingTrackToPlay by remember { mutableStateOf<TrackUI?>(null) }
                var pendingCreatePlaylist by remember { mutableStateOf(false) }
                var pendingPlaylistToOpen by remember { mutableStateOf<PlayListUI?>(null) }

                LaunchedEffect(pendingTrackToPlay) {
                    pendingTrackToPlay?.let { track ->
                        val bundle = Bundle().apply {
                            putParcelable("TRACK", track)
                            putBoolean("IS_FAVORITE", track.isLiked)
                        }
                        navController.navigate(
                            R.id.action_mediaLibraryFragment_to_musicPlayerFragment,
                            bundle
                        )
                        pendingTrackToPlay = null
                    }
                }

                LaunchedEffect(pendingCreatePlaylist) {
                    if (pendingCreatePlaylist) {
                        navController.navigate(R.id.action_mediaLibraryFragment_to_createPlayList)
                        pendingCreatePlaylist = false
                    }
                }

                LaunchedEffect(pendingPlaylistToOpen) {
                    pendingPlaylistToOpen?.let { playlist ->
                        val bundle = bundleOf(AboutPlayListFragment.PLAYLIST_ITEM to playlist)
                        navController.navigate(
                            R.id.action_mediaLibraryFragment_to_aboutPlayListFragment,
                            bundle
                        )
                        pendingPlaylistToOpen = null
                    }
                }

                MediaLibraryTheme(darkTheme = isSystemInDarkTheme()) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        LibraryScreen(
                            viewModel = viewModel,
                            onTrackClick = { track -> pendingTrackToPlay = track },
                            onCreatePlaylistClicked = { pendingCreatePlaylist = true },
                            onPlaylistClicked = { playlist -> pendingPlaylistToOpen = playlist }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavoriteTracks()
        viewModel.getPlaylists()
    }
}