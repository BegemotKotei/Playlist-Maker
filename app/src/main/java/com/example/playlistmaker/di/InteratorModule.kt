package com.example.playlistmaker.di

import com.example.playlistmaker.db.domain.LikeTrackInteractor
import com.example.playlistmaker.db.domain.PlayListInteractor
import com.example.playlistmaker.db.domain.imlp.LikeTrackInteractorImpl
import com.example.playlistmaker.db.domain.imlp.PlayListInteractorImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.playlist_create.domain.SaveImageToMemoryInteractor
import com.example.playlistmaker.playlist_create.domain.impls.SaveImageToMemoryInteractorImpl
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.SharedPrefsInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.search.domain.sharedpref.SharedPrefsInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SharingInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    factory<SettingsInteractor> {
        SettingsInteractorImpl(repository = get())
    }
    factory<SharedPrefsInteractor> {
        SharedPrefsInteractorImpl(repository = get())
    }
    factory<SharingInteractor> {
        SharingInteractorImpl(externalNavigator = get(), settingsRepository = get())
    }
    factory<TracksInteractor> {
        TracksInteractorImpl(repository = get())
    }
    factory<PlayerInteractor> {
        PlayerInteractorImpl(repository = get())
    }
    factory<LikeTrackInteractor> {
        LikeTrackInteractorImpl(repository = get())
    }
    factory<PlayListInteractor> {
        PlayListInteractorImpl(repository = get())
    }
    factory<SaveImageToMemoryInteractor> {
        SaveImageToMemoryInteractorImpl(repository = get())
    }
}