package com.piggeon.chat.adapters

import android.text.format.DateFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.piggeon.chat.R
import com.piggeon.chat.databinding.ItemMessageBinding
import com.piggeon.chat.models.Message

/**
 * Adaptador de la lista de mensajes del chat.
 * Los mensajes propios van a la derecha y los recibidos a la izquierda.
 */
class ChatAdapter : ListAdapter<Message, ChatAdapter.MessageViewHolder>(DIFF_CALLBACK) {

    inner class MessageViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            val context = binding.root.context
            binding.tvText.text = message.text
            binding.tvTime.text = DateFormat.format("HH:mm", message.timestamp) // Timestamp del mensaje

            // Alineación y color según quién envió el mensaje
            val params = binding.bubble.layoutParams as FrameLayout.LayoutParams
            params.gravity = if (message.isMine) Gravity.END else Gravity.START
            binding.bubble.layoutParams = params
            binding.bubble.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    if (message.isMine) R.color.bubble_mine else R.color.bubble_other
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder =
        MessageViewHolder(ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        // DiffUtil calcula solo los cambios para animar y refrescar la lista eficientemente
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Message, newItem: Message) = oldItem == newItem
        }
    }
}
