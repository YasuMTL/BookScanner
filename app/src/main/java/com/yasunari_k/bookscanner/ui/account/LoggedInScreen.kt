package com.yasunari_k.bookscanner.ui.account

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yasunari_k.bookscanner.ui.UserInfoViewModel

@Composable
fun LoggedInScreen(
    onClickBorrow: () -> Unit = {},
    onClickReturn: () -> Unit = {},
    onClickLogout: () -> Unit = {},
    userInfoViewModel: UserInfoViewModel = viewModel()
) {
    val bookBorrowerUiState by userInfoViewModel.bookBorrower.collectAsState()
    val bookInfoUiState by userInfoViewModel.bookInfoInMemory.collectAsState()

    bookBorrowerUiState.name
    bookInfoUiState.items.first().volumeInfo.title

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .semantics { contentDescription = "LoggedIn Screen" },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val userName = "Justin Trudeau"//TODO: How to get user name?
        Text(
            text = userName, textAlign = TextAlign.Center, fontSize = 40.sp
        )
        //Spacer(modifier = Modifier.height(16.dp))

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

@Preview
@Composable
fun PreviewLoggedInScreen() {
    MaterialTheme {
        LoggedInScreen()
    }
}