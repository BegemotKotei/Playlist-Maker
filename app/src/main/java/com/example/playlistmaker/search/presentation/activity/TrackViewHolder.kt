package com.example.playlistmaker.search.presentation.activity

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.dpToPx
import com.example.playlistmaker.getRadiusCutImage
import com.example.playlistmaker.search.presentation.models.TrackUI

class TrackViewHolder(private val binding: TrackItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: TrackUI) {
        with(binding) {
            tvTrackName.text = item.trackName
            tvArtistName.text = item.artistName
            tvTrackTime.text = item.trackTimeMillis
        }

        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.placeholder_ic)
            .centerInside()
            .transform(RoundedCorners(dpToPx(getRadiusCutImage(), itemView.context)))
            .into(binding.ivArtworkUrl100)
    }
}