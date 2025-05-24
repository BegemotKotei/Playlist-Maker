package com.example.playlistmaker.presentation.player

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMusicPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.dpToPx
import com.example.playlistmaker.presentation.getRadiusCutImage
import com.example.playlistmaker.presentation.parcelable
import com.google.android.material.button.MaterialButton

class MusicPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMusicPlayerBinding
    private var playerState = PlayerState.DEFAULT
    private var mediaPlayer = MediaPlayer()
    private var url: String? = " "
    private lateinit var playButton: MaterialButton
    private val timeMusic30 by lazy { binding.tvTimeMusic30 }


    private val viewModel by viewModels<MusicPlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playButton = binding.mbPlayMusic
        binding.ivBack.setOnClickListener { finish() }

        intent.parcelable<Track>(getTrackKey())?.let { track ->
            url = track.previewUrl
            binding.tvNameMusic.text = track.trackName
            binding.tvGroupName.text = track.artistName
            binding.tvTimeMusicAnswer.text = track.trackTimeMillis

            if(track.collectionName == null) {
                binding.textGroup.isVisible = false
            } else {
                binding.tvGroupName.text = track.collectionName
            }
            binding.tvEarAnswer.text = track.releaseDate
            binding.tvTypeMusicAnswer.text = track.primaryGenreName
            binding.tvCountryAnswer.text = track.country

            loadTrackImage(track)
        }

        preparePlayer()

        playButton.setOnClickListener {
            viewModel.setPlayerPosition(mediaPlayer.currentPosition)
            viewModel.startTimerMusic(playerState)
            playbackControl()
        }

        viewModel.timerLiveData.observe(this) {
            timeMusic30.text = it
            viewModel.setPlayerPosition(mediaPlayer.currentPosition)
        }

        setupIntents()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }

    private fun loadTrackImage(track: Track) {
        val urlImage = track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(this)
            .load(urlImage)
            .placeholder(R.drawable.placeholder_ic)
            .centerInside()
            .transform(RoundedCorners(dpToPx(getRadiusCutImage(), this)))
            .into(binding.ivMusicImage)
    }

    @SuppressLint("SetTextI18n")
    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setIconResource(R.drawable.ic_play)
            playerState = PlayerState.PREPARED
        }
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
    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setIconResource(R.drawable.pause_ic)
        playerState = PlayerState.PLAYING
    }
    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setIconResource(R.drawable.ic_play)
        playerState = PlayerState.PAUSED
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

        fun getTrackKey(): String = TRACK_KEY
    }
}