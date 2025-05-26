package com.example.playlistmaker.search.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitConfig {
    fun provideRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.SECONDS)
            .connectTimeout(3, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}