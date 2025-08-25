package com.example.playlistmaker.di

import com.example.playlistmaker.db.data.LikeTrackRepositoryImpl
import com.example.playlistmaker.db.data.PlayListRepositoryImpl
import com.example.playlistmaker.db.domain.LikeTrackRepository
import com.example.playlistmaker.db.domain.PlayListRepository
import com.example.playlistmaker.db.mappers.PlayListDbMapper
import com.example.playlistmaker.db.mappers.TrackDbMapper
import com.example.playlistmaker.player.data.MediaPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.MediaPlayerRepository
import com.example.playlistmaker.playlist_create.data.SaveImageToMemoryRepositoryImpl
import com.example.playlistmaker.playlist_create.domain.SaveImageToMemoryRepository
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
        SharedPrefsRepositoryImpl(storage = get(), trackDao = get())
    }
    factory<ExternalNavigator> {
        ExternalNavigatorImpl(context = androidContext())
    }
    single<TracksRepository> {
        TracksRepositoryImpl(networkClient = get(), trackDao = get())
    }
    factory<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl()
    }
    single<LikeTrackRepository> {
        LikeTrackRepositoryImpl(trackDao = get(), trackDbMapper = get())
    }
    single<SaveImageToMemoryRepository> {
        SaveImageToMemoryRepositoryImpl(saveImageToMemory = get())
    }
    single<PlayListRepository> {
        PlayListRepositoryImpl(playListDao = get(), playListDbMapper = get())
    }
    factory { PlayListDbMapper() }

    factory { TrackDbMapper() }
}