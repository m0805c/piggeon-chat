package com.piggeon.chat.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Usuario registrado. Se guarda en Room (tabla "users").
 * Actividades 1-3 y 6: registro, contraseña, verificación de correo y rol.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,            // El correo es el identificador único
    val passwordHash: String,                 // Nunca guardamos la contraseña en texto plano
    val role: String = ROLE_USER,             // "ADMIN" o "USER"
    val emailVerified: Boolean = false        // Pasa a true al introducir el código enviado
) {
    companion object {
        const val ROLE_ADMIN = "ADMIN"
        const val ROLE_USER = "USER"
    }
}
