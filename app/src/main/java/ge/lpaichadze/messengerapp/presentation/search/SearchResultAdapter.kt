package ge.lpaichadze.messengerapp.presentation.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ge.lpaichadze.messengerapp.R
import ge.lpaichadze.messengerapp.databinding.SearchResultBinding
import ge.lpaichadze.messengerapp.persistence.model.User

class SearchResultAdapter : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    var results: List<User>? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            this.notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            SearchResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindUserData(results!![position])
    }

    override fun getItemCount(): Int {
        return results?.size ?: 0
    }

    inner class ViewHolder(private val binding: SearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindUserData(user: User) {

            binding.nickName.text = user.nickName
            binding.occupation.text = user.occupation

            if (user.imgUri != null) {
                Glide.with(binding.root)
                    .load(user.imgUri)
                    .into(binding.profileIcon)
            } else {
                binding.profileIcon.setImageResource(R.drawable.avatar_image_placeholder)
            }
        }
    }
}