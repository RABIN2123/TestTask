package com.example.testtask.list


data class GifList(
    val list: List<GifItem> = mutableListOf(),
    val status: Int = 0,
    val currentOffset: Int = 0
)

data class GifItem(
    val id: String = "",
    val smallImageUrl: String = "",
    val bigImageUrl: String = ""
)
