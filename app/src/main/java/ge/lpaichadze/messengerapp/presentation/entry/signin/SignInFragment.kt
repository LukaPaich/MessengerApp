package ge.lpaichadze.messengerapp.presentation.entry.signin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ge.lpaichadze.messengerapp.R
import ge.lpaichadze.messengerapp.databinding.FragmentSignInBinding
import ge.lpaichadze.messengerapp.presentation.BaseFragment
import ge.lpaichadze.messengerapp.presentation.messaging.MessagingActivity

class SignInFragment : BaseFragment() {

    private val viewModel: SignInViewModel by viewModels {
        SignInViewModel.getViewModelFactory(this.requireContext())
    }

    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        this.setProgressBar(progressBar = binding.progressBar)

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

        showProgressBar()
        viewModel.attemptSignIn(nickName, password)
    }
}