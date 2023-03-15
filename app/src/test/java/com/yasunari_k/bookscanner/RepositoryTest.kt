package com.yasunari_k.bookscanner

import com.yasunari_k.bookscanner.model.Book
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class RepositoryTest {

    private val BASE_URL =
        "https://www.googleapis.com/books/v1/volumes/"
    lateinit var retrofit: Retrofit
    lateinit var repository: Repository

    @Before
    fun setUp() {
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .build()
        repository = Repository()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun fetchInfo_withIsbnCode_success() = runTest {
        val book: Book = repository.fetchInfo("9782894649268")
        val bookInfo = book.items.first().volumeInfo

        assert(bookInfo.title == "Le Québécois Pour Mieux Voyager")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun fetchInfo_withNotIsbnBarcode_fail() = runTest {
        val book: Book = repository.fetchInfo("819039021111")//Tile Mate
        assert(book.kind == "No Data Available")
    }

    @Test
    fun checkRetrofitInstance() {
        assert(retrofit.baseUrl().url().toString() == BASE_URL)
    }
}