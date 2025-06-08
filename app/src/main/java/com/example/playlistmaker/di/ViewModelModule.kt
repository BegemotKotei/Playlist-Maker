package com.example.playlistmaker.di

import com.example.playlistmaker.main.presentation.MainViewModel
import com.example.playlistmaker.player.presentation.MusicPlayerViewModel
import com.example.playlistmaker.search.presentation.stateHolders.SearchActivityViewModel
import com.example.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel<MainViewModel> {
        MainViewModel(
            settingsInteractor = get()
        )
    }
    viewModel<MusicPlayerViewModel> {
        MusicPlayerViewModel(playerInteractor = get())
    }
    viewModel<SearchActivityViewModel> {
        SearchActivityViewModel(
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
}