package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import retrofit2.HttpException
import retrofit2.Retrofit

class RetrofitNetworkClient(
    private val retrofit: Retrofit
) : NetworkClient {

    private val iTunesApi = retrofit.create(iTunesApi::class.java)

    override fun doRequest(dto: Any): Response {
        try {
            if (dto is TrackSearchRequest) {
                val resp = iTunesApi.search(dto.expression).execute()
                val body = resp.body() ?: Response()
                return body.apply { resultCode = resp.code() }
            } else {
                return Response().apply { resultCode = 400 }
            }
        } catch (exception: HttpException) {
            return Response().apply { resultCode = exception.code() }
        } catch (e: Exception) {
            return Response().apply { resultCode = 400 }
        }
    }
}