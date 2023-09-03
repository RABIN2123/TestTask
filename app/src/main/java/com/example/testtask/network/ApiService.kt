package com.example.testtask.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("trending?api_key=${RetrofitBuilder.API_KEY}")
    suspend fun getGifs(
        @Query("offset") page: Int,
        @Query("limit") limit: Int
    ): JsonItem
}