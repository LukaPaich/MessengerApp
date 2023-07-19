package ge.lpaichadze.messengerapp.presentation.entry.signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import ge.lpaichadze.messengerapp.R
import ge.lpaichadze.messengerapp.databinding.FragmentSignUpBinding
import ge.lpaichadze.messengerapp.presentation.BaseFragment
import ge.lpaichadze.messengerapp.presentation.messaging.MessagingActivity


class SignUpFragment : BaseFragment() {

    private val viewModel: SignUpViewModel by viewModels {
        SignUpViewModel.getViewModelFactory()
    }

    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        setProgressBar(binding.signUpProgressBar)

        viewModel.liveCurrentUserData.observe(this.viewLifecycleOwner) {
            if (it != null) {
                hideProgressBar()
                startActivity(Intent(activity, MessagingActivity::class.java))
                requireActivity().finish()
            }
        }

        viewModel.liveErrorData.observe(this.viewLifecycleOwner) {
            if (it != null) {
                hideProgressBar()
                binding.errorText.visibility = View.VISIBLE
                binding.errorText.text = it
            }
        }

        binding.signUpButton.setOnClickListener {
            val nickName = binding.nickNameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val occupation = binding.occupationEditText.text.toString()

            if (validateInput(nickName, password)) {
                viewModel.attemptSignUp(nickName, password, occupation)
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
        } else if (password.length < 6) {
            binding.passwordTextField.error = getString(R.string.password_must_be_atleast_six_long)
        }

        return success
    }

    private fun isNickNameValid(input: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9 ]+\$")
        return input.matches(regex)
    }


}