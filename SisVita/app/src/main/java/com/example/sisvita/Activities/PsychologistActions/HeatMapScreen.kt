package com.example.sisvita.Activities.PsychologistActions


import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.sisvita.Activities.ViewModelsPackage.HeatMapSvViewModel
import com.example.sisvita.Activities.ViewModelsPackage.SharedViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng

@Composable
fun HeatmapScreen(navController: NavHostController? = null ,
        sharedViewModel: SharedViewModel
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val viewModel: HeatMapSvViewModel = viewModel()
    sharedViewModel.isInVigilance = false
    viewModel.getHeatMapService()
    val Heatdata = viewModel.results
    Log.d("HEATDATA tam arreglo",Heatdata?.data?.size.toString())
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize(),
        update = { mapView ->
            mapView.getMapAsync(OnMapReadyCallback { googleMap ->
                googleMap.uiSettings.isZoomControlsEnabled = true
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-12.046374, -77.042793), 10f))
                Heatdata?.data?.let {
                    val weightedLatLngs = it.map { heatmap ->
                        val intensity = when (heatmap.nivel_ansiedad) {
                            "MUY ALTA" -> 4
                            "ALTA" -> 3
                            "MODERADA" -> 2
                            "NORMAL" -> 1
                            else -> 1
                        }
                        WeightedLatLng(LatLng(heatmap.latitud, heatmap.longitud), intensity.toDouble())
                    }

                    val provider = HeatmapTileProvider.Builder()
                        .weightedData(weightedLatLngs)
                        .radius(50)
                        .opacity(1.0)
                        .build()

                    googleMap.addTileOverlay(TileOverlayOptions().tileProvider(provider))
                } ?: run {

                }

            })
        }
    )
}