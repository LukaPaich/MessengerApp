package ge.lpaichadze.messengerapp.presentation.home.conversations

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.lpaichadze.messengerapp.persistence.ConversationRepository
import ge.lpaichadze.messengerapp.persistence.FireBaseConversationRepository
import ge.lpaichadze.messengerapp.persistence.model.FullConversationData
import kotlinx.coroutines.launch

class ConversationsViewModel(private val conversationsRepository: ConversationRepository) :
    ViewModel() {

    val liveErrorData: LiveData<String?> = conversationsRepository.liveErrorData

    val liveFullConversationsData: LiveData<List<FullConversationData>?> =
        conversationsRepository.liveFullConversationsData

    fun searchByNickname(query: String) {
        viewModelScope.launch {
            conversationsRepository.searchByNickname(query)
        }
    }

    fun listenToConversations(keepQueryOnUpdate: Boolean) {
        viewModelScope.launch {
            conversationsRepository.listenToConversations(keepQueryOnUpdate)
        }
    }

    fun stopListening() {
        conversationsRepository.stopListening()
    }

    companion object {
        fun getViewModelFactory(context: Context): ConversationsViewModelFactory {
            return ConversationsViewModelFactory(context.applicationContext)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class ConversationsViewModelFactory(val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ConversationsViewModel(
            FireBaseConversationRepository(context, Firebase.database, Firebase.auth)
        ) as T
    }
}