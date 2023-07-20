package ge.lpaichadze.messengerapp.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ge.lpaichadze.messengerapp.R
import ge.lpaichadze.messengerapp.presentation.entry.LoginActivity
import ge.lpaichadze.messengerapp.presentation.home.HomeActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val authService = Firebase.auth

        val curUser = authService.currentUser
        if (authService.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        finish()
    }
}