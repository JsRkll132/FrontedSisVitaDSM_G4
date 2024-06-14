package com.example.sisvita.data.models

data class FormularioEnvioModel(
    val formulario_id: Int,
    val paciente_id: Int,
    val respuestas: List<Respuesta>
)