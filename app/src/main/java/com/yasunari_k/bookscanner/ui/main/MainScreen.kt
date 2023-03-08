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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(
    onClickAuth: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .semantics { contentDescription = "Main Screen" },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "モントリオール日本語センター", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onClickAuth,
            modifier = Modifier.semantics { contentDescription = "Show Camera View" }
        ) {
            Text(text = "Scan QR Code", fontSize = 40.sp)
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