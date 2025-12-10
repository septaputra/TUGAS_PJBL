package com.example.multiactivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.multiactivity.ui.theme.MultiActivityTheme

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultiActivityTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SecondScreen(
                        modifier = Modifier.padding(innerPadding),
                        onBackClick = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun SecondScreen(modifier: Modifier = Modifier, onBackClick: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Anda di Layar Kedua")
        Button(onClick = onBackClick) {
            Text("KEMBALI")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SecondScreenPreview() {
    MultiActivityTheme {
        SecondScreen(onBackClick = {})
    }
}
