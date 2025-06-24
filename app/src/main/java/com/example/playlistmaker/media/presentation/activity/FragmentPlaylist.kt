package com.example.playlistmaker.media.presentation.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.media.presentation.viewModel.PlayListViewModel

class FragmentPlaylist : Fragment() {

    private val viewModel: PlayListViewModel by viewModels<PlayListViewModel>()
    private lateinit var binding: FragmentPlaylistBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        private const val PLAYLIST_NUMBER = "playlist_number"

        fun newInstance() = FragmentPlaylist().apply {
            arguments = Bundle().apply {
                putInt(PLAYLIST_NUMBER, 1)
            }
        }
    }
}