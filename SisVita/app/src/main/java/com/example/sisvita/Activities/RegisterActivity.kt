package com.example.sisvita.Activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sisvita.Activities.ViewModelsPackage.HeatMapSvViewModel
import com.example.sisvita.Activities.ViewModelsPackage.UbigeosViewModel
import com.example.sisvita.data.RetrofitServiceFactory
import com.example.sisvita.data.models.RegisterModel
import com.example.sisvita.data.models.TotalUbigeosResponse
import com.example.sisvita.data.models.TotalUbigeosResponseItem
import kotlinx.coroutines.launch


data class Distrito(val distrito: String, val ubigeo: String)
data class Provincia(val provincia: String, val distritos: List<Distrito>)
data class Departamento(val departamento: String, val provincias: List<Provincia>)
class RegisterActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterScreen()
        }
    }

    @Composable
    fun RegisterScreen() {
        var username by remember { mutableStateOf("") }
        var firstName by remember { mutableStateOf("") }
        var lastNameP by remember { mutableStateOf("") }
        var lastNameM by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var ubigeo by remember { mutableStateOf("") }
        var userType by remember { mutableStateOf("paciente") }
        var phoneNumber by remember { mutableStateOf("") }
        var expanded by remember { mutableStateOf(false) }
        val viewModel : UbigeosViewModel = viewModel()

        var  selectedDistrito by remember { mutableStateOf<com.example.sisvita.data.models.Distrito?>(null) }
        var selectedProvincia  by remember { mutableStateOf<com.example.sisvita.data.models.Provincia?>(null) }
        var selectedDepartamento by remember { mutableStateOf<TotalUbigeosResponseItem?>(null) }
        LaunchedEffect(Unit) {
            viewModel.getUbigeos()
        }
        val allData = viewModel.results

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Registrar Usuario", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de Usuario") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Nombres") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastNameP,
                onValueChange = { lastNameP = it },
                label = { Text("Apellido Paterno") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastNameM,
                onValueChange = { lastNameM = it },
                label = { Text("Apellido Materno") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ubigeo,
                onValueChange = { ubigeo = it },
                label = { Text("N°Ubigeo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = userType,
                    onValueChange = { },
                    label = { Text("Tipo de Usuario") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Expand")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    DropdownMenuItem(
                        text = { Text("Paciente") },
                        onClick = {
                            userType = "paciente"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Psicólogo") },
                        onClick = {
                            userType = "psicologo"
                            expanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Número de Celular") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val registerUser = RegisterModel(
                        nombre_usuario = username,
                        nombres = firstName,
                        apellido_paterno = lastNameP,
                        apellido_materno = lastNameM,
                        contrasena = password,
                        correo = email,
                        tipo_usuario = userType,
                        numero_celular = phoneNumber,
                        ubigeo = ubigeo
                    )
                    registerUser(registerUser)
                }
            ) {
                Text("Registrar")
            }
        }
    }

    private fun registerUser(user: RegisterModel) {

        lifecycleScope.launch {
            val service = RetrofitServiceFactory.makeRetrofitService()
            try {
                val response = service.registerUser(user)
                Log.d(response.status.User,response.sucess.toString())
                if (response.sucess == 1) {
                    Toast.makeText(this@RegisterActivity, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                    finish() // Finaliza la actividad después del registro exitoso
                } else {
                    Toast.makeText(this@RegisterActivity, "Ocurrió un error al registrar usuario", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("RegisterActivity", "Error registrando usuario", e)
                Toast.makeText(this@RegisterActivity, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @Composable
    @Preview
    fun RegisterScreenPreview(){
        RegisterScreen()
    }
}
