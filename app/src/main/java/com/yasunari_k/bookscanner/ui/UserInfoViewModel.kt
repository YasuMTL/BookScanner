package com.yasunari_k.bookscanner.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yasunari_k.bookscanner.Repository
import com.yasunari_k.bookscanner.model.Book
import com.yasunari_k.bookscanner.model.BookBorrower
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserInfoViewModel(private val repository: Repository): ViewModel() {

    private val _bookBorrower: MutableStateFlow<BookBorrower> = MutableStateFlow(
        BookBorrower(name = "", emailAddress = "", registerDate = ""))
    val bookBorrower: StateFlow<BookBorrower> = _bookBorrower.asStateFlow()

    private val _bookInfoInMemory: MutableStateFlow<Book> = MutableStateFlow(Book(kind = "", totalItems = 0, items = emptyList()))
    val bookInfoInMemory: StateFlow<Book> = _bookInfoInMemory.asStateFlow()

    fun getBookInfo(isbnCode: String) {
        viewModelScope.launch {
            val response: Book = repository.fetchInfo(isbnCode)
            _bookInfoInMemory.value = response
            Log.d("ViewModel", "Response: $response")
        }
    }

    fun setUserInfo(userInfo: String) {
        val bookBorrower = createBorrower(userInfo)
        _bookBorrower.value = bookBorrower
    }

    private fun createBorrower(userInfo: String): BookBorrower {
        Log.d("createBorrower", "convert received info to BookBorrower")
        return BookBorrower("1", "22", "333")
    }

    // Define ViewModel factory in a companion object
    companion object {
        fun provideViewModelFactory(
            repository: Repository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(UserInfoViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return UserInfoViewModel(
                        repository = repository
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel Class")
            }
        }
    }
}