package com.piggeon.chat.adapters

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.piggeon.chat.databinding.ItemConversationBinding
import com.piggeon.chat.models.Conversation

/**
 * Adaptador de la lista de chats (pantalla de inicio).
 * Muestra contacto, último mensaje, hora y el indicador de no leídos.
 */
class ConversationAdapter(
    private val onClick: (Conversation) -> Unit
) : ListAdapter<Conversation, ConversationAdapter.ConversationViewHolder>(DIFF_CALLBACK) {

    inner class ConversationViewHolder(private val binding: ItemConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(conversation: Conversation) {
            binding.tvName.text = conversation.contactName
            binding.tvLastMessage.text = conversation.lastMessage
            binding.tvTime.text = if (conversation.lastTimestamp > 0) {
                DateFormat.format("HH:mm", conversation.lastTimestamp)
            } else {
                ""
            }

            // Indicador de no leídos: solo visible si hay mensajes sin abrir
            binding.tvUnread.visibility = if (conversation.unreadCount > 0) View.VISIBLE else View.GONE
            binding.tvUnread.text = conversation.unreadCount.toString()

            binding.root.setOnClickListener { onClick(conversation) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder =
        ConversationViewHolder(
            ItemConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Conversation>() {
            override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation) = oldItem == newItem
        }
    }
}
