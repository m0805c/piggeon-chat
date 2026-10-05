package com.piggeon.chat.data

import android.content.Context

/**
 * Sesión del usuario guardada en SharedPreferences.
 * Así el usuario sigue logueado aunque cierre la app, hasta que cierre sesión.
 */
object SessionManager {

    private const val PREFS_NAME = "piggeon_session"
    private const val KEY_EMAIL = "logged_email"

    fun saveLogin(context: Context, email: String) {
        prefs(context).edit().putString(KEY_EMAIL, email).apply()
    }

    /** Devuelve el correo del usuario logueado, o null si no hay sesión. */
    fun currentEmail(context: Context): String? = prefs(context).getString(KEY_EMAIL, null)

    fun logout(context: Context) {
        prefs(context).edit().remove(KEY_EMAIL).apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
