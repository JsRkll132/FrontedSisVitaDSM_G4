package com.example.sisvita.Activities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sisvita.data.models.FormularioCompletadoModelResponse

class SharedViewModel : ViewModel() {
    var selectedResult: FormularioCompletadoModelResponse? = null
}