package ge.lpaichadze.messengerapp.presentation.search


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import ge.lpaichadze.messengerapp.databinding.ActivitySearchBinding
import ge.lpaichadze.messengerapp.persistence.model.User
import ge.lpaichadze.messengerapp.presentation.conversation.ConversationActivity
import ge.lpaichadze.messengerapp.presentation.conversation.TO_USER_DATA
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val DEBOUNCE_DELAY = 300L

class SearchActivity : AppCompatActivity() {

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModel.getViewModelFactory(this)
    }

    private lateinit var binding: ActivitySearchBinding

    private lateinit var adapter: SearchResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)

        adapter = SearchResultAdapter {
            val intent = Intent(this, ConversationActivity::class.java)
            intent.putExtra(TO_USER_DATA, it)
            startActivity(intent)
        }
        binding.searchResults.adapter = adapter

        binding.backButton.setOnClickListener {
            this@SearchActivity.onBackPressedDispatcher.onBackPressed()
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {

            private var lastSearchJob: Job? = null

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s == null || s.length < 3) {
                    return
                }

                lastSearchJob?.cancel()
                lifecycleScope.launch {
                    delay(DEBOUNCE_DELAY)
                    hideSearchResults()
                    showProgressBar()
                    viewModel.searchUsers(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        viewModel.liveErrorData.observe(this) {
            hideProgressBar()
            if (it != null) {
                binding.errorText.visibility = View.VISIBLE
                binding.errorText.text = it
            } else {
                binding.errorText.visibility = View.GONE
            }
        }

        viewModel.liveUsersData.observe(this) {
            hideProgressBar()
            if (it != null) {
                showSearchResults(it)
            } else {
                hideSearchResults()
            }
        }

        setContentView(binding.root)
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideSearchResults() {
        binding.searchResults.visibility = View.GONE
        binding.noUsersFoundLabel.visibility = View.GONE
    }

    private fun showSearchResults(users: List<User>) {

        if (users.isEmpty()) {
            binding.noUsersFoundLabel.visibility = View.VISIBLE
            return
        }

        binding.searchResults.visibility = View.VISIBLE
        adapter.results = users
    }
}

