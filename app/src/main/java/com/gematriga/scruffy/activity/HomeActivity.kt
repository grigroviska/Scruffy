package com.gematriga.scruffy.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.gematriga.scruffy.Fragments.CallFragment
import com.gematriga.scruffy.Fragments.ChatsFragment
import com.gematriga.scruffy.Fragments.StatusFragment
import com.gematriga.scruffy.adapter.ViewPagerAdapter
import com.gematriga.scruffy.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth


class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser?.displayName.toString()

        val email = intent.getStringExtra("email")

        binding.userName.text = currentUser

        supportActionBar!!.title = ""

        val fragmentArrayList = ArrayList<Fragment>()

        fragmentArrayList.add(ChatsFragment())
        fragmentArrayList.add(StatusFragment())
        fragmentArrayList.add(CallFragment())

        val adapter = ViewPagerAdapter(this,supportFragmentManager,fragmentArrayList)

        binding.viewPager.adapter = adapter

        binding.tabs.setupWithViewPager(binding.viewPager)


        /*val tabLayout : TabLayout = findViewById(R.id.tabs)
        val viewPager : ViewPager = findViewById(R.id.view_pager)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addFragment(ChatsFragment(),"Chats")
        viewPagerAdapter.addFragment(SettingsFragment(), "Settings")

        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)*/

    }

    /*internal class ViewPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager){

        private val fragments : ArrayList<Fragment>
        private val titles : ArrayList<String>

        init {

            fragments = ArrayList<Fragment>()
            titles = ArrayList<String>()

        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        fun addFragment(fragment: Fragment, title: String){

            fragments.add(fragment)
            titles.add(title)

        }

        override fun getPageTitle(i: Int): CharSequence {
            return titles[i]
        }


    }*/
}