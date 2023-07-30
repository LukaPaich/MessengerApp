package ge.lpaichadze.messengerapp.presentation.home.conversations

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import ge.lpaichadze.messengerapp.R
import ge.lpaichadze.messengerapp.databinding.FragmentConversationsBinding
import ge.lpaichadze.messengerapp.presentation.BaseFragment
import ge.lpaichadze.messengerapp.presentation.conversation.ConversationActivity
import ge.lpaichadze.messengerapp.presentation.conversation.TO_USER_DATA

class ConversationsFragment : BaseFragment() {

    private val viewModel: ConversationsViewModel by viewModels {
        ConversationsViewModel.getViewModelFactory(this.requireContext())
    }

    private lateinit var binding: FragmentConversationsBinding

    private lateinit var adapter: FullConversationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentConversationsBinding.inflate(inflater, container, false)
        setProgressBar(binding.progressBar)

        adapter = FullConversationAdapter {
            val intent = Intent(requireActivity(), ConversationActivity::class.java)
            intent.putExtra(TO_USER_DATA, it)
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter

        viewModel.liveErrorData.observe(viewLifecycleOwner) {
            it?.let {
                hideProgressBar()
                Toast.makeText(this.requireActivity(), it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.liveFullConversationsData.observe(viewLifecycleOwner) {
            it?.let {
                hideProgressBar()
                adapter.conversations = it
            }
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showProgressBar()
        viewModel.searchByNickname("")
        viewModel.listenToConversations(true)
    }


    override fun onPause() {
        super.onPause()
        viewModel.stopListening()
    }
}