package com.example.simpletodolist.data.local

import android.content.Context

/*
 * Gestor de sesión local.
 *
 * Guarda en SharedPreferences los datos del usuario que inició sesión
 * (id y nombre). Esto permite que la app "recuerde" al usuario aunque
 * cierre y vuelva a abrir la aplicación.
 *
 * Forma parte de la capa "data/local" porque almacena datos en el
 * dispositivo, sin pasar por la red.
 *
 * En una app real se guardaría un token JWT en lugar del id directamente.
 */
class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSession(userId: String, userName: String) {
        prefs.edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, userName)
            .apply()
    }

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)

    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    fun isLoggedIn(): Boolean = getUserId() != null

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "session_prefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
    }
}
