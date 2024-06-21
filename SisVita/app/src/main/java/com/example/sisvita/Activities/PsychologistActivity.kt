package com.example.sisvita.Activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sisvita.data.RetrofitServiceFactory
import com.example.sisvita.data.models.FormularioCompletadoModelResponse
import com.example.sisvita.ui.theme.SisVitaTheme

class PsychologistActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SisVitaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PsychologistScreen()
                }
            }
        }
    }

    @Composable
    fun PsychologistScreen() {
        val navController = rememberNavController()
        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            bottomBar = { PsychologistBottomNavigationBar(navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "vigilance",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("vigilance") { VigilanceScreen() }
                composable("evaluate") { EvaluateResultsScreen() }
                composable("heatmap") { HeatMapScreen() }
            }
        }
    }

    @Composable
    fun PsychologistBottomNavigationBar(navController: NavHostController) {
        val items = listOf(
            BottomNavItem("Vigilance", "vigilance", Icons.Default.CheckCircle),
            BottomNavItem("Evaluate", "evaluate", Icons.Default.Edit),
            BottomNavItem("Heatmap", "heatmap", Icons.Default.LocationOn)
        )

        BottomNavigation(
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            items.forEach { item ->
                BottomNavigationItem(
                    icon = { Icon(item.icon, contentDescription = null) },
                    label = { Text(item.label) },
                    selected = currentDestination?.route == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }

    data class BottomNavItem(val label: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
}

@Composable
fun VigilanceScreen() {
    // This composable should include filters, list of participants, and their test scores
    // Also include a color-coded indicator for critical cases (red, amber, green)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Vigilance Screen")
    }
}
@Composable
fun EvaluateResultsScreen() {
    // This composable should allow the specialist to evaluate test results,
    // confirm the template result, add anxiety level, observations,
    // invite for appointment, and notify via email or WhatsApp
    var results by remember { mutableStateOf<List<FormularioCompletadoModelResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var trigger by remember { mutableStateOf(0) }
    LaunchedEffect(trigger) {
        val service = RetrofitServiceFactory.makeRetrofitService()
        try {
            results = service.obtener_puntuacionesAllService()
            Log.d(results.get(0)?.apellido_materno,"")
            isLoading = false
        } catch (e: Exception) {
            Log.e("EvaluateResultsScreen", "Error: ${e.message}", e)
            isLoading = false
            // Handle error
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn {
            items(results) { result ->
                EvaluateResultItem(result)
            }
        }
    }
}

@Composable
fun EvaluateResultItem(result: FormularioCompletadoModelResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),

    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Nombre: ${result?.nombres} ${result?.apellido_paterno} ${result?.apellido_materno}", style = MaterialTheme.typography.bodySmall)
            Text(text = "ID: ${result?.completado_formulario_id}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Tipo de Formulario: ${result?.tipo_formulario}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Puntuación: ${result?.suma_puntuacion}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Fecha: ${result?.fecha_completado}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
@Composable
fun HeatMapScreen() {
    // This composable should visualize a heat map with the geographical location
    // of cases using some geolocation data

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Heat Map Screen")
    }
}
