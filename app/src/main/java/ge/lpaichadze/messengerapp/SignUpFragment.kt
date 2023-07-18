package ge.lpaichadze.messengerapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ge.lpaichadze.messengerapp.databinding.FragmentSignInBinding
import ge.lpaichadze.messengerapp.databinding.FragmentSignUpBinding


class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        auth = Firebase.auth

        binding.signUpButton.setOnClickListener {
            val nickName = binding.nickNameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val occupation = binding.occupationEditText.text.toString()

            if (validateInput(nickName, password)) {
                attemptSignUp(
                    binding.nickNameEditText.text.toString(),
                    binding.passwordEditText.text.toString(),
                    binding.occupationEditText.text.toString()
                )
            }
        }

        return binding.root
    }

    private fun validateInput(nickName: String, password: String): Boolean {
        var error = false

        if (nickName.isEmpty()) {
            binding.nickNameTextField.error = getString(R.string.nickname_field_must_be_filled)
            error = true
        }

        if (password.isEmpty()) {
            binding.passwordTextField.error = getString(R.string.password_field_must_be_filled)
            error = true
        }

        return error
    }

    private fun attemptSignUp(nickName: String, password: String, occupation: String) {

    }


}