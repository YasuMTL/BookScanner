package com.yasunari_k.bookscanner.network

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL =
    "https://www.googleapis.com/books/v1/volumes/"
//private val moshi = Moshi.Builder()
//    .add(KotlinJsonAdapterFactory())
//    .build()

private val retrofit = Retrofit.Builder()
//    .addConverterFactory(MoshiConverterFactory.create(moshi))
    //.addConverterFactory(GsonConverterFactory.create())
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface BookApiService {
    @GET(" ")
//    suspend fun getBookInfo(): String
    suspend fun getBooks(
        @Query("q") inTitle: String,
        @Query("key") apiKey: String
    ): Response<String>
}

object BookApi {
    val retrofitService : BookApiService by lazy {
        retrofit.create(BookApiService::class.java)
    }
}