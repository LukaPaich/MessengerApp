package ge.lpaichadze.messengerapp.persistence

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import ge.lpaichadze.messengerapp.R
import ge.lpaichadze.messengerapp.persistence.model.Conversation
import ge.lpaichadze.messengerapp.persistence.model.FullConversationData
import ge.lpaichadze.messengerapp.persistence.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

interface ConversationRepository {

    val liveErrorData: LiveData<String?>

    val liveFullConversationsData: LiveData<List<FullConversationData>?>

    suspend fun searchByNickname(query: String)

    suspend fun listenToConversations(keepQueryOnUpdate: Boolean)

    fun stopListening()
}

private const val TAG = "ConversationRepository"

class FireBaseConversationRepository(
    private val context: Context,
    db: FirebaseDatabase,
    private val auth: FirebaseAuth
) : ConversationRepository {

    private val _liveErrorData: MutableLiveData<String?> = MutableLiveData(null)

    override val liveErrorData: LiveData<String?>
        get() = _liveErrorData

    private val _liveFullConversationsData: MutableLiveData<List<FullConversationData>?> =
        MutableLiveData(null)

    override val liveFullConversationsData: LiveData<List<FullConversationData>?>
        get() = _liveFullConversationsData

    private val conversations = db.getReference("Conversations")

    private val users = db.getReference("Users")

    private var lastQuery = ""

    private var childEventListener: ChildEventListener? = null

    override suspend fun searchByNickname(query: String) {
        auth.uid?.let { uid ->
            lastQuery = query
            try {
                val conversations = conversations
                    .child(uid)
                    .orderByChild("time")
                    .get()
                    .await()

                val fullConversations = conversations.children.asFlow()
                    .map { it.getValue(Conversation::class.java)!! }
                    .map {
                        FullConversationData(
                            it,
                            users.child(it.withUid!!).get().await().getValue(User::class.java)!!
                        )
                    }
                    .filter { it.user.nickName?.startsWith(query) ?: false }
                    .toList()
                    .reversed()

                _liveFullConversationsData.postValue(fullConversations)
            } catch (exception: Exception) {
                postError(exception)
            }
        }
    }

    override suspend fun listenToConversations(keepQueryOnUpdate: Boolean) {
        stopListening()
        auth.uid?.let { uid ->
            val currentScope = currentCoroutineContext()
            val childEventListener = object: ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    CoroutineScope(currentScope)
                        .launch {
                            searchByNickname(if (keepQueryOnUpdate) lastQuery else "")
                        }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    CoroutineScope(currentScope)
                        .launch {
                            searchByNickname(if (keepQueryOnUpdate) lastQuery else "")
                        }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {}
            }
            conversations
                .child(uid)
                .addChildEventListener(childEventListener)
        }
    }

    override fun stopListening() {
        auth.uid?.let {
            childEventListener?.let {
                conversations.removeEventListener(it)
            }
        }
    }

    private fun postError(
        exception: Exception,
        @StringRes defaultResId: Int = R.string.default_db_error
    ) {
        _liveErrorData.postValue(exception.message ?: context.getString(defaultResId))
        Log.e(TAG, "postError: Server Error", exception)
    }
}