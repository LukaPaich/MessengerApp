package ge.lpaichadze.messengerapp.persistence.model

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class User(
        val uid: String? = null,
        val nickName: String? = null,
        val email: String? = null,
        val occupation: String? = null,
        val imgUri: String? = null
): Serializable