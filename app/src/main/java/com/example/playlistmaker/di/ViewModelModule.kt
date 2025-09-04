package com.example.playlistmaker.di

import com.example.playlistmaker.about_playlist.presentation.view_model.AboutPlayListFragmentViewModel
import com.example.playlistmaker.core.resourceManager.IResourceManager
import com.example.playlistmaker.core.resourceManager.ResourceManager
import com.example.playlistmaker.media.presentation.viewModel.LikeMusicViewModel
import com.example.playlistmaker.media.presentation.viewModel.MediaLibraryViewModel
import com.example.playlistmaker.media.presentation.viewModel.PlayListViewModel
import com.example.playlistmaker.player.presentation.MusicPlayerViewModel
import com.example.playlistmaker.playlist_create.presentation.CreatePlayListFragmentViewModel
import com.example.playlistmaker.search.presentation.models.TrackUI
import com.example.playlistmaker.search.presentation.stateHolders.SearchFragmentViewModel
import com.example.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel<MusicPlayerViewModel> { (trackUI: TrackUI) ->
        MusicPlayerViewModel(
            playerInteractor = get(),
            likeTrackInteractor = get(),
            playListInteractor = get(),
            trackUI = trackUI
        )
    }
    viewModel<SearchFragmentViewModel> {
        SearchFragmentViewModel(
            sharedPrefsInteractor = get(),
            tracksInteractor = get()
        )
    }
    viewModel<SettingsViewModel> {
        SettingsViewModel(
            sharingInteractor = get(),
            settingsInteractor = get()
        )
    }
    viewModel<MediaLibraryViewModel> {
        MediaLibraryViewModel(settingsInteractor = get())
    }
    viewModel<LikeMusicViewModel> {
        LikeMusicViewModel(likeTrackInteractor = get())
    }
    viewModel<PlayListViewModel> {
        PlayListViewModel(playlistInteractor = get())
    }
    viewModel<CreatePlayListFragmentViewModel> {
        CreatePlayListFragmentViewModel(saveImageInteractor = get(), playListInteractor = get())
    }
    viewModel<AboutPlayListFragmentViewModel> {
        AboutPlayListFragmentViewModel(playListInteractor = get(), resourceManager = get())
    }
    factory<IResourceManager> {
        ResourceManager(context = get())
    }
}