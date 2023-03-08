package com.yasunari_k.bookscanner.ui.returns

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReturnScreen(
    onClickBackButton: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .semantics { contentDescription = "Return Screen" },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Display Borrowed Books List here!",
            fontSize = 30.sp
        )
        Button(
            onClick = onClickBackButton,
            modifier = Modifier
                .width(200.dp)
                .height(60.dp)) {
            Text(text = "Back")
        }
    }
}

@Preview
@Composable
fun PreviewReturnScreen() {
    MaterialTheme {
        ReturnScreen()
    }
}