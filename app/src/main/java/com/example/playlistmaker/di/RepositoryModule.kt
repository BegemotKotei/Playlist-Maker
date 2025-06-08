package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.MediaPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.MediaPlayerRepository
import com.example.playlistmaker.search.data.SharedPrefsRepositoryImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.sharedpref.SharedPrefsRepository
import com.example.playlistmaker.settings.data.ExternalNavigatorImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.ExternalNavigator
import com.example.playlistmaker.settings.domain.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<SettingsRepository> {
        SettingsRepositoryImpl(context = androidContext())
    }
    single<SharedPrefsRepository> {
        SharedPrefsRepositoryImpl(storage = get())
    }
    factory<ExternalNavigator> {
        ExternalNavigatorImpl(context = androidContext())
    }
    single<TracksRepository> {
        TracksRepositoryImpl(networkClient = get())
    }
    single<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(mediaPlayer = get())
    }
}