package com.example.sisvita.data.models

data class AuthResponseModel(
    val status: String,
    val token: String,
    val type_user:Int
)