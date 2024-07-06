package com.example.sisvita


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sisvita.Activities.LoginActivity
import com.example.sisvita.Activities.RegisterActivity


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

                MainScreen()


        }
    }

    @Composable
    fun MainScreen() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
                Text(text = "SYSVITA", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))




            Button(
                onClick = {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                }
            ) {
                Text("Iniciar Sesion")
            }
            Button(
                onClick = {
                    startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
                }
            ) {
                Text("Registrar Cuenta")
            }
        }
    }
}