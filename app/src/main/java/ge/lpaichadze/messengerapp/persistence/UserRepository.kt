package ge.lpaichadze.messengerapp.persistence

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ge.lpaichadze.messengerapp.persistence.model.User
import ge.lpaichadze.messengerapp.utils.toEmail

interface UserRepository {

    val liveCurrentUserData: LiveData<User?>

    val liveErrorData: LiveData<String?>

    fun getCurrentUserByUid(uid: String)

    fun registerUser(nickName: String, password: String, occupation: String)

    fun loginUser(nickName: String, password: String)
}

class FireBaseUserRepository(db: FirebaseDatabase, private val auth: FirebaseAuth) :
    UserRepository {

    private val usersRef = db.getReference("Users")

    private val _liveCurrentUserData: MutableLiveData<User?> = MutableLiveData(null)
    override val liveCurrentUserData: LiveData<User?>
        get() = _liveCurrentUserData

    private val _liveErrorData: MutableLiveData<String?> = MutableLiveData(null)
    override val liveErrorData: LiveData<String?>
        get() = _liveErrorData

    init {
        if (auth.currentUser != null) {
            getCurrentUserByUid(auth.currentUser!!.uid)
        }
    }

    private fun insertUser(user: User) {
        user.uid?.let { uid ->
            usersRef.child(uid).setValue(user)
                .addOnSuccessListener {
                    _liveCurrentUserData.postValue(user)
                }
                .addOnFailureListener {
                    _liveErrorData.postValue(it.message ?: "DB Error")
                }
        }
    }

    override fun getCurrentUserByUid(uid: String) {
        usersRef.child(uid).get()
            .addOnSuccessListener {
                _liveCurrentUserData.postValue(it.getValue(User::class.java))
            }
            .addOnFailureListener {
                _liveErrorData.postValue(it.message ?: "Data Retrieval Error")
            }
    }

    override fun registerUser(nickName: String, password: String, occupation: String) {
        auth.createUserWithEmailAndPassword(nickName.toEmail(), password)
            .addOnSuccessListener {
                insertUser(User(auth.currentUser!!.uid, nickName, occupation))
            }
            .addOnFailureListener {
                _liveErrorData.postValue(it.message ?: "Auth Error")
            }
    }

    override fun loginUser(nickName: String, password: String) {
        auth.signInWithEmailAndPassword(nickName.toEmail(), password)
            .addOnSuccessListener {
                getCurrentUserByUid(auth.currentUser!!.uid)
            }
            .addOnFailureListener {
                _liveErrorData.postValue(it.message ?: "Auth Error")
            }
    }
}