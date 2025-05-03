package com.example.playlistmaker.presentation.player

import java.text.SimpleDateFormat
import java.util.Locale

object DateFormatter {
    fun formatReleaseDate(inputDate: String?): String {
        if (inputDate.isNullOrEmpty()) return ""
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy", Locale.getDefault())
            inputFormat.parse(inputDate)?.let { date ->
                outputFormat.format(date)
            } ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}