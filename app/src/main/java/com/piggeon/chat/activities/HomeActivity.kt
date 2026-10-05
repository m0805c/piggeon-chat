package com.piggeon.chat.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.piggeon.chat.R
import com.piggeon.chat.adapters.ConversationAdapter
import com.piggeon.chat.data.AppDatabase
import com.piggeon.chat.data.ChatRepository
import com.piggeon.chat.data.SessionManager
import com.piggeon.chat.databinding.ActivityHomeBinding
import com.piggeon.chat.models.Conversation
import com.piggeon.chat.models.User
import com.piggeon.chat.util.toast
import kotlinx.coroutines.launch

/**
 * Pantalla principal: lista de chats con indicador de no leídos.
 * Desde aquí se abre un chat, se inicia una videollamada y se muestra el rol del usuario.
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var chatRepository: ChatRepository
    private var contacts: List<Conversation> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sin sesión no se puede estar aquí
        val email = SessionManager.currentEmail(this)
        if (email == null) {
            goToLogin()
            return
        }

        val db = AppDatabase.get(this)
        chatRepository = ChatRepository(db)

        // Lista de chats (RecyclerView)
        val adapter = ConversationAdapter { conversation ->
            startActivity(
                Intent(this, ChatActivity::class.java)
                    .putExtra(ChatActivity.EXTRA_CONVERSATION_ID, conversation.id)
            )
        }
        binding.rvConversations.layoutManager = LinearLayoutManager(this)
        binding.rvConversations.adapter = adapter

        // Se actualiza sola cada vez que cambian las conversaciones
        lifecycleScope.launch {
            chatRepository.conversations().collect { list ->
                contacts = list
                adapter.submitList(list)
            }
        }

        // Actividad 6: mostrar el rol del usuario
        lifecycleScope.launch {
            val user = db.userDao().findByEmail(email)
            val isAdmin = user?.role == User.ROLE_ADMIN
            binding.tvRole.text = getString(
                R.string.role_label,
                getString(if (isAdmin) R.string.role_admin else R.string.role_user)
            )
            if (isAdmin) {
                binding.tvAdminInfo.visibility = View.VISIBLE
                binding.tvAdminInfo.text = getString(R.string.admin_only, db.userDao().count())
            }
        }

        binding.btnVideoCall.setOnClickListener { pickContactToCall() }
        binding.btnLogout.setOnClickListener {
            SessionManager.logout(this)
            goToLogin()
        }
    }

    /** Muestra la lista de contactos para elegir a quién llamar. */
    private fun pickContactToCall() {
        if (contacts.isEmpty()) {
            toast(R.string.no_contacts)
            return
        }
        val names = contacts.map { it.contactName }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle(R.string.call_pick_title)
            .setItems(names) { _, which ->
                startActivity(
                    Intent(this, VideoCallActivity::class.java)
                        .putExtra(VideoCallActivity.EXTRA_CONTACT_NAME, names[which])
                )
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun goToLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }
}
