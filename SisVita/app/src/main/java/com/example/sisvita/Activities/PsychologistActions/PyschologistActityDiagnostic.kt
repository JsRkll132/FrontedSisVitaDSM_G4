package com.example.sisvita.Activities.PsychologistActions

import android.graphics.Typeface.BOLD
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sisvita.Activities.ViewModelsPackage.SharedViewModel
import com.example.sisvita.data.RetrofitServiceFactory
import com.example.sisvita.data.models.DiagnosticModel
import com.example.sisvita.data.models.FormularioCompletadoModelResponse
import kotlinx.coroutines.launch

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
    val levelAnxiety = formularioCompletadoModelResponse?.nivel_ansiedad ?: "NORMAL"
    sharedViewModel.isInVigilance = false

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
        val levelAnxietyNameColor = when (levelAnxiety) {
            "MUY ALTA" -> Color(0xFFFF0000) // Rojo fuerte
            "ALTA" -> Color(0xC4FF5900) // Rojo suave
            "MODERADA" -> Color(0xCDFFC700) // Naranja
            "NORMAL" -> Color(0xC6A5FF00) // Color actual del tema
            else -> Color(0xC6A5FF00) // Color actual del tema
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row {
                    Column {
                        Text(
                            text = "Nivel de Ansiedad: ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = levelAnxiety,
                            color =levelAnxietyNameColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        Text(
                            text = "Tipo de Test ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = formularioCompletadoModelResponse?.tipo_formulario?:"",
                            fontSize = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row {
                    Text(
                        text = "Paciente : ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${formularioCompletadoModelResponse?.nombres} ${formularioCompletadoModelResponse?.apellido_paterno} ${formularioCompletadoModelResponse?.apellido_materno}"
                    )
                }
                Spacer(modifier = Modifier.height(15.dp)) // Espaciado entre filas

                Row {
                    Text(
                        text = "Tipo de Formulario: ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(text = formularioCompletadoModelResponse?.tipo_formulario.toString())
                }
                Spacer(modifier = Modifier.height(15.dp)) // Espaciado entre filas

                Row {
                    Text(
                        text = "Puntuación Obtenida : ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(text = formularioCompletadoModelResponse?.suma_puntuacion.toString())
                }
                Spacer(modifier = Modifier.height(15.dp)) // Espaciado entre filas

                Row {
                    Text(
                        text = "Fecha : ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(text = formularioCompletadoModelResponse?.fecha_completado.toString())
                }
                Spacer(modifier = Modifier.height(15.dp)) // Espaciado entre filas


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