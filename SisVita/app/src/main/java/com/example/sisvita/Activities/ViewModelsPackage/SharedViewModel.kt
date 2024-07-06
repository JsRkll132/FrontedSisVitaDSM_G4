package com.example.sisvita.Activities.ViewModelsPackage
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sisvita.data.models.FormularioCompletadoModelResponse
import com.example.sisvita.data.models.FormularioModel

class SharedViewModel : ViewModel() {
    var selectedResult: FormularioCompletadoModelResponse? = null
    var allFormsDd: List<FormularioModel> by mutableStateOf(emptyList())
    var selectedFormType: String? by mutableStateOf(null)
    var searchQueryName: String? by mutableStateOf("")
    var isInVigilance: Boolean by mutableStateOf(false)
}