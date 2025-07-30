package com.example.playlistmaker.di

import com.example.playlistmaker.media.presentation.viewModel.MediaLibraryViewModel
import com.example.playlistmaker.player.presentation.MusicPlayerViewModel
import com.example.playlistmaker.search.presentation.stateHolders.SearchFragmentViewModel
import com.example.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel<MusicPlayerViewModel> {
        MusicPlayerViewModel(playerInteractor = get())
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
        MediaLibraryViewModel(get())
    }
}