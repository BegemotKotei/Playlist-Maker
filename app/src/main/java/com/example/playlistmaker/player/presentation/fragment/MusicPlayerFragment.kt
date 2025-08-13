package com.example.playlistmaker.player.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.core.parcelable
import com.example.playlistmaker.databinding.FragmentMusicPlayerBinding
import com.example.playlistmaker.player.presentation.MusicPlayerViewModel
import com.example.playlistmaker.search.presentation.models.TrackUI
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MusicPlayerFragment : Fragment() {
    private var _binding: FragmentMusicPlayerBinding? = null
    private val binding get() = _binding!!
    private var url: String? = null
    private val viewModel: MusicPlayerViewModel by viewModel<MusicPlayerViewModel> {
        parametersOf(getTrackFromArgs())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicPlayerBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val trackUI = getTrackFromArgs()
        initViews()
        loadTrackData(trackUI)
        setupPlayer()
        isLiked()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.releasePlayer()
        _binding = null
    }

    private fun getTrackFromArgs(): TrackUI {
        return requireArguments().parcelable<TrackUI>(TRACK_KEY)
            ?: throw IllegalStateException("TrackUI is required")
    }

    private fun initViews() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.mbPlayMusic.setOnClickListener { viewModel.playbackControl() }
    }

    private fun loadTrackData(trackUI: TrackUI) {
        with(trackUI) {
            viewModel.getLikeStatus(trackUI.trackId)
            url = previewUrl
            binding.tvNameMusic.text = trackName
            binding.tvGroupName.text = artistName
            binding.tvTimeMusicAnswer.text = trackTimeMillis
            binding.textGroup.isVisible = !collectionName.isNullOrEmpty()
            binding.tvGroupMusicAnswer.text = collectionName
            binding.tvEarAnswer.text = releaseDate
            binding.tvTypeMusicAnswer.text = primaryGenreName
            binding.tvCountryAnswer.text = country

            val urlImage = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
            Glide.with(this@MusicPlayerFragment)
                .load(urlImage)
                .placeholder(R.drawable.placeholder_ic)
                .into(binding.ivMusicImage)
        }
    }

    private fun setupPlayer() {
        url?.let {
            viewModel.preparePlayer(it)
            viewModel.playerState.observe(viewLifecycleOwner) { state ->
                state?.let {
                    binding.tvTimeMusic30.text = state.progress
                    binding.mbPlayMusic.setIconResource(state.buttonIconRes)
                    binding.mbPlayMusic.isEnabled = state.isPlayButtonEnabled
                }
            }
        }
    }

    private fun isLiked() {
        viewModel.isLiked.observe(viewLifecycleOwner) { isLiked ->
            if (isLiked) {
                binding.mbLikeMusic.setIconResource(R.drawable.like_ic_red)
                binding.mbLikeMusic.setIconTintResource(R.color.red)
            } else {
                binding.mbLikeMusic.setIconResource(R.drawable.like_ic)
                binding.mbLikeMusic.setIconTintResource(R.color.white)
            }
        }
        binding.mbLikeMusic.setOnClickListener {
            viewModel.changeLikeStatus()
        }
    }

    companion object {
        private const val TRACK_KEY = "TRACK"
    }
}