package com.example.sisvita.Activities

import android.content.Intent
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
import com.example.sisvita.data.RetrofitService
import com.example.sisvita.data.RetrofitServiceFactory
import com.example.sisvita.data.models.AuthModel
import com.example.sisvita.data.models.AuthResponseModel
import com.example.sisvita.saveToken
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

                    val model = AuthModel(password,username)
                    var response : AuthResponseModel ?= null
                    lifecycleScope.launch {

                        response = callLoginApi(makeRetrofitService, model)
                        isLoading = false
                        Log.d("SisVita", "Status: ${response?.status}")  // Using Timber or Logcat
                        Log.d("SisVita", "Token: ${response?.token}")
                        if (response?.status.equals("sucess login")){
                            response?.token?.let { saveToken(it,this@LoginActivity) }
                            Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@LoginActivity,  "Login Failed", Toast.LENGTH_SHORT).show()
                        }
                        if (response?.status.equals("sucess login")){
                            startActivity(Intent(this@LoginActivity, TestActivity::class.java))
                        }


                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f),
                enabled = !isLoading
            ) {
                Text("Iniciar sesión")
            }
        }
    }

    private suspend fun performLogin(request: AuthModel, callback: (Boolean, String?, String?) -> Unit) {
        val apiService = RetrofitServiceFactory.makeRetrofitService()
        try {
            val response =  apiService.loginAccount(request)
            if (response.status=="success login" ) {
                callback(true, response.token, null)
            } else {
                callback(false, null, "Login Failed: ${response.status}")
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = errorBody ?: "HTTP Error: ${e.code()}"
            callback(false, null, errorMessage)
        } catch (e: IOException) {
            callback(false, null, "Network error")
        } catch (e: Exception) {
            callback(false, null, "An unexpected error occurred")
        }
    }
}