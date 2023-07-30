package ge.lpaichadze.messengerapp.presentation.conversation

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import ge.lpaichadze.messengerapp.R
import ge.lpaichadze.messengerapp.databinding.ActivityConversationBinding
import ge.lpaichadze.messengerapp.persistence.model.User
import java.time.Instant

const val TO_USER_DATA = "TO_USER_DATA"

class ConversationActivity : AppCompatActivity() {

    private val viewModel: ConversationViewModel by viewModels {
        ConversationViewModel.getViewModelFactory(this)
    }

    private lateinit var binding: ActivityConversationBinding

    private lateinit var adapter: MessageAdapter

    private lateinit var userTo: User

    private lateinit var curUserUid: String

    private var fullRefresh: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConversationBinding.inflate(layoutInflater)
        curUserUid = viewModel.currentUserUid()!!
        adapter = MessageAdapter(curUserUid)
        binding.recyclerView.adapter = adapter

        userTo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(TO_USER_DATA, User::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(TO_USER_DATA) as User
        }

        binding.nickName.text = userTo.nickName
        binding.occupation.text = userTo.occupation
        binding.collapsedToolBar.nickName.text = userTo.nickName
        binding.collapsedToolBar.occupation.text = userTo.occupation

        binding.collapsedToolBar.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (userTo.imgUri == null) {
            binding.profileIcon.setImageResource(R.drawable.avatar_image_placeholder)
            binding.collapsedToolBar.profileIcon.setImageResource(R.drawable.avatar_image_placeholder)
        } else {
            Glide.with(this).load(userTo.imgUri).into(binding.profileIcon)
            Glide.with(this).load(userTo.imgUri).into(binding.collapsedToolBar.profileIcon)
        }

        binding.chatBox.sendButton.setOnClickListener {
            val msg = binding.chatBox.messageInputEditText.text.toString()
            binding.chatBox.messageInputEditText.text.clear()
            if (msg.isNotEmpty()) {
                viewModel.sendMessage(curUserUid, userTo.uid!!, msg, Instant.now())
            }
        }

        viewModel.liveMessageData.observe(this) {
            it?.let {
                if (fullRefresh) {
                    adapter.messages = it.toMutableList()
                    fullRefresh = false
                } else {
                    adapter.appendList(it)
                }
            }
        }

        fullRefresh = true
        viewModel.refreshMessages(curUserUid, userTo.uid!!)
        setContentView(binding.root)
    }
}