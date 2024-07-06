package com.example.sisvita.data.models

data class DataFromHeatMapResponse(
    val apellido_materno: String,
    val apellido_paterno: String,
    val id_formulario: Int,
    val id_paciente: Int,
    val id_ultimo_formulario: Int,
    val id_usuario: Int,
    val latitud: Double,
    val longitud: Double,
    val nivel_ansiedad: String,
    val nombres: String,
    val ubigeo: String
)