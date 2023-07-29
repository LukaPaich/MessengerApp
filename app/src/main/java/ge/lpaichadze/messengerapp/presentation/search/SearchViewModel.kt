package ge.lpaichadze.messengerapp.presentation.search

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import ge.lpaichadze.messengerapp.persistence.FireBaseUserRepository
import ge.lpaichadze.messengerapp.persistence.UserRepository
import ge.lpaichadze.messengerapp.persistence.model.User

class SearchViewModel(private val userRepository: UserRepository) : ViewModel() {

    val liveUsersData: LiveData<List<User>?> = userRepository.liveUsersData

    val liveErrorData: LiveData<String?> = userRepository.liveErrorData

    companion object {
        fun getViewModelFactory(context: Context): SearchViewModelFactory {
            return SearchViewModelFactory(context.applicationContext)
        }
    }

    fun searchUsers(query: String) {
        userRepository.searchUsers(query)
    }
}

@Suppress("UNCHECKED_CAST")
class SearchViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(
            FireBaseUserRepository(context, Firebase.database, Firebase.auth, Firebase.storage)
        ) as T
    }
}