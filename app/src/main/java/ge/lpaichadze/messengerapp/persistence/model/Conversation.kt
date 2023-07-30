package ge.lpaichadze.messengerapp.persistence.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Conversation(
    val withUid: String? = null,
    val content: String? = null,
    val time: Long? = null
)
