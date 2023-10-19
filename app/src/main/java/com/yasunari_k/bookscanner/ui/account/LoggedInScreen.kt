package com.yasunari_k.bookscanner.ui.account

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yasunari_k.bookscanner.model.BookBorrower
import com.yasunari_k.bookscanner.model.Info

@Composable
fun LoggedInScreen(
    onClickBorrow: () -> Unit = {},
    onClickReturn: () -> Unit = {},
    onClickLogout: () -> Unit = {},
    borrowerState: State<BookBorrower>,
    borrowedBookState: State<Info>
) {
    val borrowerName = borrowerState.value.name

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .semantics { contentDescription = "LoggedIn Screen" },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BorrowerName(borrowerName = borrowerName)

        Text(
            text = borrowedBookState.value.title ?: "Not Found", textAlign = TextAlign.Center, fontSize = 40.sp
        )

        Column(modifier = Modifier
            .width(300.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val width = Modifier
                .width(400.dp)
                .height(120.dp)
                .padding(10.dp)
            val fontSize = 32.sp
            Button(
                onClick = onClickBorrow,
                modifier = width.semantics { contentDescription = "Borrow Button" }
            ) {
                Text(text = "Borrow", fontSize = fontSize)
            }
            Button(
                onClick = onClickReturn,
                modifier = width.semantics { contentDescription = "Return Button" }
            ) {
                Text(text = "Return", fontSize = fontSize)
            }
            Button(
                onClick = onClickLogout,
                modifier = Modifier.semantics { contentDescription = "Logout Button" }
            ) {
                Text(text = "Logout")
            }
        }
    }
}

@Composable
fun BorrowerName(borrowerName: String) {
    Text(
        text = borrowerName, textAlign = TextAlign.Center, fontSize = 40.sp
    )
}

/*
@Preview
@Composable
fun PreviewLoggedInScreen() {
    MaterialTheme {
        LoggedInScreen()
    }
}*/
