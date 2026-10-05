package com.piggeon.chat.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.piggeon.chat.R
import com.piggeon.chat.data.AppDatabase
import com.piggeon.chat.databinding.ActivityVerifyEmailBinding
import com.piggeon.chat.util.toast
import kotlinx.coroutines.launch

/**
 * Actividad 3: autenticación por correo.
 * El usuario introduce el código recibido; si es correcto, su correo queda verificado
 * y ya puede iniciar sesión.
 */
class VerifyEmailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_CODE = "extra_code"
    }

    private lateinit var binding: ActivityVerifyEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra(EXTRA_EMAIL)
        val code = intent.getStringExtra(EXTRA_CODE).orEmpty()
        if (email == null) {
            finish() // Sin correo no hay nada que verificar
            return
        }

        binding.tvInfo.text = getString(R.string.verify_info, email, code)
        binding.btnVerify.setOnClickListener {
            if (binding.etCode.text.toString().trim() == code) {
                markAsVerified(email)
            } else {
                toast(R.string.err_code_wrong)
            }
        }
    }

    private fun markAsVerified(email: String) {
        lifecycleScope.launch {
            val userDao = AppDatabase.get(this@VerifyEmailActivity).userDao()
            userDao.findByEmail(email)?.let { userDao.update(it.copy(emailVerified = true)) }
            toast(R.string.verify_ok)
            finish() // Vuelve al login, que es la pantalla de debajo en la pila
        }
    }
}
