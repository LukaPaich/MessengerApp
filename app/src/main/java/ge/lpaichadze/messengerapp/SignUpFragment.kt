package ge.lpaichadze.messengerapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.lpaichadze.messengerapp.databinding.FragmentSignUpBinding
import ge.lpaichadze.messengerapp.persistence.FireBaseUserRepository
import ge.lpaichadze.messengerapp.persistence.UserRepository
import ge.lpaichadze.messengerapp.persistence.model.User
import ge.lpaichadze.messengerapp.utils.toEmail


class SignUpFragment : BaseFragment() {

    private lateinit var binding: FragmentSignUpBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var usersRepo: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        usersRepo = FireBaseUserRepository(Firebase.database)

        setProgressBar(binding.signUpProgressBar)

        binding.signUpButton.setOnClickListener {
            val nickName = binding.nickNameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val occupation = binding.occupationEditText.text.toString()

            if (validateInput(nickName, password)) {
                attemptSignUp(
                    nickName,
                    password,
                    occupation
                )
            }
        }

        return binding.root
    }

    private fun validateInput(nickName: String, password: String): Boolean {
        var success = true

        if (nickName.isEmpty()) {
            binding.nickNameTextField.error = getString(R.string.nickname_field_must_be_filled)
            success = false
        } else if (!isNickNameValid(nickName)) {
            binding.nickNameEditText.error = getString(R.string.nickname_chars_invalid)
        }

        if (password.isEmpty()) {
            binding.passwordTextField.error = getString(R.string.password_field_must_be_filled)
            success = false
        }

        return success
    }

    private fun isNickNameValid(input: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9 ]+\$")
        return input.matches(regex)
    }

    private fun attemptSignUp(nickName: String, password: String, occupation: String) {

        showProgressBar()
        auth.createUserWithEmailAndPassword(nickName.toEmail(), password)
            .addOnCompleteListener { hideProgressBar() }
            .addOnSuccessListener {
                usersRepo.insertUser(User(auth.currentUser!!.uid, nickName, occupation))
                val activity = requireActivity()

                activity.startActivity(Intent(activity, MessagingActivity::class.java))
                activity.finish()
            }
            .addOnFailureListener {
                binding.errorText.visibility = View.VISIBLE
                val errorMessage = it.localizedMessage

                if (errorMessage != null) {
                    binding.errorText.text = errorMessage
                } else {
                    binding.errorText.text = getString(R.string.signup_failed)
                }
            }
    }


}