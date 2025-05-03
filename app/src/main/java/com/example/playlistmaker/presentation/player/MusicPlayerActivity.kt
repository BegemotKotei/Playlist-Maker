package com.example.playlistmaker.presentation.player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.databinding.ActivityMusicPlayerBinding
import com.example.playlistmaker.presentation.dpToPx
import com.example.playlistmaker.presentation.getRadiusCutImage
import com.example.playlistmaker.presentation.parcelable
import com.google.android.material.button.MaterialButton

class MusicPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMusicPlayerBinding
    private val viewModel: MusicPlayerViewModel by viewModels()
    private val mainThreadHandler by lazy { Handler(Looper.getMainLooper()) }
    private lateinit var playButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playButton = binding.mbPlayMusic
        binding.ivBack.setOnClickListener { finish() }

        intent.parcelable<Track>(getTrackKey())?.let { track ->
            viewModel.initMediaPlayer(
                track.previewUrl,
                onPrepared = { updatePlayButtonState() },
                onCompletion = {
                    updatePlayButtonState()
                    mainThreadHandler.removeCallbacksAndMessages(null)
                    binding.tvTimeMusic30.text = TIME_START
                }
            )
            displayTrackInfo(track)
            loadTrackImage(track)
        }

        setupIntents()
        playButton.setOnClickListener {
            viewModel.playbackControl()
            updatePlayButtonState()
            startTimerMusic()
        }
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.isPlaying()) {
            viewModel.playbackControl()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.release()
        mainThreadHandler.removeCallbacksAndMessages(null)
    }

    private fun displayTrackInfo(track: Track) {
        binding.tvNameMusic.text = track.trackName
        binding.tvGroupName.text = track.artistName
        binding.tvTimeMusicAnswer.text = track.trackTimeMillis
        binding.textGroup.isVisible = track.collectionName != null
        binding.tvGroupMusicAnswer.text = track.collectionName
        binding.tvEarAnswer.text = viewModel.getFormattedDate(track.releaseDate)
        binding.tvTypeMusicAnswer.text = track.primaryGenreName
        binding.tvCountryAnswer.text = track.country
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

    private fun updatePlayButtonState() {
        playButton.setIconResource(
            if (viewModel.isPlaying()) R.drawable.pause_ic else R.drawable.ic_play
        )
    }

    private fun startTimerMusic() {
        if (!viewModel.isPlaying()) return

        mainThreadHandler.post(object : Runnable {
            override fun run() {
                binding.tvTimeMusic30.text = viewModel.getCurrentPosition()
                if (viewModel.isPlaying()) {
                    mainThreadHandler.postDelayed(this, DELAY)
                }
            }
        })
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
        private const val TIME_START = "00:00"

        fun getTrackKey(): String = TRACK_KEY
    }
}