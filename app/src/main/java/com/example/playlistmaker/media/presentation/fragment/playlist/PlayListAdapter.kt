package com.example.playlistmaker.media.presentation.fragment.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.playlist_create.presentation.models.PlayListUI

class PlayListAdapter : RecyclerView.Adapter<PlayListViewHolder>() {
    var data: List<PlayListUI> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onClick: (PlayListUI) -> Unit = {}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder {
        val view = LayoutInflater.from(parent.context)
        return PlayListViewHolder(PlaylistItemBinding.inflate(view, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PlayListViewHolder, position: Int) {
        holder.bind(data[position])
        holder.itemView.setOnClickListener { onClick(data[position]) }
    }
}