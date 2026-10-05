package com.piggeon.chat.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.piggeon.chat.models.Conversation
import com.piggeon.chat.models.Message
import com.piggeon.chat.models.User

/** Base de datos local de la app (Room). Se usa una sola instancia en toda la app. */
@Database(
    entities = [User::class, Conversation::class, Message::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /** Devuelve la base de datos, creándola si todavía no existe. */
        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "piggeon.db"
                )
                    .addCallback(SeedCallback())   // Datos de ejemplo al crear la BD
                    .build()
                    .also { INSTANCE = it }
            }
    }

    /** Se ejecuta solo la primera vez que se crea la base de datos: añade contactos de ejemplo. */
    private class SeedCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            listOf("Ana", "Luis", "Equipo Piggeon").forEach { name ->
                db.execSQL(
                    "INSERT INTO conversations (contactName, lastMessage, lastTimestamp, unreadCount) " +
                        "VALUES ('$name', '', 0, 0)"
                )
            }
        }
    }
}
