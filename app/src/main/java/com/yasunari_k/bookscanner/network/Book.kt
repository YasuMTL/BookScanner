package com.yasunari_k.bookscanner.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Book(
    val kind: String,
    val totalItems: Int,
    val items: List<Item>
)

data class Item(
    val volumeInfo: Info
)

data class Info(
    val title: String,
    val authors: List<String>,
    val publishedDate: String,
    val description: String
)
