package com.example.sisvita.Activities

import android.content.Context
import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
private const val PREFS_NAME = "com.example.sisvita.PREFS"
private const val PREF_TOKEN = "token"

// Dentro de la clase LoginActivity


fun getUserIdFromToken(token: String): Int? {
    val jwt = JWT(token)
    return jwt.getClaim("id_user").asInt()
}
fun getGlobalUserIdFromToken(token: String): Int? {
    val jwt = JWT(token)
    return jwt.getClaim("usuario_id").asInt()
}
fun saveToken(token: String, context: Context) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString(PREF_TOKEN, token)
        apply()
    }
}
fun getToken(context: Context): String? {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getString(PREF_TOKEN, null)
}

fun saveUserId(userId: Int, context: Context) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putInt("user_id", userId)
        apply()
    }
}
fun saveGobalUserId(usuario_id: Int, context: Context) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putInt("usuario_id", usuario_id)
        apply()
    }
}
fun clearToken(context: Context) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        remove(PREF_TOKEN)
        apply()
    }
}