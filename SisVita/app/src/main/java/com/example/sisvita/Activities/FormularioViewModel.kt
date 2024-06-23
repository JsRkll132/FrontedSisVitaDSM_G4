package com.example.sisvita.Activities

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sisvita.data.RetrofitServiceFactory
import com.example.sisvita.data.models.FormularioCompletadoModelResponse
import com.example.sisvita.data.models.FormularioModel
import kotlinx.coroutines.launch

class FormularioViewModel : ViewModel() {

    var results by mutableStateOf<List<FormularioCompletadoModelResponse>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    fun getFormularios() {
        viewModelScope.launch {
            val service = RetrofitServiceFactory.makeRetrofitService()
            try {
                results = service.obtener_puntuacionesAllService()
                isLoading = false
            } catch (e: Exception) {
                Log.e("FormularioViewModel", "Error: ${e.message}", e)
                isLoading = false
                // Handle error
            }
        }
    }

    var formularioDetail by mutableStateOf<FormularioCompletadoModelResponse?>(null)
        private set

    fun getFormularioDetail(completado_formulario_id: Int) {
        viewModelScope.launch {
            val service = RetrofitServiceFactory.makeRetrofitService()
            try {
                formularioDetail = service.obtener_puntuaciones_paciente_formulario(completado_formulario_id)
                isLoading = false
            } catch (e: Exception) {
                Log.e("FormularioViewModel", "Error: ${e.message}", e)
                isLoading = false
                // Handle error
            }
        }
    }
    var AllFormTypes by mutableStateOf <List<FormularioModel>>(emptyList())
        private set
    fun getFormTypes() {
        viewModelScope.launch {

            val service = RetrofitServiceFactory.makeRetrofitService()

            try {
                AllFormTypes = service.getForms()
                isLoading = false
            } catch (e: Exception) {
                Log.e("FAllFormTypes", "Error: ${e.message}", e)
                isLoading = false
                // Handle error
            }
        }
    }

}