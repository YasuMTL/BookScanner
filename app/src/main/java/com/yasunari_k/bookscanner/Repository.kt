package com.yasunari_k.bookscanner

import com.yasunari_k.bookscanner.model.Book
import com.yasunari_k.bookscanner.model.getInitialised
import com.yasunari_k.bookscanner.network.BookApi

class Repository {
    suspend fun fetchInfo(isbnCode: String): Book {
        val isbnCodeToPassIn = "isbn:$isbnCode"

        println("isbnCode=$isbnCode")

        val bookInfo: Book = try {
            BookApi.retrofitService.getBooks(isbnCodeToPassIn)
        } catch (exception: Exception) {
            getInitialised()
        }

        return bookInfo
    }
}