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
import ge.lpaichadze.messengerapp.presentation.home.HomeActivity
import ge.lpaichadze.messengerapp.utils.isValidNickname


class SignUpFragment : BaseFragment() {

    private val viewModel: SignUpViewModel by viewModels {
        SignUpViewModel.getViewModelFactory(this.requireContext())
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
                startActivity(Intent(activity, HomeActivity::class.java))
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
            val nickName = binding.nickNameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()
            val occupation = binding.occupationEditText.text.toString().trim()

            if (validateInput(nickName, password)) {
                showProgressBar()
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
        } else if (!nickName.isValidNickname()) {
            binding.nickNameEditText.error = getString(R.string.nickname_chars_invalid)
            success = false
        } else if (nickName.length < 3 || nickName.length > 20) {
            binding.nickNameEditText.error = getString(R.string.nickname_not_in_range)
            success = false
        }

        if (password.isEmpty()) {
            binding.passwordTextField.error = getString(R.string.password_field_must_be_filled)
            success = false
        } else if (password.length < 6) {
            binding.passwordTextField.error = getString(R.string.password_must_be_atleast_six_long)
            success = false
        }

        return success
    }


}