package com.example.sisvita.Activities.ViewModelsPackage

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sisvita.data.RetrofitServiceFactory
import com.example.sisvita.data.models.HeatMapResponseModel
import kotlinx.coroutines.launch

class HeatMapSvViewModel : ViewModel() {
    var results   by mutableStateOf<HeatMapResponseModel?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    fun getHeatMapService() {
        viewModelScope.launch {
            val service = RetrofitServiceFactory.makeRetrofitService()
            try {
                results = service.executeHeatMapService()
                isLoading = false
            } catch (e: Exception) {
                Log.e("HeatMapViewModel", "Error: ${e.message}", e)
                isLoading = false
                // Handle error
            }
        }
    }
}