package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.sharedpref.SharedPrefsInteractor
import com.example.playlistmaker.search.domain.sharedpref.SharedPrefsRepository

class SharedPrefsInteractorImpl(
    private val repository: SharedPrefsRepository
) : SharedPrefsInteractor {
    override fun readWriteClear(
        use: String,
        track: Track?,
        consumer: SharedPrefsInteractor.SharedPrefsConsumer
    ) {
        consumer.consume(repository.saveReadClear(use, track))
    }

    override fun readWriteClearWithoutConsumer(use: String, track: Track?): List<Track> {
        return repository.saveReadClear(use, track)
    }
}