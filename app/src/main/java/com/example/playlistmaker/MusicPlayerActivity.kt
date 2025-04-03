package com.example.playlistmaker

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityMusicPlayerBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MusicPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMusicPlayerBinding
    private val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener { finish() }

        intent.parcelable<Track>(getTrackKey())?.let { track ->
            displayTrackInfo(track)
            loadTrackImage(track)
        } ?: run { Log.i("MusicPlayerActivity", "Intent не содержит трек") }

        setupIntents()
    }

    private fun displayTrackInfo(track: Track) {
        try {
            binding.tvNameMusic.text = track.trackName
            binding.tvGroupName.text = track.artistName
            binding.tvTimeMusicAnswer.text = dateFormat.format(track.trackTimeMillis.toLong())
            if (track.collectionName == null) {
                binding.textGroup.isVisible = false
            } else {
                binding.tvGroupMusicAnswer.text = track.collectionName
            }
            binding.tvEarAnswer.text = getFormattedDate(track.releaseDate)
            binding.tvTypeMusicAnswer.text = track.primaryGenreName
            binding.tvCountryAnswer.text = track.country
            Log.i("MusicPlayerActivity", track.toString())
        } catch (e: Exception) {
            Log.i("MusicPlayerActivity", e.toString())
        }
    }

    private fun setupIntents() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            v.setPadding(
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom,
            )
            insets
        }
    }

    private fun loadTrackImage(track: Track) {
        val urlImage = track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

        Glide.with(this)
            .load(urlImage)
            .placeholder(R.drawable.placeholder_ic)
            .centerInside()
            .transform(RoundedCorners(getRadiusCutImage()))
            .into(binding.ivMusicImage)

    }

    private fun getFormattedDate(inputDate: String?): String {
        if (inputDate.isNullOrEmpty()) return ""

        val inputFormat: SimpleDateFormat by lazy {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        }

        val outputFormat: SimpleDateFormat by lazy {
            SimpleDateFormat("yyyy", Locale.getDefault())
        }

        inputFormat.parse(inputDate)?.let { date ->
            return outputFormat.format(date).orEmpty()
        } ?: return ""
    }

    companion object {
        private const val TRACK_KEY = "TRACK"

        fun getTrackKey(): String {
            return TRACK_KEY
        }
    }


}