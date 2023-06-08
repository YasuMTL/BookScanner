package com.yasunari_k.bookscanner.ui.returns

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.yasunari_k.bookscanner.model.BookBorrower

@Composable
fun ReturnScreen(
    onClickBackButton: () -> Unit = {},
    borrowerState: State<BookBorrower>,
) {
    val borrowedBooks = borrowerState.value.borrowedBooksList
    //TODO Fetch the borrowed books list first

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .semantics { contentDescription = "Return Screen" },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn {
            items(//How to pass in the borrower's name? Does it really need to identify the book to return?
                items = borrowedBooks,
                itemContent = {
                    BorrowedBookListItem(borrowedBook = it)
                }
            )
        }
        Button(
            onClick = onClickBackButton,
            modifier = Modifier
                .width(200.dp)
                .height(60.dp)) {
            Text(text = "Back")
        }
    }
}

@Composable
fun BorrowedBookListItem(borrowedBook: BorrowedBook) {
    val context = LocalContext.current

    Row {
        Column {
            Text(text = borrowedBook.title)
            Text(text = borrowedBook.dateToReturn)
        }
        Button(
            onClick = {
                Toast.makeText(context, "${borrowedBook.title}を本当に返却してもよろしいですか？", Toast.LENGTH_SHORT).show()
                //TODO
                // 1. Ask user if they want to confirm the return of the book
                // 2. Proceed the book return by noting returned date in the field of "Book was returned" on the spreadsheet
                val borrower = borrowedBook.borrowerName
                //Need to read the data on the Spreadsheet first in order to know in which row you have to fill up the field of Book was returned

                //TODO: read() to get the row number

                //write the date of today in the field with the row number
            }
        ) {
            Text(text = "返却する")
        }
    }
}

//@Preview
//@Composable
//fun PreviewReturnScreen() {
//    MaterialTheme {
//        ReturnScreen(
//            {/*DO nothing*/},
//            DataProvider.borrowedBookList)
//    }
//}

//Some data to mock the presentation
data class BorrowedBook(
    val borrowerName: String,
    val title: String,
    val dateToReturn: String
)
object DataProvider {
//    val borrowedBookList = listOf(
//        BorrowedBook("test 1", "2023-06-01"),
//        BorrowedBook("test 2", "2023-06-02"),
//        BorrowedBook("test 3", "2023-06-03"),
//        BorrowedBook("test 4", "2023-06-04"),
//        BorrowedBook("test 5", "2023-06-05"),
//    )
}