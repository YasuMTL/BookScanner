package com.yasunari_k.bookscanner.model

import com.google.gson.annotations.SerializedName
import com.yasunari_k.bookscanner.ui.returns.BorrowedBook

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
    val registerDate: String,
    val borrowedBooksList: MutableList<BorrowedBook>
)