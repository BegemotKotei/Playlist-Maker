package com.example.playlistmaker.search.presentation.fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.search.presentation.models.TrackUI

class TrackAdapter : RecyclerView.Adapter<TrackViewHolder>() {
    var data: List<TrackUI> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onClick: (TrackUI) -> Unit = {}
    var onLongClick: (TrackUI) -> Unit = {}

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(data[position])
        holder.itemView.setOnClickListener {
            val item = data.getOrNull(position) ?: return@setOnClickListener
            onClick(item)
        }
        holder.itemView.setOnLongClickListener {
            val item = data.getOrNull(position) ?: return@setOnLongClickListener false
            onLongClick(item)
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
        return TrackViewHolder(TrackItemBinding.inflate(view, parent, false))
    }
}