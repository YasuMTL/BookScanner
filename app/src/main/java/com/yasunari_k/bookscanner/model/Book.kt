package com.yasunari_k.bookscanner.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Book(
    val kind: String,
    val totalItems: Int,
    val items: List<Item>
)
fun getInitialised(): Book {
    return Book("No Data Available", 0, emptyList())
}


data class Item(
    val volumeInfo: Info
)

data class Info(
    val title: String,
    val authors: List<String>,
    val publishedDate: String,
    val description: String
)
