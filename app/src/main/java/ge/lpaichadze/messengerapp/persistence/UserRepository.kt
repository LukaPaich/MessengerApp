package ge.lpaichadze.messengerapp.persistence

import com.google.firebase.database.FirebaseDatabase
import ge.lpaichadze.messengerapp.persistence.model.User

interface UserRepository {

    fun getUserById(uid: String): User

    fun insertUser(user: User)
}

class FireBaseUserRepository(firebaseDatabase: FirebaseDatabase) : UserRepository {

    private val usersRef = firebaseDatabase.getReference("Users")

    override fun getUserById(uid: String): User {
        TODO("Not yet implemented")
    }

    override fun insertUser(user: User) {
        user.uid?.let { uid ->
            usersRef.child(uid).setValue(user)
        }
    }
}