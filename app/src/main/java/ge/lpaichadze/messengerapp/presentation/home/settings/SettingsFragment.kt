package ge.lpaichadze.messengerapp.presentation.home.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import ge.lpaichadze.messengerapp.R
import ge.lpaichadze.messengerapp.databinding.FragmentSettingsBinding
import ge.lpaichadze.messengerapp.persistence.model.User
import ge.lpaichadze.messengerapp.presentation.BaseFragment
import ge.lpaichadze.messengerapp.presentation.entry.LoginActivity
import ge.lpaichadze.messengerapp.utils.isValidNickname

class SettingsFragment : BaseFragment() {

    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModel.getViewModelFactory(this.requireContext())
    }

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        switchInput(false)
        setProgressBar(binding.progressBar)
        showProgressBar()

        binding.signOutButton.setOnClickListener {
            showProgressBar()
            viewModel.signOut()
            val activity = requireActivity()
            activity.startActivity(Intent(activity, LoginActivity::class.java))
            activity.finish()
        }

        binding.updateButton.setOnClickListener {
            val nickName = binding.nickNameEditText.text.toString()
            val occupation = binding.occupationEditText.text.toString()

            if (nickName.isEmpty()) {
                binding.nickNameTextField.error = getString(R.string.nickname_field_must_be_filled)
                return@setOnClickListener
            }

            if (!nickName.isValidNickname()) {
                binding.nickNameEditText.error = getString(R.string.nickname_chars_invalid)
                return@setOnClickListener
            }

            switchInput(false)
            showProgressBar()
            viewModel.update(nickName, occupation, "")
        }

        viewModel.liveErrorData.observe(this.viewLifecycleOwner) {
            if (it != null) {
                hideProgressBar()
                binding.settingsErrorText.visibility = View.VISIBLE
                binding.settingsErrorText.text = it
                switchInput(true)
            }
        }

        viewModel.liveCurrentUserData.observe(this.viewLifecycleOwner) {
            if (it != null) {
                hideProgressBar()
                updateData(it)
                switchInput(true)
            }
        }

        return binding.root
    }

    private fun switchInput(enabled: Boolean) {
        binding.imageView.isEnabled = enabled
        binding.nickNameEditText.isEnabled = enabled
        binding.occupationEditText.isEnabled = enabled
        binding.signOutButton.isEnabled = enabled
        binding.updateButton.isEnabled = enabled
    }

    private fun updateData(user: User) {
        binding.nickNameEditText.setText(user.nickName)
        binding.occupationEditText.setText(user.occupation)

        // TODO: Image setting
    }

}