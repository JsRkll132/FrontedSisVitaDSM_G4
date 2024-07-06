package com.example.sisvita.Activities.PsychologistActions

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sisvita.Activities.ViewModelsPackage.FormularioViewModel
import com.example.sisvita.Activities.ViewModelsPackage.SharedViewModel
import com.example.sisvita.data.models.FormularioCompletadoModelResponse
import com.example.sisvita.data.models.FormularioModel
import java.time.Instant.ofEpochMilli
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


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

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun EvaluateResultsScreen(navController: NavController, sharedViewModel: SharedViewModel) {
    val formularioViewModel: FormularioViewModel = viewModel()
    var isLoading by remember { mutableStateOf(true) }
    var results by mutableStateOf<List<FormularioCompletadoModelResponse>>(emptyList())
    var formTypes by remember { mutableStateOf<List<FormularioModel>>(emptyList()) }

    isLoading = formularioViewModel.isLoading

    // Function to execute data fetching on composition start
    @Composable
    fun executeData() {
        LaunchedEffect(Unit) {
            formularioViewModel.getFormularios()
            formTypes = formularioViewModel.AllFormTypes
        }
    }

    // Fetch data on initial composition
    LaunchedEffect(Unit) {
        formularioViewModel.getFormularios()
        formularioViewModel.getFormTypes()
    }

    // Update state with fetched data
    results = formularioViewModel.results
    formTypes = formularioViewModel.AllFormTypes

    // Mutable state for date selection and date picker visibility
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var state = rememberDatePickerState()
    sharedViewModel.isInVigilance = true
    // Function to convert ISO date string to LocalDate

    // Function to format Instant date to LocalDate
    var selectedDate_ = state.selectedDateMillis
    selectedDate_?.let {
        selectedDate =
            ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDateTime().format(DateTimeFormatter.ISO_DATE).toString()
        Log.d("FECHA SELECCIONADA", selectedDate!!)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            var expanded by remember { mutableStateOf(false) }
            var selectedFormType by remember { mutableStateOf<String?>(null) }
            var active by remember {
                mutableStateOf(false)
            }
            // Dropdown menu for selecting form type
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)) {
                Column {

                    Spacer(modifier = Modifier.height(3.dp))
                    Row {
                        OutlinedButton(onClick = { expanded = true }) {
                            Text(sharedViewModel.selectedFormType ?: "Todos los formularios")
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        OutlinedButton(onClick = { showDatePicker = true }) {
                            Text("Seleccionar Fecha")
                        }
                        Spacer(modifier = Modifier.width(2.dp))

                    }
                    // Date picker dialog
                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                Button(
                                    onClick = { showDatePicker = false
                                }) {

                                    Text(text = "Seleccionar")

                                }
                            },
                            dismissButton = {
                                OutlinedButton(onClick = { showDatePicker = false }) {
                                    Text(text = "Cancelar")
                                }
                            }
                        ) {
                            DatePicker(state = state)
                        }
                    }

                    // Dropdown menu for form types
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            sharedViewModel.selectedFormType = null
                            expanded = false
                        }) {
                            Text(
                                "Todos los formularios",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        formTypes.forEach { formType ->
                            DropdownMenuItem(onClick = {
                                sharedViewModel.selectedFormType = formType.formulario_tipo
                                expanded = false
                            }) {
                                if (!expanded){
                                    executeData()
                                }

                                Text(
                                    formType.formulario_tipo,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
            fun convertDateFormat(inputDate: String): String {
                // Parsear el string original a ZonedDateTime usando RFC_1123_DATE_TIME
                val parsedDate = ZonedDateTime.parse(inputDate, DateTimeFormatter.RFC_1123_DATE_TIME)

                // Obtener LocalDate de ZonedDateTime
                val localDate = parsedDate.toLocalDate()

                // Formatear la nueva fecha a ISO_DATE sin 'Z' final
                val formattedDate = localDate.format(DateTimeFormatter.ISO_DATE)

                Log.d("FECHA FORMADA: ",formattedDate)
                return formattedDate
            }

            // Filtering results based on selected form type and selected date
            var filteredResults by mutableStateOf<List<FormularioCompletadoModelResponse>>(emptyList())
            filteredResults = results.filter { result ->
                (sharedViewModel.selectedFormType == null || result.tipo_formulario == sharedViewModel.selectedFormType) &&
                        (selectedDate == null || (convertDateFormat(result.fecha_completado).equals(selectedDate.toString()))) &&
                        (sharedViewModel.searchQueryName.isNullOrBlank() || "${result.nombres} ${result.apellido_paterno} ${result.apellido_materno}".contains(sharedViewModel.searchQueryName ?: "", ignoreCase = true) || "${result.nivel_ansiedad}".contains(sharedViewModel.searchQueryName ?: "", ignoreCase = true))
            }

            if (selectedDate == null && sharedViewModel.selectedFormType == null && sharedViewModel.searchQueryName.isNullOrBlank()) {
                executeData()
                filteredResults = results
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
    //default Color(0xFFA9C6E3)
    // Asegurarse de que los valores no sean nulos, proporcionando valores predeterminados
    val levelAnxiety = result.nivel_ansiedad ?: "NORMAL"
    val levelAnxietyCardColor = when (levelAnxiety) {
        "MUY ALTA" -> Color(0xFFFF8080) // Rojo fuerte
        "ALTA" -> Color(0xC4FFA16F) // Rojo suave
        "MODERADA" -> Color(0xCDFFE27C) // Naranja
        "NORMAL" -> Color(0xC6D9FF93) // Color actual del tema
        else -> Color(0xC6D9FF93) // Color actual del tema
    }
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

            .clickable(onClick = {
                sharedViewModel.selectedResult = result
                navController.navigate("evaluate/diagnostico")
            })
        ,
        colors = CardDefaults.cardColors(
            containerColor = levelAnxietyCardColor
        )

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

                            color = levelAnxietyNameColor,
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
