package com.yasunari_k.bookscanner.model

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
    val title: String? = null,
    val authors: List<String>? = null,
    val publishedDate: String? = null,
    val description: String? = null
)