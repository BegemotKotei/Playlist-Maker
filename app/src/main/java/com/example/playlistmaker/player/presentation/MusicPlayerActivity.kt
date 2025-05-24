package com.example.playlistmaker.player.presentation

import android.annotation.SuppressLint
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
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.dpToPx
import com.example.playlistmaker.getRadiusCutImage
import com.example.playlistmaker.parcelable

class MusicPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMusicPlayerBinding
    private var url: String? = " "
    private val viewModel by viewModels<MusicPlayerViewModel>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            finish()
        }

        intent.parcelable<Track>(TRACK_KEY)?.let { track ->
            url = track.previewUrl
            binding.tvNameMusic.text = track.trackName
            binding.tvGroupName.text = track.artistName
            binding.tvTimeMusicAnswer.text = track.trackTimeMillis
            if (track.collectionName == null) {
                binding.textGroup.isVisible = false
            } else {
                binding.tvGroupMusicAnswer.text = track.collectionName
            }
            binding.tvEarAnswer.text = track.releaseDate
            binding.tvTypeMusicAnswer.text = track.primaryGenreName
            binding.tvCountryAnswer.text = track.country
            val urlImage = track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

            Glide.with(this)
                .load(urlImage)
                .placeholder(R.drawable.placeholder_ic)
                .centerInside()
                .transform(RoundedCorners(dpToPx(getRadiusCutImage(), this)))
                .into(binding.ivMusicImage)

        }

        viewModel.preparePlayer(url.toString())


        binding.mbPlayMusic.setOnClickListener {
            viewModel.setPlayerPosition()
            viewModel.startTimerMusic()
            viewModel.playbackControl()
        }

        viewModel.timerLiveData.observe(this) {
            binding.tvTimeMusic30.text = it
            viewModel.setPlayerPosition()
        }

        viewModel.playerState.observe(this) {
            if (it == PlayerState.PREPARED || it == PlayerState.PAUSED) {
                binding.mbPlayMusic.setIconResource(R.drawable.pause_ic)
            } else {
                binding.mbPlayMusic.setIconResource(R.drawable.ic_play)
            }
        }
        setupInsets()
    }

    override fun onPause() {
        viewModel.pausePlayer()
        super.onPause()
    }

    override fun onDestroy() {
        viewModel.mediaPlayerRelease()
        super.onDestroy()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBar.left,
                systemBar.top,
                systemBar.right,
                systemBar.bottom
            )
            insets
        }
    }

    companion object {
        private const val TRACK_KEY = "TRACK"
        fun getTrackKey(): String = TRACK_KEY
    }
}