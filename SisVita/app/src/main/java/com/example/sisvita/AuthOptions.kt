package com.example.sisvita

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.activity.ComponentActivity

// Dentro de la clase LoginActivity

fun saveToken(token: String,activity : Activity) {
    val sharedPreferences= activity.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = sharedPreferences.edit()
    editor.putString("auth_token", token)
    editor.apply()
}

private fun getToken(activity : Activity): String? {
    val sharedPreferences = activity.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("auth_token", null)
}