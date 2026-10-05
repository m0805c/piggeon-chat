package com.piggeon.chat.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import java.security.MessageDigest

/** Muestra un Toast con un texto de strings.xml (así se traduce solo). */
fun Context.toast(@StringRes resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

/** Convierte la contraseña en un hash SHA-256 para no guardarla en texto plano. */
fun hashPassword(password: String): String =
    MessageDigest.getInstance("SHA-256")
        .digest(password.toByteArray())
        .joinToString("") { "%02x".format(it) }
