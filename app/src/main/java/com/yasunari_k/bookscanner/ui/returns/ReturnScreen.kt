package com.yasunari_k.bookscanner.ui.returns

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
                itemContent = { borrowedBook ->
                    BorrowedBookListItem(
                        borrowedBook = borrowedBook,
                        onClickBorrowedBook = {
                            val bookTitleToErase = borrowedBook.title
                            onClickBorrowedBook(bookTitleToErase)

                            //borrowedBooks.remove(borrowedBook)
                        }
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

    val returnBook = remember { mutableStateOf(false) }
    val callDialog = remember { mutableStateOf(false) }
    Log.d("ReturnScreen", "returnBook=$returnBook")
    Log.d("ReturnScreen", "callDialog=$callDialog")

    if (callDialog.value) {
        Log.d("ReturnScreen", "Call CustomDialog!")
        CustomDialog(
            openDialogCustom = callDialog,
            returnBook = returnBook
        )
    }

    if (returnBook.value) {
        Log.d("ReturnScreen", "User wishes to return book!")
        onClickBorrowedBook()
        returnBook.value = false
    }

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
            modifier = Modifier
                .height(50.dp)
                .padding(5.dp)
                .weight(2F),
            onClick = {
                Log.d("Button", "returnBook.value=${returnBook.value}")
                callDialog.value = true
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

@Composable
fun CustomDialog(
    openDialogCustom: MutableState<Boolean>,
    returnBook: MutableState<Boolean>
) {
    Dialog(onDismissRequest = {
        openDialogCustom.value = false
    }) {
        ReturnConfirmationDialogUI(
            openDialogCustom = openDialogCustom,
            returnBook = returnBook
        )
    }
}

//Layout
@Composable
fun ReturnConfirmationDialogUI(
    modifier: Modifier = Modifier,
    openDialogCustom: MutableState<Boolean>,
    returnBook: MutableState<Boolean>
){
    Card(
        //shape = MaterialTheme.shapes.medium,
        shape = RoundedCornerShape(10.dp),
        // modifier = modifier.size(280.dp, 240.dp)
        modifier = Modifier.padding(10.dp,5.dp,10.dp,10.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier
                .background(Color.White)) {

            //.......................................................................
//            Image(
//                painter = painterResource(id = R.drawable.notification),
//                contentDescription = null, // decorative
//                contentScale = ContentScale.Fit,
//                colorFilter  = ColorFilter.tint(
//                    color = Purple40
//                ),
//                modifier = Modifier
//                    .padding(top = 35.dp)
//                    .height(70.dp)
//                    .fillMaxWidth(),
//
//                )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "この本を返却しても\nよろしいですか？",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.h5,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
//                Text(
//                    text = "Allow Permission to send you notifications when new art styles added.",
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier
//                        .padding(top = 10.dp, start = 25.dp, end = 25.dp)
//                        .fillMaxWidth(),
//                    style = MaterialTheme.typography.body1
//                )
            }
            //.......................................................................
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(Color.LightGray),
                horizontalArrangement = Arrangement.SpaceAround) {

                TextButton(onClick = {
                    openDialogCustom.value = false
                    returnBook.value = false
                }) {

                    Text(
                        "いいえ",
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
                TextButton(onClick = {
                    openDialogCustom.value = false
                    returnBook.value = true
                }) {
                    Text(
                        "はい",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
            }
        }
    }
}


@SuppressLint("UnrememberedMutableState")
@Preview (name="Custom Dialog")
@Composable
fun MyDialogUIPreview(){
    ReturnConfirmationDialogUI(
        openDialogCustom = mutableStateOf(false),
        returnBook = mutableStateOf(false)
    )
}