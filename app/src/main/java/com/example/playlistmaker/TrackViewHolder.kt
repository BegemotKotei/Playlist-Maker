package com.example.playlistmaker

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.TrackItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(private val binding: TrackItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    fun bind(item: Track) {
        binding.apply {
            binding.tvTrackName.text = item.trackName
            binding.tvArtistName.text = item.artistName
            binding.tvTrackTime.text = dateFormat.format(item.trackTimeMillis.toLong())
            Glide.with(itemView)
                .load(item.artworkUrl100)
                .placeholder(R.drawable.placeholder_ic)
                .centerInside()
                .transform(RoundedCorners(getRadiusCutImage()))
                .into(binding.ivArtworkUrl100)
        }
    }
}