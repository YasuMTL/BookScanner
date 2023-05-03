package com.yasunari_k.bookscanner.network

import com.yasunari_k.bookscanner.model.Book
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL =
    "https://www.googleapis.com/books/v1/volumes/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface BookApiService {
    @GET(" ")
    suspend fun getBooks(
        @Query("q") isbnCode: String
    ): Book
}

object BookApi {
    val retrofitService : BookApiService by lazy {
        retrofit.create(BookApiService::class.java)
    }
}