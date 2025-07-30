package com.example.playlistmaker.core.resourceManager

import android.content.Context

//TODO пробросить в di
class ResourceManager(private val context: Context) : IResourceManager {
    override fun getStringById(resId: Int): String {
        return context.getString(resId)
    }

    override fun getStringById(resId: Int, vararg args: String): String {
        return context.getString(resId, *args)
    }
}