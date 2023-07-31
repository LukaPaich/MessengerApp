package ge.lpaichadze.messengerapp.presentation.home.conversations

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import ge.lpaichadze.messengerapp.databinding.FragmentConversationsBinding
import ge.lpaichadze.messengerapp.presentation.BaseFragment
import ge.lpaichadze.messengerapp.presentation.conversation.ConversationActivity
import ge.lpaichadze.messengerapp.presentation.conversation.TO_USER_DATA
import ge.lpaichadze.messengerapp.presentation.home.OnScrollListener
import ge.lpaichadze.messengerapp.utils.DEBOUNCE_DELAY
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ConversationsFragment : BaseFragment() {

    private val viewModel: ConversationsViewModel by viewModels {
        ConversationsViewModel.getViewModelFactory(this.requireContext())
    }

    private lateinit var binding: FragmentConversationsBinding

    private lateinit var adapter: FullConversationAdapter

    private var onScrollListener: OnScrollListener? = null

    private var lastSearchJob: Job? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentConversationsBinding.inflate(inflater, container, false)
        setProgressBar(binding.progressBar)

        adapter = FullConversationAdapter {
            val intent = Intent(requireActivity(), ConversationActivity::class.java)
            intent.putExtra(TO_USER_DATA, it)
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                onScrollListener?.onScrolled(dx, dy)
            }
        })

        binding.searchTextField.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s == null || (s.isNotEmpty() && s.length < 3)) {
                    return
                }

                lastSearchJob?.cancel()
                launchSearch(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

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
                if (it.isEmpty()) {
                    binding.noUsersFoundLabel.visibility = View.VISIBLE
                } else {
                    binding.noUsersFoundLabel.visibility = View.GONE
                }
            }
        }


        // Only show progressbar for first load
        showProgressBar()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnScrollListener) {
            onScrollListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()

        if (context is OnScrollListener) {
            onScrollListener = null
        }
    }

    override fun onResume() {
        super.onResume()
        launchSearch()
        viewModel.listenToConversations(true)
    }


    override fun onPause() {
        super.onPause()
        viewModel.stopListening()
    }

    private fun launchSearch(query: String = "") {
        lastSearchJob = lifecycleScope.launch {
            delay(DEBOUNCE_DELAY)
            showProgressBar()
            viewModel.searchByNickname(query)
        }
    }
}