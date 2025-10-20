package com.example.playlistmaker.player.presentation.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
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
import com.example.playlistmaker.core.resourceManager.IResourceManager
import com.example.playlistmaker.core.showCustomToast
import com.example.playlistmaker.databinding.FragmentMusicPlayerBinding
import com.example.playlistmaker.player.presentation.MusicPlayerViewModel
import com.example.playlistmaker.player.presentation.PlayerState
import com.example.playlistmaker.player.presentation.service.AudioPlayerService
import com.example.playlistmaker.player.presentation.service.AudioPlayerServiceImpl
import com.example.playlistmaker.search.presentation.models.TrackUI
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MusicPlayerFragment : Fragment() {
    private var _binding: FragmentMusicPlayerBinding? = null
    private val binding get() = _binding!!
    private val resourceManager: IResourceManager by inject()
    private lateinit var adapter: BottomSheetPlayListAdapter
    private val viewModel: MusicPlayerViewModel by viewModel<MusicPlayerViewModel> {
        parametersOf(getTrackFromArgs())
    }
    private var audioService: AudioPlayerService? = null
    private var serviceConnection: ServiceConnection? = null
    private var isBound = false

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

        adapter = BottomSheetPlayListAdapter(resourceManager)
        val trackUI = getTrackFromArgs()

        initViews()
        loadTrackData(trackUI)
        observePlayerState()
        observeIsLiked()
        setupPlaylistLogic()
        bindServiceToAudioPlayer(trackUI)
    }

    private fun createServiceConnection(): ServiceConnection {
        return object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                if (service is AudioPlayerServiceImpl.AudioPlayerBinder) {
                    audioService = service.getService()
                    isBound = true

                    val track = getTrackFromArgs()
                    audioService?.preparePlayer(track)

                    observeService()
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                audioService = null
                isBound = false
            }
        }.also { serviceConnection = it }
    }

    private fun bindServiceToAudioPlayer(track: TrackUI) {
        val intent = Intent(requireContext(), AudioPlayerServiceImpl::class.java).apply {
            putExtra(TRACK_KEY, track)
        }

        requireContext().bindService(intent, createServiceConnection(), Context.BIND_AUTO_CREATE)
    }

    private var isObservingService = false

    private fun observeService() {
        if (isObservingService) return
        isObservingService = true

        lifecycleScope.launch {
            audioService?.getIsPlaying()?.collect { isPlaying ->
                viewModel.updatePlaybackState(isPlaying)
            }
        }

        lifecycleScope.launch {
            audioService?.getCurrentTime()?.collect { time ->
                binding.tvTimeMusic30.text = time
            }
        }
    }

    override fun onResume() {
        super.onResume()
        audioService?.hideNotification()
        viewModel.update()
    }

    override fun onPause() {
        super.onPause()
        if (audioService?.getIsPlaying()?.value == true) {
            audioService?.showNotificationIfPlaying()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioService?.stop()
        if (isBound && serviceConnection != null) {
            requireContext().unbindService(serviceConnection!!)
            isBound = false
        }
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        audioService?.stop()
        _binding = null
    }

    private fun initViews() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.playButton.setOnClickListener {
            audioService?.togglePlayback()
        }
        binding.mbLikeMusic.setOnClickListener {
            viewModel.changeLikeStatus()
        }
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

    private fun loadTrackData(trackUI: TrackUI) {
        with(trackUI) {
            viewModel.getLikeStatus(trackUI.trackId)
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

    private fun observePlayerState() {
        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            state?.let {
                updatePlayBackButtonState(state)
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

    private fun updatePlayBackButtonState(state: PlayerState) {
        when (state) {
            is PlayerState.Default -> {
                binding.playButton.isEnabled = false
                binding.playButton.setPlaying(false)
            }

            is PlayerState.Prepared -> {
                binding.playButton.isEnabled = true
                binding.playButton.setPlaying(false)
            }

            is PlayerState.Playing -> {
                binding.playButton.isEnabled = true
                binding.playButton.setPlaying(true)
            }

            is PlayerState.Paused -> {
                binding.playButton.isEnabled = true
                binding.playButton.setPlaying(false)
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