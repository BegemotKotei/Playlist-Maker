package com.example.playlistmaker.db.data

import android.content.Context
import android.content.Intent
import com.example.playlistmaker.R
import com.example.playlistmaker.db.dao.PlayListDao
import com.example.playlistmaker.db.domain.PlayListRepository
import com.example.playlistmaker.db.entity.PlayListEntity
import com.example.playlistmaker.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.db.mappers.PlayListDbMapper
import com.example.playlistmaker.playlist_create.domain.models.PlayList
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale

class PlayListRepositoryImpl(
    private val playListDao: PlayListDao,
    private val playListDbMapper: PlayListDbMapper,
    private val context: Context
) : PlayListRepository {
    override suspend fun newPlayList(playList: PlayList) =
        playListDao.createPlaylist(playListDbMapper.mapToPlayListEntity(playList))

    override fun listPlayList(): Flow<List<PlayList>> = flow {
        emit(mapIntoPlayList(playListDao.getPlaylists()))
    }

    override suspend fun getCountTracks(id: Long): Int {
        return playListDao.getTracksCount(id)
    }

    override fun listTrackPlaylist(id: Long): Flow<List<Track>> = flow {
        emit(playListDbMapper.mapToListTrack(playListDao.tracksInPlayList(id)))
    }

    override suspend fun getTimesTracks(id: Long): String {
        var timeInMillis: Long = 0
        playListDao.tracksInPlayList(id).forEach { track ->
            timeInMillis += SimpleDateFormat("mm:ss").parse(track.trackTimeMillis)?.time ?: 0
        }
        return SimpleDateFormat("mm", Locale.getDefault()).format(timeInMillis).toString()
    }

    override suspend fun deleteTrack(id: Long) {
        playListDao.deleteTrack(id)
    }

    override suspend fun deletePlaylist(id: Long) {
        playListDao.deletePlaylistTracks(id)
        playListDao.deletePlayList(id)
    }

    override suspend fun shareTracks(id: Long) {
        createIntent(getStringIntent(id))
    }

    override suspend fun editPlayList(playList: PlayList) {
        playListDao.createPlaylist(
            PlayListEntity(
                playList.id,
                playList.namePlayList,
                playList.aboutPlayList,
                playList.roadToFileImage
            )
        )
    }

    override suspend fun getPlayList(id: Long): PlayList {
        return playListDbMapper.mapToPlayList(playListDao.getPlayList(id))
    }

    override suspend fun updatePlayList(track: Track, id: Long): Boolean {
        val trackAlreadyExists =
            playListDao.findTrack(trackId = track.trackId, playlistId = id).isNotEmpty()
        if (!trackAlreadyExists) {
            playListDao.addTracksToPlaylist(
                PlaylistTrackEntity(
                    trackId = track.trackId,
                    playlistId = id,
                    trackName = track.trackName,
                    artistName = track.artistName,
                    trackTimeMillis = track.trackTimeMillis,
                    artworkUrl100 = track.artworkUrl100,
                    collectionName = track.collectionName,
                    releaseDate = track.releaseDate,
                    primaryGenreName = track.primaryGenreName,
                    country = track.country,
                    previewUrl = track.previewUrl
                )
            )
        }
        return !trackAlreadyExists
    }

    private fun mapIntoPlayList(playList: List<PlayListEntity>): List<PlayList> {
        return playList.map { playListEntity -> playListDbMapper.mapToPlayList(playListEntity) }
    }

    private fun createIntent(textMessage: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textMessage)
            type = "text/plain"
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private suspend fun getStringIntent(id: Long): String {
        val playList = playListDao.getPlayList(id)
        var textMessage = context.getString(
            R.string.tracks_share_intent,
            playList.namePlayList,
            playList.aboutPlayList,
            playListDao.getTracksCount(id).toString()
        )
        val base = playListDao.tracksInPlayList(id).reversed()
        base.forEachIndexed { index, playlistTrackEntity ->
            textMessage += "«${index + 1}. ${playlistTrackEntity.artistName} - ${playlistTrackEntity.trackName} (${playlistTrackEntity.trackTimeMillis})».\n"
        }
        return textMessage
    }
}