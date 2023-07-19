package ge.lpaichadze.messengerapp.presentation

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.fragment.app.Fragment

open class BaseFragment: Fragment() {

    private lateinit var progressBar: ProgressBar

    fun setProgressBar(progressBar: ProgressBar) {
        this.progressBar = progressBar
    }

    fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    fun hideKeyboard(view: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}