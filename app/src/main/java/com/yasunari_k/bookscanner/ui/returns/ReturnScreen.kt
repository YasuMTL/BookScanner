package com.yasunari_k.bookscanner.ui.returns

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yasunari_k.bookscanner.model.BookBorrower
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun ReturnScreen(
    onClickBackButton: () -> Unit = {},
    onClickBorrowedBook: (String) -> Unit = {},
    borrowerState: State<BookBorrower>,
) {
    val borrowedBooks = borrowerState.value.borrowedBooksList
    //TODO Fetch the borrowed books list first
    Log.d("ReturnScreen", "borrowedBooks=$borrowedBooks")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .semantics { contentDescription = "Return Screen" },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn {
            items(
                items = borrowedBooks,
                itemContent = {
                    BorrowedBookListItem(
                        borrowedBook = it,
                        onClickBorrowedBook = { onClickBorrowedBook(it.title) }
                    )
                }
            )
        }
        Button(
            onClick = onClickBackButton,
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)
                .padding(5.dp)
        ) {
            Text(text = "Back")
        }
    }
}

@Composable
fun BorrowedBookListItem(
    borrowedBook: BorrowedBook,
    onClickBorrowedBook: () -> Unit = {}
) {
    val context = LocalContext.current

    Row (verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ){
        Column (
            modifier = Modifier.
                weight(3F),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = borrowedBook.title,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Text(text = borrowedBook.dateToReturn)
        }
        Button(
            modifier = Modifier.
                height(50.dp).
                padding(5.dp).
                weight(2F),
            onClick = {
                Toast.makeText(context, "${borrowedBook.title}を本当に返却してもよろしいですか？", Toast.LENGTH_SHORT).show()
                //TODO
                // 1. Ask user if they want to confirm the return of the book
                // 2. Proceed the book return by noting returned date in the field of "Book was returned" on the spreadsheet
                val borrower = borrowedBook.borrowerName
                borrowedBook.dateToReturn
                //Need to read the data on the Spreadsheet first in order to know in which row you have to fill up the field of Book was returned

                //TODO: read() to get the row number
                onClickBorrowedBook()

                //write the date of today in the field with the row number
            }
        ) {
            Text(text = "返却する")
        }
    }
}

@Preview
@Composable
fun PreviewReturnScreen() {
    MaterialTheme {
        ReturnScreen(
            onClickBorrowedBook = { bookTitle ->

            },
            onClickBackButton = {

            },
            borrowerState = MutableStateFlow(
                BookBorrower(
                    name = "Test Yasu",
                    emailAddress = "aaabbb@com",
                    registerDate = "2023-07-12",//todo: No more need the "registerDate"
                    borrowedBooksList = mutableListOf(
                        BorrowedBook("Test Yasu", "Test 1", "Someday"),
                        //BorrowedBook("Kanemitsu", "Mini dictionnaire Français-Allemand Allemand-Français", "2023-07-08 16:59"),
                        BorrowedBook("Kanemitsu", "Mini dictionnaire Français-Allemand aaaa", "2023-07-08 16:59"),
                        BorrowedBook("Kanemitsu", "ももたろうと金太郎", "2023-07-08 16:59"),
                        BorrowedBook("Test Yasu", "Test 3", "Someday")
                    )
                )
            ).asStateFlow().collectAsState()
        )
    }
}

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