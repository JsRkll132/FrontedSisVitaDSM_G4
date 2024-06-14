package com.example.sisvita.data.models

data class Respuesta(
    val paciente_id: Int,
    val pregunta_id: Int,
    val puntuacion: Int,
    val respuesta: String
)