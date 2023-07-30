package ge.lpaichadze.messengerapp.presentation.conversation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ge.lpaichadze.messengerapp.databinding.IncomingMessageBinding
import ge.lpaichadze.messengerapp.databinding.OutgoingMessageBinding
import ge.lpaichadze.messengerapp.persistence.model.Message
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val TIMESTAMP_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

const val INCOMING_MSG_VIEW_TYPE = 0

const val OUTGOING_MSG_VIEW_TYPE = 1

class MessageAdapter(private val currentUserUid: String) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    var messages: MutableList<Message> = mutableListOf()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun appendList(newMessages: List<Message>) {
        val idx = messages.size
        this.messages.addAll(newMessages)
        this.notifyItemRangeInserted(idx, newMessages.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == INCOMING_MSG_VIEW_TYPE) {
            return IncomingViewHolder(
                IncomingMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        return OutgoingViewHolder(
            OutgoingMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMessageData(messages[position])
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        if (messages[position].toUid == currentUserUid) {
            return INCOMING_MSG_VIEW_TYPE
        }
        return OUTGOING_MSG_VIEW_TYPE
    }

    abstract inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bindMessageData(message: Message)
    }

    inner class IncomingViewHolder(private val binding: IncomingMessageBinding) :
        ViewHolder(binding.root) {

        override fun bindMessageData(message: Message) {
            binding.messageContent.text = message.content
            binding.messageTimestamp.text =
                TIMESTAMP_FORMAT.format(
                    Instant.ofEpochMilli(message.time!!).atZone(ZoneId.systemDefault())
                )
        }
    }

    inner class OutgoingViewHolder(private val binding: OutgoingMessageBinding) :
        ViewHolder(binding.root) {

        override fun bindMessageData(message: Message) {
            binding.messageContent.text = message.content
            binding.messageTimestamp.text =
                TIMESTAMP_FORMAT.format(
                    Instant.ofEpochMilli(message.time!!).atZone(ZoneId.systemDefault())
                )
        }
    }
}