package com.example.sisvita

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.sisvita.data.RetrofitService
import com.example.sisvita.data.RetrofitServiceFactory
import com.example.sisvita.data.models.AuthModel
import com.example.sisvita.data.models.AuthResponseModel
import com.example.sisvita.ui.theme.SisVitaTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import retrofit2.await

class MainActivity : ComponentActivity() {
    private suspend fun callLoginApi(service: RetrofitService, model: AuthModel): AuthResponseModel {
           return service.loginAccount(model)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val makeRetrofitService = RetrofitServiceFactory.makeRetrofitService()
        val model = AuthModel("bruto12345","jvc223344")
        var response : AuthResponseModel
        lifecycleScope.launch {
            response = callLoginApi(makeRetrofitService, model)
            Log.d("SisVita", "Status: ${response.status}")  // Using Timber or Logcat
            Log.d("SisVita", "Token: ${response.token}")

        }


        setContent {
            SisVitaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SisVitaTheme {
        Greeting("Android")
    }
}