package com.example.playlistmaker

import android.os.Bundle
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

            val urlImage = track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

            Glide.with(this)
                .load(urlImage)
                .placeholder(R.drawable.placeholder_ic)
                .centerInside()
                .transform(RoundedCorners(getRadiusCutImage()))
                .into(binding.ivMusicImage)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) {v, insets ->
            v.setPadding(
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom,
            )
            insets
        }
    }

    private fun getFormattedDate(inputDate: String?): String {
        inputDate ?: return ""

        val dfIn = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val dfOut = SimpleDateFormat("yyyy", Locale.getDefault())

        dfIn.parse(inputDate)?.let { date ->
            return dfOut.format(date).orEmpty()
        } ?: return ""
    }

    companion object {
        private const val TRACK_KEY = "TRACK"

        fun getTrackKey(): String {
            return TRACK_KEY
        }
    }


}