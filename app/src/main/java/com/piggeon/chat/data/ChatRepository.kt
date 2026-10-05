package com.piggeon.chat.data

import com.piggeon.chat.models.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Capa de datos del chat. Las pantallas no tocan Room directamente, sino este repositorio.
 * Cuando se integre Firebase, aquí se cambiarán las llamadas locales por un listener en tiempo real.
 */
class ChatRepository(private val db: AppDatabase) {

    private val chatDao = db.chatDao()

    fun conversations() = chatDao.observeConversations()

    fun messages(conversationId: Long) = chatDao.observeMessages(conversationId)

    suspend fun getConversation(id: Long) = chatDao.getConversation(id)

    suspend fun markAsRead(id: Long) = chatDao.markAsRead(id)

    /** Guarda un mensaje propio y, de momento, simula que el contacto responde. */
    suspend fun sendMessage(conversationId: Long, text: String) {
        val now = System.currentTimeMillis()
        chatDao.insertMessage(Message(conversationId = conversationId, text = text, timestamp = now, isMine = true))
        chatDao.updateLastMessage(conversationId, text, now, incoming = false)
        simulateContactReply(conversationId)
    }

    /**
     * Simula una respuesta del contacto a los 2 segundos.
     * Usa un scope propio (no el de la pantalla) para que la respuesta llegue aunque
     * el usuario salga del chat; así aparece el indicador de no leídos.
     */
    private fun simulateContactReply(conversationId: Long) {
        appScope.launch {
            delay(2000)
            val reply = REPLIES.random()
            val now = System.currentTimeMillis()
            chatDao.insertMessage(Message(conversationId = conversationId, text = reply, timestamp = now, isMine = false))
            chatDao.updateLastMessage(conversationId, reply, now, incoming = true)
        }
    }

    companion object {
        private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        private val REPLIES = listOf(
            "¡Qué bien! 👍",
            "Te respondo en un momento",
            "Jajaja, buenísimo",
            "Perfecto, gracias",
            "¿Podemos hablar luego?"
        )
    }
}
