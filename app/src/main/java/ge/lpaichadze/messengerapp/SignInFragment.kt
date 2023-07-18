package ge.lpaichadze.messengerapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ge.lpaichadze.messengerapp.databinding.FragmentSignInBinding

private const val TAG = "SignInFragment"

// TODO: Add progress bar
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
        val nickName = binding.nickNameTextField.editText!!.text.toString()
        val password = binding.passwordTextField.editText!!.text.toString()
        var error = false;

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

        auth.signInWithEmailAndPassword(nickName, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(activity, MessagingActivity::class.java))
                    activity.finish()

                    hideKeyboard(binding.root)
                    return@addOnCompleteListener
                }

                binding.errorText.visibility = View.VISIBLE;
                hideKeyboard(binding.root)
        }

    }

    private fun hideKeyboard(view: View) {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}