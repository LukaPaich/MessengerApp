package ge.lpaichadze.messengerapp.persistence.model

import com.google.firebase.database.IgnoreExtraProperties
import java.time.Instant

@IgnoreExtraProperties
data class Message(
    val fromUid: String? = null,
    val toUid: String? = null,
    val content: String? = null,
    val time: Long? = null
)
