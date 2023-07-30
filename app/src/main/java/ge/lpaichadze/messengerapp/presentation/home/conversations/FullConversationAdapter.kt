package ge.lpaichadze.messengerapp.presentation.home.conversations

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ge.lpaichadze.messengerapp.R
import ge.lpaichadze.messengerapp.databinding.FullConversationBinding
import ge.lpaichadze.messengerapp.persistence.model.FullConversationData
import ge.lpaichadze.messengerapp.persistence.model.User
import ge.lpaichadze.messengerapp.utils.toFormatted
import java.time.Instant

typealias OnConversationClicked = (user: User) -> Unit

private const val NULL_TIME = "--"

class FullConversationAdapter(private val onConversationClicked: OnConversationClicked) :
    RecyclerView.Adapter<FullConversationAdapter.ViewHolder>() {

    var conversations: List<FullConversationData>? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            this.notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FullConversationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindFullConversationData(conversations!![position])
    }

    override fun getItemCount(): Int {
        return conversations?.size ?: 0
    }

    inner class ViewHolder(private val binding: FullConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindFullConversationData(data: FullConversationData) {
            binding.messageContent.text = data.conversation.content
            binding.nickName.text = data.user.nickName

            if (data.conversation.time != null) {
                binding.timestamp.text =
                    Instant.ofEpochMilli(data.conversation.time).toFormatted()
            } else {
                binding.timestamp.text = NULL_TIME
            }

            if (data.user.imgUri == null) {
                binding.profileIcon.setImageResource(R.drawable.avatar_image_placeholder)
            } else {
                Glide.with(binding.root)
                    .load(data.user.imgUri)
                    .into(binding.profileIcon)
            }

            binding.root.setOnClickListener { onConversationClicked(data.user) }
        }
    }
}