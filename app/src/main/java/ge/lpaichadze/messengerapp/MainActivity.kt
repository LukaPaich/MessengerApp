package ge.lpaichadze.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val authService = Firebase.auth

        val curUser = authService.currentUser
        if (authService.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            startActivity(Intent(this, MessagingActivity::class.java))
        }

        finish()
    }
}