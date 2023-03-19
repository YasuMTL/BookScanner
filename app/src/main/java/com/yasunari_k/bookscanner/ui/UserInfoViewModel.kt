package com.yasunari_k.bookscanner.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.yasunari_k.bookscanner.Repository
import com.yasunari_k.bookscanner.model.Book
import com.yasunari_k.bookscanner.model.BookBorrower
import com.yasunari_k.bookscanner.model.Info
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserInfoViewModel(private val repository: Repository): ViewModel() {
    private val _bookBorrower = MutableStateFlow(
        BookBorrower(
            name = "",
            emailAddress = "",
            registerDate = ""
        )
    )
    val bookBorrower: StateFlow<BookBorrower> = _bookBorrower.asStateFlow()

    private val _bookInfoInMemory = MutableStateFlow(Info(title = "", authors = emptyList(), publishedDate = "", description = ""))
    val bookInfoInMemory: StateFlow<Info> = _bookInfoInMemory.asStateFlow()

    fun updateBorrowerInfo(borrowerInfo: String) {
        if (!isQrCodeConformed(borrowerInfo)) {
            return
        }

        viewModelScope.launch {
            val borrowerInfoMap = convertFetchedInfoToUserData(borrowerInfo)
            Log.d("ViewModel","Convert fetched info to user data")
            updateBorrower(borrowerInfoMap)
            Log.d("ViewModel","Update is done!")
        }
    }

    private fun updateBorrower(borrowerInfoMap: Map<String, String>) {
        _bookBorrower.update {
            it.copy(
                name = borrowerInfoMap["name"] ?: "",
                emailAddress = borrowerInfoMap["email"] ?: "",
                registerDate = borrowerInfoMap["registeredDate"] ?: ""
            )
        }
        Log.d("updateBorrower", "BorrowerName=${borrowerInfoMap["name"]}")
    }

    fun isQrCodeConformed(dataFromQrCode: String): Boolean {
        return dataFromQrCode.contains("name") &&
                dataFromQrCode.contains("date") &&
                dataFromQrCode.contains("email")
    }

    fun isIsbnCodeFormatConformed(codeToCheck: String): Boolean {
        return codeToCheck.startsWith("978")
    }

    fun updateBookInfo(isbnCode: String) {
        viewModelScope.launch {
            val response: Book = repository.fetchInfo(isbnCode)
            _bookInfoInMemory.value = response
            Log.d("ViewModel", "Response: $response")
        }
    }

    fun extractInfoFromBook(book: Book):Info {
        Log.d("extractInfoFromBook", "book=$book")
        val bookInfo = book.items.first().volumeInfo
        val title = bookInfo.title
        val authors = bookInfo.authors
        val publishedDate = bookInfo.publishedDate
        val description = bookInfo.description

        return Info(title, authors, publishedDate, description)
    }

    fun convertFetchedInfoToUserData(scannedJsonData: String): Map<String, String> {
        val fetchedBookBorrower = Gson().fromJson(scannedJsonData, BookBorrower::class.java)

        val borrowerInfoMap= mutableMapOf(
            "name" to fetchedBookBorrower.name,
            "email" to fetchedBookBorrower.emailAddress,
            "registerdDate" to fetchedBookBorrower.registerDate,
        )

        Log.d("BookBorrower",
            "name=${fetchedBookBorrower.name}, " +
                 "email=${fetchedBookBorrower.emailAddress}, " +
                 "registeredDate=${fetchedBookBorrower.registerDate}"
        )

        return borrowerInfoMap
    }

    fun resetInfo() {
        viewModelScope.launch {
            _bookBorrower.update {
                it.copy(name = "", emailAddress = "", registerDate = "")
            }
            _bookInfoInMemory.update {
                it.copy(title = "", authors = emptyList(), publishedDate = "", description = "")
            }
        }
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