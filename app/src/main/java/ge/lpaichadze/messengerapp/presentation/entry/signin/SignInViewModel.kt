package ge.lpaichadze.messengerapp.presentation.entry.signin

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.lpaichadze.messengerapp.persistence.FireBaseUserRepository
import ge.lpaichadze.messengerapp.persistence.UserRepository
import ge.lpaichadze.messengerapp.persistence.model.User

class SignInViewModel(private val userRepository: UserRepository) : ViewModel() {

    val liveCurrentUserData: LiveData<User?> = userRepository.liveCurrentUserData

    val liveErrorData: LiveData<String?> = userRepository.liveErrorData

    fun attemptSignIn(nickName: String, password: String) {
        userRepository.loginUser(nickName, password)
    }

    companion object {
        fun getViewModelFactory(context: Context): SignInViewModelFactory {
            return SignInViewModelFactory(context.applicationContext)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class SignInViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignInViewModel(
            FireBaseUserRepository(context, Firebase.database, Firebase.auth)
        ) as T
    }
}