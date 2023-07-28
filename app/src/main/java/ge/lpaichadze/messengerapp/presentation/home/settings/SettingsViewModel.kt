package ge.lpaichadze.messengerapp.presentation.home.settings

import android.content.Context
import android.net.Uri
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

class SettingsViewModel(private val userRepository: UserRepository): ViewModel() {

    val liveCurrentUserData: LiveData<User?> = userRepository.liveCurrentUserData

    val liveErrorData: LiveData<String?> = userRepository.liveErrorData

    companion object {
        fun getViewModelFactory(context: Context): SettingsViewModelFactory {
            return SettingsViewModelFactory(context.applicationContext)
        }
    }

    fun update(nickName: String, email: String, occupation: String, imageUri: Uri? = null) {
        userRepository.updateCurrentUser(nickName, email, occupation, imageUri)
    }

    fun signOut() {
        userRepository.signOutCurrentUser()
    }
}

@Suppress("UNCHECKED_CAST")
class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(
            FireBaseUserRepository(context, Firebase.database, Firebase.auth, Firebase.storage)
        ) as T
    }
}