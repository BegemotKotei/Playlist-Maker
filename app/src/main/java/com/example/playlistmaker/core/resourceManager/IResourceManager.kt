package com.example.playlistmaker.core.resourceManager

import androidx.annotation.StringRes

interface IResourceManager {
    fun getStringById(@StringRes resId: Int): String
    fun getStringById(@StringRes resId: Int, vararg args: String): String
    fun getTracksPlural(count: Int): String
    fun formatMinutes(numberMinutes: Int): String
}