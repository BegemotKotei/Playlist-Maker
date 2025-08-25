package com.example.playlistmaker.player.presentation.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistItemBsBinding
import com.example.playlistmaker.playlist_create.presentation.models.PlayListUI

class BottomSheetPlayListAdapter : RecyclerView.Adapter<BottomSheetPlayListViewHolder>() {
    var data: List<PlayListUI> = emptyList()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    var onClick: (PlayListUI) -> Unit = {}
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BottomSheetPlayListViewHolder {
        val view = LayoutInflater.from(parent.context)
        return BottomSheetPlayListViewHolder(PlaylistItemBsBinding.inflate(view, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BottomSheetPlayListViewHolder, position: Int) {
        holder.bind(data[position])
        holder.itemView.setOnClickListener { onClick(data[position]) }
    }
}