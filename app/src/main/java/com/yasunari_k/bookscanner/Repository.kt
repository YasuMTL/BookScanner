package com.yasunari_k.bookscanner

import android.util.Log
import com.yasunari_k.bookscanner.model.Book
import com.yasunari_k.bookscanner.network.BookApi

class Repository {
    suspend fun fetchInfo(isbnCode: String): Book {
        val isbnCodeToPassIn = "isbn:$isbnCode"

        val bookInfo = BookApi.retrofitService.getBooks(isbnCodeToPassIn)
        Log.d("testBookApi", "bookInfo = $bookInfo")
        return bookInfo
    }
}