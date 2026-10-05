package com.piggeon.chat.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Mensaje dentro de una conversación (tabla "messages").
 * Si se borra la conversación, sus mensajes se borran también (CASCADE).
 */
@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = Conversation::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("conversationId")]
)
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isMine: Boolean                       // true = lo envié yo, false = lo recibí
)
