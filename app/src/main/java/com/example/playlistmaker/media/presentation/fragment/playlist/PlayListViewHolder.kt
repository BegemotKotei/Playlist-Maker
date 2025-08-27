package com.example.playlistmaker.media.presentation.fragment.playlist

import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.core.resourceManager.IResourceManager
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.playlist_create.presentation.models.PlayListUI

class PlayListViewHolder(
    private val binding: PlaylistItemBinding,
    private val resourceManager: IResourceManager
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: PlayListUI) {
        with(binding) {
            tvNamePlaylist.text = item.namePlayList
            tvCountTrack.text = resourceManager.getTracksPlural(item.count)
            if (item.roadToFileImage.isNotEmpty()) {
                ivImage.setImageURI(item.roadToFileImage.toUri())
            } else {
                ivImage.setImageResource(R.drawable.placeholder_ic)
            }
        }
    }
}
