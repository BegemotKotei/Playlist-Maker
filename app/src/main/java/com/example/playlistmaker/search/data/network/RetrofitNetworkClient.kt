package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class RetrofitNetworkClient(private val iTunesApi: ITunesApi) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        return withContext(Dispatchers.IO) {
            try {
                if (dto is TrackSearchRequest) {
                    val result = iTunesApi.search(dto.expression)
                    result.apply { resultCode = 200 }
                } else {
                    Response().apply { resultCode = 400 }
                }
            } catch (ex: HttpException) {
                Response().apply { resultCode = ex.code() }
            } catch (ex: Exception) {
                Response().apply { resultCode = 400 }
            }
        }
    }
}