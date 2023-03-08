package com.yasunari_k.bookscanner.ui.account

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoggedInScreen(
    onClickBorrow: () -> Unit = {},
    onClickReturn: () -> Unit = {},
    onClickLogout: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .semantics { contentDescription = "LoggedIn Screen" },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val userName = "Yasunari KanemitsuKanemitsuKanemitsu"
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
                modifier = width
            ) {
                Text(text = "Borrow", fontSize = fontSize)
            }
            Button(
                onClick = onClickReturn,
                modifier = width
            ) {
                Text(text = "Return", fontSize = fontSize)
            }
            Button(
                onClick = onClickLogout
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