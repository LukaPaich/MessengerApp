package ge.lpaichadze.messengerapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ge.lpaichadze.messengerapp.databinding.FragmentSignInBinding
import ge.lpaichadze.messengerapp.utils.toEmail

class SignInFragment : BaseFragment() {

    private lateinit var binding: FragmentSignInBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        this.setProgressBar(progressBar = binding.progressBar)
        auth = Firebase.auth


        with(binding) {
            signInButton.setOnClickListener {
                attemptLogin()
            }

            signUpButton.setOnClickListener {
                findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
            }
        }


        return binding.root
    }

    private fun attemptLogin() {
        hideKeyboard(binding.root)

        val nickName = binding.nickNameTextField.editText!!.text.toString()
        val password = binding.passwordTextField.editText!!.text.toString()
        var error = false

        if (nickName.isEmpty()) {
            binding.nickNameTextField.error = getString(R.string.nickname_field_must_be_filled)
            error = true
        }

        if (password.isEmpty()) {
            binding.passwordTextField.error = getString(R.string.password_field_must_be_filled)
            error = true
        }

        if (error) {
            return
        }

        val activity = requireActivity()

        showProgressBar()

        auth.signInWithEmailAndPassword(nickName.toEmail(), password)
            .addOnSuccessListener(activity) {
                startActivity(Intent(activity, MessagingActivity::class.java))
                activity.finish()
            }
            .addOnFailureListener(activity) {
                binding.errorText.visibility = View.VISIBLE
                val errorMessage = it.localizedMessage

                if (errorMessage != null) {
                    binding.errorText.text = errorMessage
                } else {
                    binding.errorText.text = getString(R.string.authentication_failed)
                }
            }
            .addOnCompleteListener {
                hideProgressBar()
            }

    }
}