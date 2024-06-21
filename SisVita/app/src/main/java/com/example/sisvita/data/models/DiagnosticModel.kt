package com.example.sisvita.data.models

data class DiagnosticModel(
    val calificacion: Int,
    val completado_formulario_id: Int,
    val diagnostico: String,
    val formulario_id: Int,
    val paciente_id: Int,
    val psicologo_id: Int
)