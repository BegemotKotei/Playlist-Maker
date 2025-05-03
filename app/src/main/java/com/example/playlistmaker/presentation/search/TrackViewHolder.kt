package com.example.playlistmaker.presentation.search

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.presentation.dpToPx
import com.example.playlistmaker.presentation.getRadiusCutImage
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
                .transform(RoundedCorners(dpToPx(getRadiusCutImage(), itemView.context)))
                .into(binding.ivArtworkUrl100)
        }
    }
}