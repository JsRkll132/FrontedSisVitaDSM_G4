package com.example.sisvita.Activities.ViewModelsPackage

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sisvita.data.RetrofitServiceFactory
import com.example.sisvita.data.models.FormularioCompletadoModelResponse
import com.example.sisvita.data.models.TotalUbigeosResponse
import kotlinx.coroutines.launch

class UbigeosViewModel : ViewModel() {
    var results by mutableStateOf<TotalUbigeosResponse?>(null)
        private set

    fun getUbigeos() {
        viewModelScope.launch {
            val service = RetrofitServiceFactory.makeRetrofitService()
            try {
                results = service.executeUbigeoService()

            } catch (e: Exception) {
                Log.e("FormularioViewModel", "Error: ${e.message}", e)

            }
        }
    }

}