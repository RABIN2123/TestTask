package com.example.testtask.network

class ApiHelperImpl(private val apiService: ApiService): ApiHelper {
    override suspend fun getUser(page: Int, limit: Int): JsonItem {
        return apiService.getGifs(page = page, limit = limit)
    }
}