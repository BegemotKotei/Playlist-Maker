package com.example.playlistmaker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackItemBinding

class TrackAdapter(
    private var data: ArrayList<Track>,
    val listener: ((Track) -> Unit)
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(data[position])
        holder.itemView.setOnClickListener { listener(data[position]) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding =
            TrackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: ArrayList<Track>) {
        data = newData
        notifyDataSetChanged()
    }
}