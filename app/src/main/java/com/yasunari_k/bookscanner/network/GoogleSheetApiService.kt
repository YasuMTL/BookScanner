package com.yasunari_k.bookscanner.network

import com.google.gson.GsonBuilder
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

private const val BASE_URL =
    "https://script.google.com/macros/s/AKfycbyLoBSipNiIWs-DFl-mNCtnfZ0_3NzHE5DrzZytaCmrAA4g7f4tGqjqQQbZ_JWmwJc6sw/"
//https://docs.google.com/spreadsheets/d/1wj15p6XhNNphsMXYP8xQq4ftrxCCjG8mCA-ufPM_ukE/edit?usp=sharing
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

val gson = GsonBuilder()
.setLenient()
.create()

private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(20, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    })
    .build()

private val retrofit = Retrofit.Builder()
//    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addConverterFactory(GsonConverterFactory.create(gson))
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()

interface GoogleSheetApiService {
    @GET("exec")
    suspend fun getSomething(
        //@Path(" ") bookNameToNote: String
    ): Response<String>
}

object SheetApi {
    val retrofitService : GoogleSheetApiService by lazy {
        retrofit.create(GoogleSheetApiService::class.java)
    }
}