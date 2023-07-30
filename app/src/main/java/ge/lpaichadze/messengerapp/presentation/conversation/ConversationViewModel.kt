package ge.lpaichadze.messengerapp.presentation.conversation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.lpaichadze.messengerapp.persistence.FireBaseMessageRepository
import ge.lpaichadze.messengerapp.persistence.MessageRepository
import ge.lpaichadze.messengerapp.persistence.model.Message
import java.time.Instant

class ConversationViewModel(private val messageRepository: MessageRepository): ViewModel() {

    val liveMessageData: LiveData<List<Message>?> = messageRepository.liveMessageData

    val liveErrorData: LiveData<String?> = messageRepository.liveErrorData
    fun currentUserUid(): String? {
        return messageRepository.getCurrentUserUid()
    }

    fun refreshMessages(userUid: String, otherUserUid: String) {
        messageRepository.getMessagesBetween(userUid, otherUserUid)
    }

    fun sendMessage(fromUid: String, toUid: String, content: String, time: Instant) {
        messageRepository.sendMessage(fromUid, toUid, content, time)
    }

    fun listenToMessages(userUid: String, otherUserUid: String) {
        messageRepository.listenConversationBetween(userUid, otherUserUid)
    }

    companion object {
        fun getViewModelFactory(context: Context): ConversationViewModelFactory {
            return ConversationViewModelFactory(context.applicationContext)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class ConversationViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ConversationViewModel(
            FireBaseMessageRepository(context, Firebase.database, Firebase.auth)
        ) as T
    }
}