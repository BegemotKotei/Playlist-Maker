package com.example.playlistmaker.presentation.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.databinding.TrackItemBinding

class TrackAdapter : RecyclerView.Adapter<TrackViewHolder>() {

    private var data: MutableList<Track> = mutableListOf()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onClick: (Track) -> Unit = {}

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(data[position])
        holder.itemView.setOnClickListener { onClick(data[position]) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding =
            TrackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)

    }

    fun getDate(): MutableList<Track> {
        return data
    }

    fun setDate(newData: MutableList<Track>) {
        data = newData
    }
}