package com.example.sisvita.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.sisvita.data.RetrofitServiceFactory
import com.example.sisvita.data.models.FormularioModel
import com.example.sisvita.ui.theme.SisVitaTheme
import kotlinx.coroutines.launch

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SisVitaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun MainScreen() {
        val navItems = listOf("Home", "Forms", "Settings")
        var selectedItem by remember { mutableStateOf(0) }

        Scaffold(
            bottomBar = {
                BottomNavigation {
                    navItems.forEachIndexed { index, item ->
                        BottomNavigationItem(
                            icon = {
                                when (item) {
                                    "Home" -> Icon(Icons.Filled.Home, contentDescription = item)
                                    "Forms" -> Icon(Icons.Filled.List, contentDescription = item)
                                    "Settings" -> Icon(Icons.Filled.Settings, contentDescription = item)
                                }
                            },
                            label = { Text(item) },
                            selected = selectedItem == index,
                            onClick = { selectedItem = index }
                        )
                    }
                }
            }
        ) {
            when (selectedItem) {
                0 -> HomeScreen()
                1 -> FormsScreen()
                2 -> SettingsScreen()
            }
        }
    }

    @Composable
    fun HomeScreen() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Home Screen")
        }
    }

    @Composable
    fun FormsScreen() {
        var forms by remember { mutableStateOf<List<FormularioModel>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            lifecycleScope.launch {
                val service = RetrofitServiceFactory.makeRetrofitService()
                try {
                    forms = service.getForms()
                    isLoading = false
                } catch (e: Exception) {
                    isLoading = false
                    Toast.makeText(this@TestActivity, "Failed to load forms", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            FormsList(forms)
        }
    }

    @Composable
    fun SettingsScreen() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Settings Screen")
        }
    }

    @Composable
    fun FormsList(forms: List<FormularioModel>) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            forms.forEach { form ->
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        val intent = Intent(this@TestActivity, QuestionsActivity::class.java)
                        intent.putExtra("FORM_ID", form.id)
                        startActivity(intent)
                    }
                ) {
                    Text(form.formulario_tipo)
                }
            }
        }
    }
}
