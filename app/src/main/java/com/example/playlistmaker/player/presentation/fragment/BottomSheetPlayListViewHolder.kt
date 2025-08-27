package com.example.playlistmaker.player.presentation.fragment

import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.core.resourceManager.IResourceManager
import com.example.playlistmaker.databinding.PlaylistItemBsBinding
import com.example.playlistmaker.playlist_create.presentation.models.PlayListUI

class BottomSheetPlayListViewHolder(
    private val binding: PlaylistItemBsBinding,
    private val resourceManager: IResourceManager
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: PlayListUI) {
        with(binding) {
            tvNamePlaylistBS.text = item.namePlayList
            tvCountTrackBS.text = resourceManager.getTracksPlural(item.count)
            if (item.roadToFileImage.isNotEmpty()) {
                ivImageBS.setImageURI(item.roadToFileImage.toUri())
            } else {
                ivImageBS.setImageResource(R.drawable.placeholder_ic)
            }
        }
    }
}