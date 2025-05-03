package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityMusicPlayerBinding
import com.google.android.material.button.MaterialButton
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class MusicPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMusicPlayerBinding
    private val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    private val mainThreadHandler by lazy { Handler(Looper.getMainLooper()) }
    private var mediaPlayer = MediaPlayer()
    private var playerState = PlayerState.DEFAULT
    private var url: String? = null
    private lateinit var playButton: MaterialButton
    private val timeMusic30 by lazy { binding.tvTimeMusic30 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playButton = binding.mbPlayMusic

        binding.ivBack.setOnClickListener { finish() }

        intent.parcelable<Track>(getTrackKey())?.let { track ->
            url = track.previewUrl
            displayTrackInfo(track)
            loadTrackImage(track)
        } ?: run {
            Log.i(
                "MusicPlayerActivity",
                "Intent does not contain a track"
            )
        }

        setupIntents()

        preparePlayer()
        playButton.setOnClickListener {
            startTimerMusic()
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        mediaPlayer.release()
        mainThreadHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun displayTrackInfo(track: Track) {
        try {
            binding.tvNameMusic.text = track.trackName
            binding.tvGroupName.text = track.artistName
            binding.tvTimeMusicAnswer.text = dateFormat.format(track.trackTimeMillis.toLong())
            if (track.collectionName == null) {
                binding.textGroup.isVisible = false
            } else {
                binding.tvGroupMusicAnswer.text = track.collectionName
            }
            binding.tvEarAnswer.text = getFormattedDate(track.releaseDate)
            binding.tvTypeMusicAnswer.text = track.primaryGenreName
            binding.tvCountryAnswer.text = track.country
            Log.i(
                "MusicPlayerActivity",
                track.toString()
            )
        } catch (e: Exception) {
            Log.i(
                "MusicPlayerActivity",
                e.toString()
            )
        }
    }

    private fun loadTrackImage(track: Track) {
        val urlImage = track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

        Glide
            .with(this)
            .load(urlImage)
            .placeholder(R.drawable.placeholder_ic)
            .centerInside()
            .transform(RoundedCorners(dpToPx(getRadiusCutImage(), this)))
            .into(binding.ivMusicImage)

    }

    private fun getFormattedDate(inputDate: String?): String {
        if (inputDate.isNullOrEmpty()) {
            Log.i(
                "MusicPlayerActivity", "Input date is null or empty"
            )
            return ""
        }

        val inputFormat: SimpleDateFormat by lazy {
            SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()
            )
        }

        val outputFormat: SimpleDateFormat by lazy {
            SimpleDateFormat(
                "yyyy", Locale.getDefault()
            )
        }

        return try {
            inputFormat.parse(inputDate)?.let { date ->
                outputFormat.format(date).orEmpty()
            } ?: run {
                Log.i(
                    "MusicPlayerActivity", "Failed to parse date: $inputDate"
                )
                ""
            }
        } catch (e: ParseException) {
            Log.i(
                "MusicPlayerActivity",
                "ParseException while parsing date $inputDate, error: ${e.message}"
            )
            ""
        } catch (e: Exception) {
            Log.i(
                "MusicPlayerActivity",
                "Unexpected error while processing date: $inputDate, error: ${e.message}"
            )
            ""
        }
    }

    private fun preparePlayer() {
        if (url.isNullOrBlank()) {
            Log.e(
                "MusicPlayerActivity",
                "URL is null or empty"
            )
            return
        }

        try {
            mediaPlayer.reset() // Сбросить перед повторным использованием
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerState = PlayerState.PREPARED
                updatePlayButtonState()
                Log.i(
                    "MusicPlayerActivity",
                    "MediaPlayer prepared, duration: ${mediaPlayer.duration}"
                )
            }
            mediaPlayer.setOnCompletionListener {
                playerState = PlayerState.PREPARED
                updatePlayButtonState()
                mainThreadHandler.removeCallbacksAndMessages(null)
                timeMusic30.text = TIME_START
            }
            mediaPlayer.setOnErrorListener { _, what, extra ->
                Log.e(
                    "MusicPlayerActivity",
                    "Error occurred: what=$what, extra=$extra"
                )
                playerState = PlayerState.DEFAULT
                updatePlayButtonState()
                false
            }
        } catch (e: IOException) {
            Log.e(
                "MusicPlayerActivity",
                "Error setting data source",
                e
            )
            playerState = PlayerState.DEFAULT
            updatePlayButtonState()
        }
    }

    private fun updatePlayButtonState() {
        when (playerState) {
            PlayerState.PLAYING -> playButton.setIconResource(R.drawable.pause_ic)
            PlayerState.PREPARED, PlayerState.PAUSED -> playButton.setIconResource(R.drawable.ic_play)
            else -> playButton.setIconResource(R.drawable.ic_play)
        }
    }

    private fun startTimerMusic() {
        if (playerState == PlayerState.PREPARED || playerState == PlayerState.PAUSED) {
            mainThreadHandler.post(createUpdateTimerMusicTask())
        } else {
            mainThreadHandler.removeCallbacksAndMessages(null)
        }
    }

    private fun createUpdateTimerMusicTask(): Runnable {
        return object : Runnable {
            override fun run() {
                val timeMusicAnswer = mediaPlayer.currentPosition
                if (timeMusicAnswer < MUSIC_TIME) {
                    timeMusic30.text = dateFormat.format(timeMusicAnswer.toLong()).toString()
                    mainThreadHandler.postDelayed(this, DELAY)
                } else {
                    timeMusic30.text = TIME_START
                    mainThreadHandler.postDelayed(this, DELAY)
                }
            }
        }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setIconResource(R.drawable.ic_play)
        playerState = PlayerState.PAUSED
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setIconResource(R.drawable.pause_ic)
        playerState = PlayerState.PLAYING
    }

    private fun playbackControl() {
        when (playerState) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }

            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer()
            }

            else -> Unit
        }
    }

    private fun setupIntents() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            v.setPadding(
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom,
            )
            insets
        }
    }

    companion object {
        private const val TRACK_KEY = "TRACK"
        private const val DELAY = 300L
        private const val MUSIC_TIME = 29900
        private const val TIME_START = "00:00"

        fun getTrackKey(): String {
            return TRACK_KEY
        }
    }

    private enum class PlayerState {
        DEFAULT, PREPARED, PLAYING, PAUSED
    }
}