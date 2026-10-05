package com.piggeon.chat.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Conversación con un contacto (tabla "conversations").
 * Guarda el resumen que se muestra en la lista de chats: último mensaje y no leídos.
 */
@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contactName: String,
    val lastMessage: String = "",
    val lastTimestamp: Long = 0,              // Milisegundos desde 1970; 0 = sin mensajes
    val unreadCount: Int = 0                  // Mensajes recibidos que aún no se han abierto
)
