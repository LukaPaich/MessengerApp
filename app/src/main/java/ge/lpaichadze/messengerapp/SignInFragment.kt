package ge.lpaichadze.messengerapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ge.lpaichadze.messengerapp.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
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
        auth.signInWithEmailAndPassword(nickName, password)
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

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun hideKeyboard(view: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}