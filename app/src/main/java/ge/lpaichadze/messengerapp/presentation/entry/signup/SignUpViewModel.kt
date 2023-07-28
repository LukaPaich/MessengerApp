package ge.lpaichadze.messengerapp.presentation.entry.signup

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

class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {

    val liveCurrentUserData: LiveData<User?> = userRepository.liveCurrentUserData

    val liveErrorData: LiveData<String?> = userRepository.liveErrorData

    fun attemptSignUp(nickName: String, password: String, occupation: String) {
        userRepository.registerUser(nickName, password, occupation)
    }

    companion object {
        fun getViewModelFactory(context: Context): SignUpViewModelFactory {
            return SignUpViewModelFactory(context.applicationContext)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class SignUpViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignUpViewModel(
                FireBaseUserRepository(context, Firebase.database, Firebase.auth, Firebase.storage)
        ) as T
    }
}