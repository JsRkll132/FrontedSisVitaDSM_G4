package com.example.sisvita.data.models

data class RegisterModel(
    val apellido_materno: String,
    val apellido_paterno: String,
    val contrasena: String,
    val correo: String,
    val nombre_usuario: String,
    val nombres: String,
    val numero_celular: String,
    val tipo_usuario: String,
    val ubigeo : String
)