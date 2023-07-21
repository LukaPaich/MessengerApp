package ge.lpaichadze.messengerapp.presentation.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import ge.lpaichadze.messengerapp.R
import ge.lpaichadze.messengerapp.databinding.ActivityHomeBinding
import ge.lpaichadze.messengerapp.presentation.home.conversations.ConversationsFragment
import ge.lpaichadze.messengerapp.presentation.home.settings.SettingsFragment
class HomeActivity : AppCompatActivity() {


    private lateinit var activityHomeBinding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(activityHomeBinding.root)

        val conversationsFragment = ConversationsFragment()
        val settingsFragment = SettingsFragment()

        val fragments = listOf(conversationsFragment, settingsFragment)
        activityHomeBinding.viewPager.adapter = PagerAdapter(this, fragments)

        activityHomeBinding.viewPager.registerOnPageChangeCallback(object: OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when (position) {
                    0 -> activityHomeBinding.bottomNavBar.selectedItemId = R.id.home
                    1 -> activityHomeBinding.bottomNavBar.selectedItemId = R.id.settings
                }
            }
        })

        activityHomeBinding.bottomNavBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> activityHomeBinding.viewPager.currentItem = 0
                R.id.settings -> activityHomeBinding.viewPager.currentItem = 1
            }

            true
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
}