package com.example.playlistmaker.media.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentLikeMusicBinding
import com.example.playlistmaker.media.presentation.viewModel.LikeMusicViewModel
import com.example.playlistmaker.search.presentation.fragment.TrackAdapter
import com.example.playlistmaker.search.presentation.models.TrackUI
import com.example.playlistmaker.ui.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LikeMusicFragment : Fragment() {

    private val viewModel: LikeMusicViewModel by viewModel()
    private var _binding: FragmentLikeMusicBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy { TrackAdapter() }
    private var debounceClick = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLikeMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        viewModel.update()
    }

    private fun setupRecyclerView() {
        binding.rwTrackLiked.adapter = adapter
        adapter.onClick = { item ->
            (activity as MainActivity).animateBottomNavigationViewFalse()
            onClickAdapter(item)
        }
    }

    private fun setupObservers() {
        viewModel.tracksLiked.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isEmpty()) {
                showEmptyState()
            } else {
                showTracks()
            }
            adapter.data = tracks
        }
    }

    private fun showEmptyState() {
        binding.rwTrackLiked.isVisible = false
        binding.tvViewHolder.isVisible = true
        binding.flViewHolder.isVisible = true
    }

    private fun showTracks() {
        binding.rwTrackLiked.isVisible = true
        binding.tvViewHolder.isVisible = false
        binding.flViewHolder.isVisible = false
    }

    private fun onClickAdapter(trackUI: TrackUI) {
        if (debounceClick) {
            debounceClick = false
            navigateToPlayer(trackUI)
        }
    }

    private fun navigateToPlayer(trackUI: TrackUI) {
        findNavController().navigate(
            R.id.action_mediaLibraryFragment_to_musicPlayerFragment,
            bundleOf(TRACK_KEY to trackUI)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        debounceClick = true
        (activity as MainActivity).animateBottomNavigationViewTrue()
    }

    companion object {
        private const val TRACK_KEY = "TRACK"

        fun newInstance() = LikeMusicFragment()
    }
}
