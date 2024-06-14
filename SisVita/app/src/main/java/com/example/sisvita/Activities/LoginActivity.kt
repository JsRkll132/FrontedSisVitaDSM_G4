package com.example.sisvita.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.auth0.android.jwt.JWT
import com.example.sisvita.clearToken
import com.example.sisvita.data.RetrofitService
import com.example.sisvita.data.RetrofitServiceFactory
import com.example.sisvita.data.models.AuthModel
import com.example.sisvita.data.models.AuthResponseModel
import com.example.sisvita.getUserIdFromToken
import com.example.sisvita.saveToken
import com.example.sisvita.saveUserId
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen()
        }
    }

    private suspend fun callLoginApi(service: RetrofitService, model: AuthModel): AuthResponseModel {
        return service.loginAccount(model)
    }

    @Composable
    fun LoginScreen() {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Iniciar sesión", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(0.8f),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isLoading = true
                    val makeRetrofitService = RetrofitServiceFactory.makeRetrofitService()
                    val model = AuthModel(password, username)
                    lifecycleScope.launch {
                        try {
                            val response = callLoginApi(makeRetrofitService, model)
                            isLoading = false
                            Log.d("SisVita", "Status: ${response?.status}")  // Using Timber or Logcat
                            Log.d("SisVita", "Token: ${response?.token}")
                            if (response?.status.equals("sucess login")) {
                                Log.d("VERFIESD","in if resonpos")
                                response.token?.let { token ->
                                    saveToken(token, this@LoginActivity)
                                    getUserIdFromToken(token)?.let { userId ->
                                        saveUserId(userId, this@LoginActivity)
                                    }
                                    Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()

                                }
                            } else {
                                Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()
                            }
                            if (response?.status.equals("sucess login")){
                                navigateToTestActivity()
                            }
                        } catch (e: Exception) {
                            isLoading = false
                            Toast.makeText(this@LoginActivity, "Failed to connect", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f),
                enabled = !isLoading
            ) {
                Text("Iniciar sesión")
            }

            Spacer(modifier = Modifier.height(16.dp))

           /* Button(
                onClick = {
                    clearToken(this@LoginActivity)
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Cerrar sesión")
            }*/
        }
    }

    private fun navigateToTestActivity() {
        startActivity(Intent(this@LoginActivity, TestActivity::class.java))
        finish()
    }
}