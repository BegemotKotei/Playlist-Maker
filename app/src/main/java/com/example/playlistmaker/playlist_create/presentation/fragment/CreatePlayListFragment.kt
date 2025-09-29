package com.example.playlistmaker.playlist_create.presentation.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.core.parcelable
import com.example.playlistmaker.core.showCustomToast
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.playlist_create.presentation.CreatePlayListFragmentViewModel
import com.example.playlistmaker.playlist_create.presentation.models.PlayListUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlayListFragment : Fragment() {
    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreatePlayListFragmentViewModel by viewModel()
    lateinit var confirmDialog: MaterialAlertDialogBuilder

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        if (arguments != null) {

            itemUse()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pickVisualMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    imageUri = uri
                    binding.imageInsert.setImageURI(uri)
                }
            }
        binding.imageInsert.setOnClickListener {
            pickVisualMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        if (arguments == null) {
            binding.bCreate.isEnabled = false
            binding.etNamePlayList.isActivated = false
            binding.etAboutPlayList.isActivated = false
        }

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNeutralButton("Отмена") { dialog, which -> }
            .setPositiveButton("Завершить") { dialog, which -> findNavController().popBackStack() }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitToView()
                }
            })

        binding.etNamePlayList.doOnTextChanged { text, start, before, count ->
            binding.bCreate.isEnabled = binding.etNamePlayList.text.isNotBlank()
            binding.etNamePlayList.isActivated = binding.etNamePlayList.text.isNotEmpty()
        }

        binding.etAboutPlayList.doOnTextChanged { text, start, before, count ->
            binding.etAboutPlayList.isActivated = binding.etAboutPlayList.text.isNotEmpty()
        }

        binding.bCreate.setOnClickListener {
            if (arguments == null) {
                Toast(requireContext()).showCustomToast(
                    getString(
                        R.string.playlist_create,
                        binding.etNamePlayList.text
                    ), requireActivity()
                )
                viewModel.createNewPlayList(
                    binding.etNamePlayList.text.toString(),
                    binding.etAboutPlayList.text.toString(),
                    imageUri
                )
                findNavController().popBackStack()
            } else {
                viewModel.editNewPlayList(
                    requireArguments().parcelable<PlayListUI>(PLAYLIST_ITEM)?.roadToFileImage?.toUri(),
                    requireArguments().parcelable<PlayListUI>(PLAYLIST_ITEM)!!.id,
                    binding.etNamePlayList.text.toString(),
                    binding.etAboutPlayList.text.toString(),
                    imageUri
                )
                findNavController().popBackStack()
            }
        }

        viewModel.nameImage

        binding.backIv.setOnClickListener {
            exitToView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun itemUse() {
        with(binding) {
            requireArguments().parcelable<PlayListUI>(PLAYLIST_ITEM)
                ?.let { playlist ->
                    if (playlist.roadToFileImage.isNotEmpty()) {
                        imageUri = playlist.roadToFileImage.toUri()
                        imageInsert.setImageURI(imageUri)
                    }
                    etNamePlayList.setText(playlist.namePlayList)
                    etNamePlayList.isActivated = true
                    if (playlist.aboutPlayList != "") {
                        etAboutPlayList.setText(playlist.aboutPlayList)
                        etAboutPlayList.isActivated = true
                    }
                    tvNameFragment.text = getString(R.string.edit_playlist)
                    bCreate.text = getString(R.string.save_fragment_playlist)
                }
        }
    }

    private fun exitToView() {
        if (arguments == null) {
            if (!binding.etAboutPlayList.text.isNullOrEmpty() || !binding.etNamePlayList.text.isNullOrEmpty() || binding.imageInsert.drawable != null) {
                confirmDialog.show()
            } else {
                findNavController().popBackStack()
            }
        } else {
            findNavController().popBackStack()
        }
    }

    companion object {
        const val PLAYLIST_ITEM = "Playlist"
    }
}