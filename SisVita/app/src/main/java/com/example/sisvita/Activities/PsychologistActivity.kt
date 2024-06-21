package com.example.sisvita.Activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PsychologistScreen() {
        val navController = rememberNavController()
        val coroutineScope = rememberCoroutineScope()
        val sharedViewModel: SharedViewModel = viewModel()
        Scaffold(
            topBar = {
                Row {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable(onClick = { navController.popBackStack() })
                    )
                }
            },

            bottomBar = { PsychologistBottomNavigationBar(navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "vigilance",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("vigilance") { VigilanceScreen() }
                composable("evaluate") { EvaluateResultsScreen(navController,sharedViewModel) }
                composable("heatmap") { HeatMapScreen() }
                composable("evaluate/diagnostico") { DiagnosticController(navController,sharedViewModel )}
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
