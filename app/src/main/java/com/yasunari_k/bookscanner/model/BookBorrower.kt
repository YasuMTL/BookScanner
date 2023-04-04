package com.yasunari_k.bookscanner.model

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/*{
    "place": "Japanese Center",
    "name": "かねみつ",
    "date": "2023-03-07",
    "email": "test.aaa@hotmail.com"
}*/
data class BookBorrower(
    val name: String,
    @SerializedName("email")
    val emailAddress: String,
    @SerializedName("date")
    val registerDate: String
)

object UserData {
    var user = BookBorrower("", "", "")

    fun convertFetchedInfoToUserData(scannedJsonData: String) {
        val fetchedBookBorrower = Gson().fromJson(scannedJsonData, BookBorrower::class.java)
        val name = fetchedBookBorrower.name
        val emailAddress = fetchedBookBorrower.emailAddress
        val registeredDate = fetchedBookBorrower.registerDate

        Log.d("BookBorrower", "name=$name, email=$emailAddress, registeredDate=$registeredDate")

        user = BookBorrower(name, emailAddress, registeredDate)
    }

    fun emptyUserInfo() {
        user = BookBorrower("", "", "")
    }
}

object BookList {
    var books = mutableListOf<Book>()

    init {
        emptyList()
    }

    fun emptyList() {
        books.clear()
    }
}