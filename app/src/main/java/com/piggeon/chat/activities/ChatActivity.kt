package com.piggeon.chat.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.piggeon.chat.adapters.ChatAdapter
import com.piggeon.chat.data.AppDatabase
import com.piggeon.chat.data.ChatRepository
import com.piggeon.chat.databinding.ActivityChatBinding
import kotlinx.coroutines.launch

/**
 * Actividad 7: chat en tiempo real (simulado en local) con una conversación.
 * Muestra los mensajes, permite escribir y enviar, y marca los mensajes como leídos.
 */
class ChatActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CONVERSATION_ID = "extra_conversation_id"
    }

    private lateinit var binding: ActivityChatBinding
    private lateinit var repository: ChatRepository
    private val adapter = ChatAdapter()
    private var conversationId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        conversationId = intent.getLongExtra(EXTRA_CONVERSATION_ID, -1L)
        if (conversationId == -1L) {
            finish() // Sin conversación no hay nada que mostrar
            return
        }

        repository = ChatRepository(AppDatabase.get(this))

        // Lista de mensajes: los nuevos aparecen abajo
        binding.rvMessages.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        binding.rvMessages.adapter = adapter

        // Nombre del contacto en la cabecera
        lifecycleScope.launch {
            repository.getConversation(conversationId)?.let {
                binding.tvContactName.text = it.contactName
            }
        }

        // Observa los mensajes: cada cambio actualiza la lista automáticamente
        lifecycleScope.launch {
            repository.messages(conversationId).collect { list ->
                adapter.submitList(list) {
                    if (list.isNotEmpty()) binding.rvMessages.scrollToPosition(list.size - 1)
                }
                repository.markAsRead(conversationId) // Al estar en el chat, todo queda leído
            }
        }

        binding.btnSend.setOnClickListener { sendCurrentMessage() }
    }

    private fun sendCurrentMessage() {
        val text = binding.etMessage.text.toString().trim()
        if (text.isEmpty()) return

        binding.etMessage.text.clear()
        lifecycleScope.launch { repository.sendMessage(conversationId, text) }
    }
}
