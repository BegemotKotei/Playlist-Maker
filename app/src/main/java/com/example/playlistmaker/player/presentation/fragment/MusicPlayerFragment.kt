package com.example.playlistmaker.player.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.app.dpToPx
import com.example.playlistmaker.app.getRadiusCutImage
import com.example.playlistmaker.app.parcelable
import com.example.playlistmaker.databinding.FragmentMusicPlayerBinding
import com.example.playlistmaker.player.presentation.MusicPlayerViewModel
import com.example.playlistmaker.search.presentation.models.TrackUI
import org.koin.androidx.viewmodel.ext.android.viewModel

class MusicPlayerFragment : Fragment() {
    private var _binding: FragmentMusicPlayerBinding? = null
    private val binding get() = _binding!!
    private var url: String? = null
    private val viewModel: MusicPlayerViewModel by viewModel()

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
        initViews()
        loadTrackData()
        setupPlayer()
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

    private fun initViews() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.mbPlayMusic.setOnClickListener { viewModel.playbackControl() }
    }

    private fun loadTrackData() {
        arguments?.parcelable<TrackUI>(TRACK_KEY)?.let { track ->
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
                .transform(RoundedCorners(dpToPx(getRadiusCutImage(), requireContext())))
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

    companion object {
        private const val TRACK_KEY = "TRACK"
    }
}