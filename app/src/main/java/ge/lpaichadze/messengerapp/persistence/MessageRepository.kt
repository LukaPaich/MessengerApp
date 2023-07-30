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
import ge.lpaichadze.messengerapp.persistence.model.Message
import ge.lpaichadze.messengerapp.utils.generateIdentifier
import java.time.Instant

interface MessageRepository {

    val liveErrorData: LiveData<String?>

    val liveMessageData: LiveData<List<Message>?>

    fun getMessagesBetween(userUid: String, otherUserUid: String)

    fun listenConversationBetween(userUid: String, otherUserUid: String)

    fun stopListening()

    fun getCurrentUserUid(): String?

    fun sendMessage(fromUid: String, toUid: String, content: String, time: Instant)
}


private const val TAG = "MessageRepository"

class FireBaseMessageRepository(
    private val context: Context,
    db: FirebaseDatabase,
    private val auth: FirebaseAuth
) : MessageRepository {

    private val _liveErrorData: MutableLiveData<String?> = MutableLiveData(null)
    override val liveErrorData: LiveData<String?>
        get() = _liveErrorData

    private val _liveMessageData: MutableLiveData<List<Message>?> = MutableLiveData(null)
    override val liveMessageData: LiveData<List<Message>?>
        get() = _liveMessageData

    private val messages = db.getReference("Messages")

    private var conversationListener: ChildEventListener? = null

    private var listenerIdentifier: String? = null

    override fun getMessagesBetween(userUid: String, otherUserUid: String) {
        messages
            .child(generateIdentifier(userUid, otherUserUid))
            .orderByChild("time")
            .get()
            .addOnSuccessListener {
                _liveMessageData.postValue(
                    it.children.map { child -> child.getValue(Message::class.java) as Message }
                )
            }
            .addOnFailureListener {
                postError(it)
            }
    }

    override fun listenConversationBetween(userUid: String, otherUserUid: String) {
        stopListening()
        listenerIdentifier = generateIdentifier(userUid, otherUserUid)
        conversationListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                _liveMessageData.postValue(listOf(snapshot.getValue(Message::class.java)!!))
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        }

        messages
            .child(listenerIdentifier!!)
            .addChildEventListener(conversationListener!!)
    }

    override fun stopListening() {
        if (listenerIdentifier != null && conversationListener != null) {
            messages.child(listenerIdentifier!!).removeEventListener(conversationListener!!)
        }
    }

    override fun getCurrentUserUid(): String? {
        return auth.uid
    }


    override fun sendMessage(fromUid: String, toUid: String, content: String, time: Instant) {
        val message = Message(
            fromUid,
            toUid,
            content,
            time.toEpochMilli()
        )
        val identifierReference = messages.child(generateIdentifier(fromUid, toUid))

        identifierReference
            .push().key?.let {
                identifierReference.child(it).setValue(message)
                    .addOnFailureListener { exception ->
                        postError(exception)
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