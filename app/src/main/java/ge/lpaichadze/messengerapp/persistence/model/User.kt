package ge.lpaichadze.messengerapp.persistence.model

import android.net.Uri
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
        val uid: String? = null,
        val nickName: String? = null,
        val email: String? = null,
        val occupation: String? = null,
        val imgUri: String? = null
)