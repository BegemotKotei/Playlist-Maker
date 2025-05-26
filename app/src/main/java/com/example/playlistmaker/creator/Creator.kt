package com.example.playlistmaker.creator

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.player.data.MediaPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.search.data.SharedPrefsRepositoryImpl
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitConfig
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.sharedstorage.SearchHistoryStorageImpl
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.SharedPrefsInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.search.domain.sharedpref.SharedPrefsInteractor
import com.example.playlistmaker.search.domain.sharedpref.SharedPrefsRepository
import com.example.playlistmaker.settings.data.ExternalNavigatorImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.ExternalNavigator
import com.example.playlistmaker.settings.domain.SettingsRepository

object Creator {

    private fun getTracksRepository(): TracksRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(RetrofitConfig().provideRetrofit()))
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getSharedPrefsRepository(context: Context): SharedPrefsRepository {
        return SharedPrefsRepositoryImpl(SearchHistoryStorageImpl(context))
    }

    fun provideSharedPrefsInteractor(context: Context): SharedPrefsInteractor {
        return SharedPrefsInteractorImpl(getSharedPrefsRepository(context))
    }

    fun provideSettingsRepository(context: Context): SettingsRepository =
        SettingsRepositoryImpl(context)

    fun provideExternalNavigator(context: Context): ExternalNavigator =
        ExternalNavigatorImpl(context)

    fun providePlayerInteractor(): PlayerInteractorImpl {
        return PlayerInteractorImpl(MediaPlayerRepositoryImpl(MediaPlayer()))
    }
}