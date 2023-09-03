package com.example.testtask.network

interface ApiHelper {
    suspend fun getUser(page: Int, limit: Int): JsonItem
}