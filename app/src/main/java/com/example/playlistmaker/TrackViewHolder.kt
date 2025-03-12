package com.example.playlistmaker

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.TrackItemBinding

class TrackViewHolder(private val binding: TrackItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Track) {
        binding.apply {
            binding.tvTrackName.text = item.trackName
            binding.tvArtistName.text = item.artistName
            binding.tvTrackTime.text = item.trackTime
            Glide.with(itemView)
                .load(item.artworkUrl100)
                .placeholder(R.drawable.placeholder_ic)
                .centerInside()
                .transform(RoundedCorners(10))
                .into(binding.ivArtworkUrl100)
        }
    }
}