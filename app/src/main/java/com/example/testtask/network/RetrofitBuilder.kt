package com.example.testtask.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
    private const val BASE_URL = "https://api.giphy.com/v1/gifs/"
    const val API_KEY = "miIxt3Gl7YaZErQR3DRlAGRuRDjZHruk"
    private fun getRetrofit() = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(
        GsonConverterFactory.create()
    ).build()

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)
}