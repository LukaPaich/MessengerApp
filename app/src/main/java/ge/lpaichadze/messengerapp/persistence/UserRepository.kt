package ge.lpaichadze.messengerapp.persistence

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import ge.lpaichadze.messengerapp.R
import ge.lpaichadze.messengerapp.persistence.model.User
import ge.lpaichadze.messengerapp.utils.isLocal
import ge.lpaichadze.messengerapp.utils.toEmail

interface UserRepository {

    val liveCurrentUserData: LiveData<User?>

    val liveErrorData: LiveData<String?>

    val liveUsersData: LiveData<List<User>?>

    fun getCurrentUserByUid(uid: String)

    fun registerUser(nickName: String, password: String, occupation: String)

    fun loginUser(nickName: String, password: String)

    fun signOutCurrentUser()

    fun updateCurrentUser(
        nickName: String,
        email: String,
        occupation: String,
        imageUri: Uri? = null
    )

    fun searchUsers(
        query: String
    )
}

private const val TAG = "UserRepository"

class FireBaseUserRepository(
    private val context: Context,
    db: FirebaseDatabase,
    private val auth: FirebaseAuth,
    private val files: FirebaseStorage
) :
    UserRepository {

    private val emailCounterRef = db.getReference("EmailCounter")

    private val usersRef = db.getReference("Users")

    private val _liveCurrentUserData: MutableLiveData<User?> = MutableLiveData(null)
    override val liveCurrentUserData: LiveData<User?>
        get() = _liveCurrentUserData

    private val _liveErrorData: MutableLiveData<String?> = MutableLiveData(null)
    override val liveErrorData: LiveData<String?>
        get() = _liveErrorData

    private val _liveUsersData: MutableLiveData<List<User>?> = MutableLiveData(null)
    override val liveUsersData: LiveData<List<User>?>
        get() = _liveUsersData

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
                    postError(it, R.string.default_db_error)
                }
        }
    }

    override fun getCurrentUserByUid(uid: String) {
        usersRef.child(uid).get()
            .addOnSuccessListener {
                _liveCurrentUserData.postValue(it.getValue(User::class.java))
            }
            .addOnFailureListener {
                postError(it, R.string.default_db_error)
            }
    }

    override fun registerUser(nickName: String, password: String, occupation: String) {
        usersRef.orderByChild("nickName")
            .equalTo(nickName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        _liveErrorData.postValue(context.getString(R.string.nickname_in_use))
                    } else {
                        insertNewUser(nickName, password, occupation)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _liveErrorData.postValue(error.message)
                }
            })
    }

    private fun insertNewUser(nickName: String, password: String, occupation: String) {
        emailCounterRef.get()
            .addOnSuccessListener { snapShot ->
                val emailId = snapShot.getValue(Int::class.java) ?: 0

                auth.createUserWithEmailAndPassword(nickName.toEmail(emailId), password)
                    .addOnSuccessListener {
                        emailCounterRef.setValue(emailId + 1)
                            .addOnSuccessListener {
                                insertUser(
                                    User(
                                        uid = auth.currentUser!!.uid,
                                        nickName = nickName,
                                        email = nickName.toEmail(emailId),
                                        occupation = occupation
                                    )
                                )
                            }
                            .addOnFailureListener {
                                postError(it, R.string.default_db_error)
                            }

                    }
                    .addOnFailureListener {
                        postError(it, R.string.default_db_error)
                    }

            }
            .addOnFailureListener {
                postError(it, R.string.default_db_error)
            }

    }

    override fun loginUser(nickName: String, password: String) {
        usersRef.orderByChild("nickName")
            .equalTo(nickName)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    _liveErrorData.postValue(context.getString(R.string.nickname_not_found))
                } else {
                    val userData = snapshot.children.first().getValue(User::class.java)
                    if (userData?.email == null) { // shouldn't happen
                        _liveErrorData.postValue(context.getString(R.string.default_db_error))
                        return@addOnSuccessListener
                    }

                    auth.signInWithEmailAndPassword(
                        userData.email, password
                    )
                        .addOnSuccessListener {
                            _liveCurrentUserData.postValue(userData)
                        }
                        .addOnFailureListener {
                            postError(it, R.string.default_auth_error)
                        }
                }
            }
            .addOnFailureListener {
                postError(it, R.string.default_db_error)
            }
    }

    override fun signOutCurrentUser() {
        auth.signOut()
        _liveCurrentUserData.postValue(null)
    }

    override fun updateCurrentUser(
        nickName: String,
        email: String,
        occupation: String,
        imageUri: Uri?
    ) {
        if (imageUri != null && imageUri.isLocal()) {
            val imageRef = files.getReference("icons/${auth.uid}")

            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl
                        .addOnSuccessListener {
                            insertUser(User(auth.uid, nickName, email, occupation, it.toString()))
                        }
                        .addOnFailureListener {
                            postError(it, R.string.default_cloud_storage_error)
                        }
                }
                .addOnFailureListener {
                    postError(it, R.string.default_cloud_storage_error)
                }
        } else {
            insertUser(User(auth.uid, nickName, email, occupation, imageUri?.toString()))
        }
    }

    override fun searchUsers(query: String) {
        usersRef
            .orderByChild("nickName")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .addOnSuccessListener {
                val list = it.children
                    .mapNotNull { userSnapshot -> userSnapshot.getValue(User::class.java) }

                _liveUsersData.postValue(list)
            }
            .addOnFailureListener {
                postError(it, R.string.default_db_error)
            }
    }

    private fun postError(exception: Exception, @StringRes defaultResId: Int) {
        Log.e(TAG, "Exception occured:", exception)
        _liveErrorData.postValue(exception.message ?: context.getString(defaultResId))
    }

}