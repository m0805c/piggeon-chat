package com.piggeon.chat.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.piggeon.chat.R
import com.piggeon.chat.data.AppDatabase
import com.piggeon.chat.databinding.ActivityRegisterBinding
import com.piggeon.chat.models.User
import com.piggeon.chat.util.hashPassword
import com.piggeon.chat.util.toast
import kotlinx.coroutines.launch

/**
 * Actividades 1 y 2: creación de usuario y confirmación de contraseña.
 * Valida el correo y que las dos contraseñas coincidan, guarda el usuario
 * y pasa a la pantalla de verificación de correo.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener { attemptRegister() }
    }

    private fun attemptRegister() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirm = binding.etConfirmPassword.text.toString()

        // Validaciones antes de tocar la base de datos
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = getString(R.string.err_invalid_email)
            return
        }
        if (password.length < 6) {
            binding.etPassword.error = getString(R.string.err_password_short)
            return
        }
        if (password != confirm) {
            binding.etConfirmPassword.error = getString(R.string.err_password_mismatch)
            return
        }

        lifecycleScope.launch {
            val userDao = AppDatabase.get(this@RegisterActivity).userDao()

            if (userDao.findByEmail(email) != null) {
                toast(R.string.err_email_exists)
                return@launch
            }

            // Actividad 6: el primer usuario registrado será administrador
            val role = if (userDao.count() == 0) User.ROLE_ADMIN else User.ROLE_USER
            userDao.insert(User(email = email, passwordHash = hashPassword(password), role = role))

            // Código de verificación. Sin Firebase no se envía por correo: se muestra en pantalla.
            val code = (1000..9999).random().toString()
            startActivity(
                Intent(this@RegisterActivity, VerifyEmailActivity::class.java)
                    .putExtra(VerifyEmailActivity.EXTRA_EMAIL, email)
                    .putExtra(VerifyEmailActivity.EXTRA_CODE, code)
            )
            finish()
        }
    }
}
