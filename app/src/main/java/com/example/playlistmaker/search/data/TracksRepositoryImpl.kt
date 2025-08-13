package com.example.playlistmaker.search.data

import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.ResponseStatus
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TrackResults
import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase
) : TracksRepository {

    override fun searchTracks(expression: String): Flow<TrackResults> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode == 200) {
            val likedTracksId = appDatabase.trackDao().getTracksIdList()
            emit(
                TrackResults(
                    status = if ((response as TrackResponse).results.isEmpty()) {
                        ResponseStatus.EMPTY
                    } else {
                        ResponseStatus.SUCCESS
                    },
                    data = response.results.map {
                        Track(
                            trackName = it.trackName,
                            artistName = it.artistName,
                            trackTimeMillis = getFormattedTime(it.trackTimeMillis),
                            artworkUrl100 = it.artworkUrl100,
                            trackId = it.trackId,
                            collectionName = it.collectionName,
                            releaseDate = getFormattedDate(it.releaseDate),
                            primaryGenreName = it.primaryGenreName,
                            country = it.country,
                            previewUrl = it.previewUrl,
                            isLiked = isFavoriteBoolean(it.trackId, likedTracksId)
                        )
                    }
                )
            )
        } else {
            emit(
                TrackResults(
                    status = ResponseStatus.ERROR
                )
            )
        }
    }

    private fun isFavoriteBoolean(trackId: String, tracksId: List<String>) =
        tracksId.find { likedTrack -> likedTrack == trackId } != ""

    private fun getFormattedDate(inputDate: String?): String {
        inputDate ?: return ""

        val dfIn = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val dfOut = SimpleDateFormat("yyyy", Locale.getDefault())

        dfIn.parse(inputDate)?.let { date ->
            return dfOut.format(date).orEmpty()
        } ?: return ""
    }

    private fun getFormattedTime(inputDate: String?): String {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        val dfOut = dateFormat.format(inputDate?.toLong())
        return dfOut
    }
}