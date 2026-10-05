package com.piggeon.chat.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.piggeon.chat.models.Conversation
import com.piggeon.chat.models.Message
import com.piggeon.chat.models.User
import kotlinx.coroutines.flow.Flow

/** Consultas sobre usuarios (registro, login y verificación). */
@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): User?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)
}

/** Consultas sobre conversaciones y mensajes. Los Flow se actualizan solos al cambiar la tabla. */
@Dao
interface ChatDao {

    // ----- Conversaciones -----

    @Query("SELECT * FROM conversations ORDER BY lastTimestamp DESC")
    fun observeConversations(): Flow<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE id = :id")
    suspend fun getConversation(id: Long): Conversation?

    @Query("UPDATE conversations SET unreadCount = 0 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    /** Actualiza el último mensaje. Si es recibido (incoming), suma un no leído. */
    @Query(
        """
        UPDATE conversations
        SET lastMessage = :text,
            lastTimestamp = :time,
            unreadCount = CASE WHEN :incoming THEN unreadCount + 1 ELSE unreadCount END
        WHERE id = :id
        """
    )
    suspend fun updateLastMessage(id: Long, text: String, time: Long, incoming: Boolean)

    // ----- Mensajes -----

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun observeMessages(conversationId: Long): Flow<List<Message>>

    @Insert
    suspend fun insertMessage(message: Message)
}
