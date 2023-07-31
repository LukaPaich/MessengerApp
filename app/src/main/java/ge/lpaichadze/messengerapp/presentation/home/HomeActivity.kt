package ge.lpaichadze.messengerapp.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import ge.lpaichadze.messengerapp.R
import ge.lpaichadze.messengerapp.databinding.ActivityHomeBinding
import ge.lpaichadze.messengerapp.presentation.home.conversations.ConversationsFragment
import ge.lpaichadze.messengerapp.presentation.home.settings.SettingsFragment
import ge.lpaichadze.messengerapp.presentation.search.SearchActivity

class HomeActivity : AppCompatActivity(), OnScrollListener {


    private lateinit var binding: ActivityHomeBinding

    private var totalScroll: Int = 0

    private var isBottomAppBarHidden: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val conversationsFragment = ConversationsFragment()
        val settingsFragment = SettingsFragment()

        val fragments = listOf(conversationsFragment, settingsFragment)
        binding.viewPager.adapter = PagerAdapter(this, fragments)

        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when (position) {
                    0 -> {
                        binding.bottomNavBar.selectedItemId = R.id.home
                        if (isBottomAppBarHidden) {
                            binding.bottomAppBar.performHide()
                        }
                    }
                    1 -> {
                        binding.bottomNavBar.selectedItemId = R.id.settings
                        if (isBottomAppBarHidden) {
                            binding.bottomAppBar.performShow()
                        }
                    }
                }
            }
        })

        binding.bottomNavBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    binding.viewPager.currentItem = 0
                    if (isBottomAppBarHidden) {
                        binding.bottomAppBar.performHide()
                    }
                }
                R.id.settings -> {
                    binding.viewPager.currentItem = 1
                    if (isBottomAppBarHidden) {
                        binding.bottomAppBar.performShow()
                    }
                }
            }

            true
        }

        binding.bottomAppBarFab.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }


    private class PagerAdapter(activity: FragmentActivity, private var fragments: List<Fragment>) :
        FragmentStateAdapter(activity) {

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }

    }

    override fun onScrolled(dx: Int, dy: Int) {
        if (dy > 0 && !isBottomAppBarHidden) {
            binding.bottomAppBar.performHide()
            isBottomAppBarHidden = true
        } else if (dy < 0) {
            binding.bottomAppBar.performShow()
            isBottomAppBarHidden = false
        }
    }
}

interface OnScrollListener {
    fun onScrolled(dx: Int, dy: Int)
}