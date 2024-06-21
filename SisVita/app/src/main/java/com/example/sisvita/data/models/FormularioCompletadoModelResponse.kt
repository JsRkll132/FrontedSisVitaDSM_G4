package com.example.sisvita.data.models

import java.util.Date

data class FormularioCompletadoModelResponse(
    val apellido_materno: String,
    val apellido_paterno: String,
    val completado_formulario_id: Int,
    val nombres: String,
    val suma_puntuacion: Int,
    val tipo_formulario: String,
    val fecha_completado :String,
    val usuario_id: Int,
    val paciente_id: Int,
    val formulario_id : Int
)