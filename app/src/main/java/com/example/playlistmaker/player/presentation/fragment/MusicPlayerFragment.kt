package com.example.playlistmaker.player.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.core.parcelable
import com.example.playlistmaker.core.showCustomToast
import com.example.playlistmaker.databinding.FragmentMusicPlayerBinding
import com.example.playlistmaker.player.presentation.MusicPlayerViewModel
import com.example.playlistmaker.search.presentation.models.TrackUI
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MusicPlayerFragment : Fragment() {
    private var _binding: FragmentMusicPlayerBinding? = null
    private val binding get() = _binding!!
    private var url: String? = null
    private val adapter by lazy { BottomSheetPlayListAdapter() }
    private val viewModel: MusicPlayerViewModel by viewModel<MusicPlayerViewModel> {
        parametersOf(getTrackFromArgs())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val trackUI = getTrackFromArgs()
        initViews()
        loadTrackData(trackUI)
        observeSetupPlayer()
        observeIsLiked()
        setupPlaylistLogic()
    }

    override fun onResume() {
        super.onResume()
        viewModel.update()
        BottomSheetBehavior.from(binding.standardBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
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

    private fun setupPlaylistLogic() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.recyclerViewBS.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.trackAddedFlow.collect { toastState ->
                if (toastState.answer) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
                makeToast(toastState.answer, toastState.name)
            }
        }

        viewModel.playLists.observe(viewLifecycleOwner) {
            adapter.data = it
        }

        binding.bsbtNewPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(
                R.id.action_musicPlayerFragment_to_createPlayList
            )
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                stateBottomSheet(newState)
            }

            override fun onSlide(p0: View, p1: Float) {}
        })

        adapter.onClick = { playList ->
            viewModel.insertInPlayList(playList.id, playList.namePlayList)
            viewModel.update()
        }

        binding.mbPlaylistAdd.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
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
        binding.mbLikeMusic.setOnClickListener {
            viewModel.changeLikeStatus()
        }
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

    private fun observeSetupPlayer() {
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

    private fun observeIsLiked() {
        viewModel.isLiked.observe(viewLifecycleOwner) { isLiked ->
            if (isLiked) {
                binding.mbLikeMusic.setIconResource(R.drawable.like_ic_red)
                binding.mbLikeMusic.setIconTintResource(R.color.red)
            } else {
                binding.mbLikeMusic.setIconResource(R.drawable.like_ic)
                binding.mbLikeMusic.setIconTintResource(R.color.white)
            }
        }
    }

    private fun stateBottomSheet(newState: Int) {
        when (newState) {
            BottomSheetBehavior.STATE_HIDDEN -> {
                binding.overlay.visibility = View.GONE
            }

            else -> {
                binding.overlay.visibility = View.VISIBLE
            }
        }
    }

    private fun makeToast(answer: Boolean, name: String) {
        if (answer) {
            Toast(requireContext()).showCustomToast(
                getString(R.string.add_playlist_yes, name),
                requireActivity()
            )
        } else {
            Toast(requireContext()).showCustomToast(
                getString(R.string.add_playlist_no, name),
                requireActivity()
            )
        }
    }

    companion object {
        private const val TRACK_KEY = "TRACK"
    }
}