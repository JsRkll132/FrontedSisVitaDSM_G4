package com.example.sisvita.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.sisvita.ui.theme.SisVitaTheme

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SisVitaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TestScreen()
                }
            }
        }
    }

    @Composable
    fun TestScreen() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    startActivity(Intent(this@TestActivity, QuestionsActivity::class.java))
                }
            ) {
                Text("Inventario de ansiedad de Beck")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    startActivity(Intent(this@TestActivity, QuestionsActivity::class.java))
                }
            ) {
                Text("Inventario de ansiedad de Beck")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    startActivity(Intent(this@TestActivity, QuestionsActivity::class.java))
                }
            ) {
                Text("Inventario de ansiedad de Beck")
            }
        }
    }
}

