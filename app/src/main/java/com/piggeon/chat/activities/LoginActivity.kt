package com.piggeon.chat.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.piggeon.chat.R
import com.piggeon.chat.data.AppDatabase
import com.piggeon.chat.data.SessionManager
import com.piggeon.chat.databinding.ActivityLoginBinding
import com.piggeon.chat.util.hashPassword
import com.piggeon.chat.util.toast
import kotlinx.coroutines.launch

/**
 * Actividad 4: inicio de sesión.
 * Comprueba correo, contraseña y que el correo esté verificado.
 * Si ya hay sesión guardada, entra directamente a la pantalla de inicio.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (SessionManager.currentEmail(this) != null) {
            goToHome()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.btnGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        lifecycleScope.launch {
            val user = AppDatabase.get(this@LoginActivity).userDao().findByEmail(email)
            when {
                user == null || user.passwordHash != hashPassword(password) ->
                    toast(R.string.err_login_invalid)

                !user.emailVerified ->
                    toast(R.string.err_not_verified)

                else -> {
                    SessionManager.saveLogin(this@LoginActivity, user.email)
                    goToHome()
                }
            }
        }
    }

    private fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
