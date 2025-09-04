package com.example.playlistmaker.core.resourceManager

import android.content.Context

class ResourceManager(private val context: Context) : IResourceManager {
    override fun getStringById(resId: Int): String {
        return context.getString(resId)
    }

    override fun getStringById(resId: Int, vararg args: String): String {
        return context.getString(resId, *args)
    }

    override fun getTracksPlural(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> "$count трек"
            count % 10 in 2..4 && count % 100 !in 12..14 -> "$count трека"
            else -> "$count треков"
        }
    }

    override fun formatMinutes(numberMinutes: Int): String {
        return when {
            numberMinutes % 10 == 1 && numberMinutes % 100 != 11 -> "$numberMinutes минута"
            numberMinutes % 10 in 2..4 && numberMinutes % 100 !in 12..14 -> "$numberMinutes минуты"
            else -> "$numberMinutes минут"
        }
    }
}