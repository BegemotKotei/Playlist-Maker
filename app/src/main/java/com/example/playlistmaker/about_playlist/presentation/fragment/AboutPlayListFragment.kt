package com.example.playlistmaker.about_playlist.presentation.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.about_playlist.presentation.view_model.AboutPlayListFragmentViewModel
import com.example.playlistmaker.core.parcelable
import com.example.playlistmaker.core.resourceManager.IResourceManager
import com.example.playlistmaker.core.showCustomToast
import com.example.playlistmaker.databinding.FragmentAboutPlaylistBinding
import com.example.playlistmaker.playlist_create.presentation.models.PlayListUI
import com.example.playlistmaker.search.presentation.fragment.TrackAdapter
import com.example.playlistmaker.search.presentation.models.TrackUI
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AboutPlayListFragment : Fragment() {
    private val resourceManager: IResourceManager by inject()
    private var _binding: FragmentAboutPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AboutPlayListFragmentViewModel by viewModel()
    private var playlistId: Long? = null
    private var imageUri: Uri? = null
    private val adapter by lazy { TrackAdapter() }
    private var debounceClick = true
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    lateinit var confirmDialog: MaterialAlertDialogBuilder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutPlaylistBinding.inflate(inflater, container, false)
        playlistId = arguments?.parcelable<PlayListUI>(PLAYLIST_ITEM)?.id

        if (playlistId == null) {
            Toast.makeText(requireContext(), "Ошибка: не удалось загрузить плейлист", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewBS.adapter = adapter
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetMore)
        hideBottomSheet()

        playlistId?.let {
            viewModel.update(it)
        }
        setupClickListeners()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        debounceClick = true
        playlistId?.let { viewModel.updatePlayList(it) }
    }

    private fun setupClickListeners() {
        binding.editPlayList.setOnClickListener {
            playlistId?.let { id ->
                findNavController().navigate(
                    R.id.action_aboutPlayListFragment_to_createPlayList,
                    bundleOf(
                        PLAYLIST_ITEM to PlayListUI(
                            id = id,
                            namePlayList = binding.tvNamePlaylist.text.toString(),
                            aboutPlayList = binding.tvAboutPlaylist.text.toString(),
                            roadToFileImage = imageUri.toString()
                        )
                    )
                )
            }
        }

        binding.bShare.setOnClickListener { shareTracks() }
        binding.shareButton.setOnClickListener { shareTracks() }

        binding.bDeletePlayList.setOnClickListener {
            playlistId?.let { id ->
                deletePlaylist(id, binding.tvNamePlaylist.text.toString())
            }
        }

        binding.bMore.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                stateBottomSheet(newState)
            }
            override fun onSlide(p0: View, p1: Float) {}
        })

        adapter.onClick = { track ->
            if (debounceClick) {
                debounceClick = false
                findNavController().navigate(
                    R.id.action_aboutPlayListFragment_to_musicPlayerFragment,
                    bundleOf(TRACK_KEY to track)
                )
            }
        }
        adapter.onLongClick = { openDialog(it) }
        binding.backIv.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setupObservers() {
        viewModel.playList.observe(viewLifecycleOwner) { playlist ->
            with(binding) {
                tvNamePlaylistBS.text = playlist.namePlayList
                tvNamePlaylist.text = playlist.namePlayList
                tvAboutPlaylist.text = playlist.aboutPlayList
                imageUri = playlist.roadToFileImage.toUri()
                if (playlist.roadToFileImage.isNotEmpty()) {
                    ivImageBS.setImageURI(imageUri)
                    ivImage.setImageURI(imageUri)
                } else {
                    ivImageBS.setImageResource(R.drawable.placeholder_ic)
                    ivImage.setImageResource(R.drawable.placeholder_ic)
                }
            }
        }

        viewModel.aboutPlayListState.observe(viewLifecycleOwner) {
            binding.tvCountTrack.text = it.count
            binding.tvTimeTracks.text = it.time
            binding.recyclerViewBS.isVisible = it.tracks.isNotEmpty()
            binding.flPlaceHolder.isVisible = it.tracks.isEmpty()
            adapter.data = it.tracks
            binding.tvCountTrackBS.text = it.count
        }
    }

    private fun hideBottomSheet() {
        if (::bottomSheetBehavior.isInitialized) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun shareTracks() {
        if (adapter.data.isNotEmpty()) {
            playlistId?.let { viewModel.shareTracks(it) }
        } else {
            Toast(requireContext()).showCustomToast(
                resourceManager.getStringById(R.string.not_tracks_share),
                requireActivity()
            )
        }
    }

    private fun stateBottomSheet(newState: Int) {
        binding.overlay.visibility = if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
    }

    private fun openDialog(track: TrackUI) {
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage(resourceManager.getStringById(R.string.delete_track_confirmation))
            .setNegativeButton(resourceManager.getStringById(R.string.no)) { _, _ -> }
            .setPositiveButton(resourceManager.getStringById(R.string.yes)) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.deleteTrack(track.trackId, playlistId ?: 0)
                }
            }
        confirmDialog.show()
    }

    private fun deletePlaylist(id: Long, name: String) {
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage(resourceManager.getStringById(R.string.delete_playlist_confirmation, name))
            .setNegativeButton(resourceManager.getStringById(R.string.no)) { _, _ -> }
            .setPositiveButton(resourceManager.getStringById(R.string.yes)) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.deletePlaylist(id)
                    findNavController().popBackStack()
                }
            }
        confirmDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PLAYLIST_ITEM = "Playlist"
        const val TRACK_KEY = "TRACK"
    }
}