package com.yasunari_k.bookscanner.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MainScreen(
    onClickAuth: () -> Unit = {},
    onClickLoggedIn: () -> Unit = {},
    onClickBorrow: () -> Unit = {},
    onClickReturn: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .semantics { contentDescription = "Main Screen"},
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { /*TODO*/ }) {
            Text(text = "MainScreen")
        }
        Row {
            Button(onClick = onClickAuth) {
                Text(text = "Auth")
            }
            Button(onClick = onClickLoggedIn) {
                Text(text = "Loggedin")
            }
            Button(onClick = onClickBorrow) {
                Text(text = "Borrow")
            }
            Button(onClick = onClickReturn) {
                Text(text = "Return")
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MaterialTheme {
        MainScreen()
    }
}