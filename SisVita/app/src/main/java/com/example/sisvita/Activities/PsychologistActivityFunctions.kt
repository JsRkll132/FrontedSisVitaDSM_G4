package com.example.sisvita.Activities

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sisvita.data.RetrofitServiceFactory
import com.example.sisvita.data.models.DiagnosticModel
import com.example.sisvita.data.models.FormularioCompletadoModelResponse
import com.example.sisvita.data.models.FormularioModel
import kotlinx.coroutines.launch


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
fun DiagnosticController(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val formularioCompletadoModelResponse = sharedViewModel.selectedResult
    // This composable should allow the specialist to evaluate test results,
    // confirm the template result, add anxiety level, observations,
    // invite for appointment, and notify via email or WhatsApp
    var diagnosticText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var results by remember { mutableStateOf<List<FormularioCompletadoModelResponse>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if (formularioCompletadoModelResponse == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No data available")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("DIAGNOSTICAR", style = MaterialTheme.typography.titleLarge)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Paciente: ${formularioCompletadoModelResponse.nombres} ${formularioCompletadoModelResponse.apellido_paterno} ${formularioCompletadoModelResponse.apellido_materno}")
                Text("Tipo de Formulario: ${formularioCompletadoModelResponse.tipo_formulario}")
                Text("Puntuación Obtenida: ${formularioCompletadoModelResponse.suma_puntuacion}")
                Text("Fecha: ${formularioCompletadoModelResponse.fecha_completado}")
            }
        }

        OutlinedTextField(
            value = diagnosticText,
            onValueChange = { diagnosticText = it },
            label = { Text("Diagnóstico") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                isLoading = true
                val diagnosticModel = DiagnosticModel(
                    calificacion = formularioCompletadoModelResponse.suma_puntuacion,
                    completado_formulario_id = formularioCompletadoModelResponse.completado_formulario_id,
                    paciente_id = formularioCompletadoModelResponse.paciente_id,
                    formulario_id = formularioCompletadoModelResponse.formulario_id,
                    psicologo_id = 1,
                    diagnostico = diagnosticText
                )
                val service = RetrofitServiceFactory.makeRetrofitService()
                scope.launch {
                    try {
                        val result = service.DiagnosticarService(diagnosticModel)
                        if (result.status == 1 ){
                            Toast.makeText(context, "Formulario enviado correctamente", Toast.LENGTH_SHORT).show()
                        }
                        isLoading = false
                    } catch (e: Exception) {
                        Log.e("DiagnosticController", "Error: ${e.message}", e)
                        isLoading = false
                        // Handle error
                    }
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Diagnosticar")
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun EvaluateResultsScreen(navController: NavController, sharedViewModel: SharedViewModel) {
    val formularioViewModel: FormularioViewModel = viewModel()
    var isLoading by remember { mutableStateOf(true) }
    var results by mutableStateOf<List<FormularioCompletadoModelResponse>>((emptyList()))
    var formTypes by mutableStateOf<List<FormularioModel>>((emptyList()))
    isLoading = formularioViewModel.isLoading
    @Composable
    fun excecuteData() {
        LaunchedEffect(Unit){
            formularioViewModel.getFormularios()
            formTypes = formularioViewModel.AllFormTypes
        }
    }
    LaunchedEffect(Unit) {
        formularioViewModel.getFormularios()
        formularioViewModel.getFormTypes()

    }

    results = formularioViewModel.results
    formTypes = formularioViewModel.AllFormTypes

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            var expanded by remember { mutableStateOf(false) }
            var selectedFormType by remember { mutableStateOf<String?>(null) }

            // Dropdown menu for selecting form type
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)) {
                Column {
                    Text("Seleccione el tipo de formulario")
                    OutlinedButton(onClick = { expanded = true }) {
                        Text( sharedViewModel.selectedFormType ?: "Todos los formularios")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            sharedViewModel.selectedFormType = null
                            expanded = false
                        }) {
                            Text("Todos los formularios",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                        }
                        formTypes.forEach { formType ->
                            DropdownMenuItem(onClick = {
                                sharedViewModel.selectedFormType= formType.formulario_tipo
                                expanded = false

                            }) {
                                excecuteData()
                                Text(formType.formulario_tipo,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }

            // Filtering results based on selected form type
            val filteredResults = if ( sharedViewModel.selectedFormType != null) {
                results.filter { it.tipo_formulario ==  sharedViewModel.selectedFormType }
            } else {
                results
            }

            LazyColumn {
                items(filteredResults) { result ->
                    EvaluateResultItem(result, navController, sharedViewModel)
                }
            }
        }
    }
}
@Composable
fun EvaluateResultItem(
    result: FormularioCompletadoModelResponse,
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    // Asegurarse de que los valores no sean nulos, proporcionando valores predeterminados
    val levelAnxiety = result.nivel_ansiedad ?: "NORMAL"
    val levelAnxietyColor = when (levelAnxiety) {
        "MUY ALTA" -> Color(0xFFFF0000) // Rojo fuerte
        "ALTA" -> Color(0xFFFF6666) // Rojo suave
        "MODERADA" -> Color(0xFFFFA500) // Naranja
        "NORMAL" -> MaterialTheme.colorScheme.surface // Color actual del tema
        else -> MaterialTheme.colorScheme.surface // Color actual del tema
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)

            .clickable(onClick = {
                sharedViewModel.selectedResult = result
                navController.navigate("evaluate/diagnostico")
            })

    ) {
        Column(
            modifier = Modifier.padding(1.dp)
        
        ) {
            Row(
                modifier = Modifier.padding(3.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            color = Color(0xFFA9C6E3),
                            shape = RoundedCornerShape(9.dp)
                        )
                        .clip(RoundedCornerShape(9.dp))
                ) {
                    Text(
                        text = "Paciente: ",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                    )
                    Text(
                        text = "${result.nombres ?: ""} ${result.apellido_paterno ?: ""} ${result.apellido_materno ?: ""}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                    )
                }
            }

            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Tipo de Formulario: ",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                )
                Text(
                    text = result.tipo_formulario ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                )
            }

            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Puntuación Obtenida: ",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                )
                Text(
                    text = result.suma_puntuacion?.toString() ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                )
            }

            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Nivel de ansiedad según test: ",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                )
                Text(
                    text = levelAnxiety,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                )
            }

            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Fecha: ",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                )
                Text(
                    text = result.fecha_completado ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                )
            }
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
