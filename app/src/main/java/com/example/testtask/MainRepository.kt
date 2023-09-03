package com.example.testtask

import android.util.Log
import com.example.testtask.list.GifItem
import com.example.testtask.list.GifList
import com.example.testtask.network.ApiHelper

class MainRepository(private val apiHelper: ApiHelper) {
    suspend fun getGifs(page: Int, limit: Int = 50): GifList {
        Log.d("TAG", "Current page = $page")
        val newState = apiHelper.getUser(page, limit)
        return GifList(list = newState.data.map { item ->
            GifItem(
                id = item.id,
                smallImageUrl = item.images.fixedWidthDownsampled.url,
                bigImageUrl = item.images.fixedHeight.url
            )
        }, status = newState.meta.status, currentOffset = page)
    }

}