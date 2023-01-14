package com.gematriga.scruffy.activity

import android.content.Intent
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

        binding.userName.text = currentUser

        if(auth.currentUser == null){

            startActivity(Intent(this,MainActivity::class.java))
            finish()

        }

        supportActionBar!!.title = ""

        val fragmentArrayList = ArrayList<Fragment>()

        fragmentArrayList.add(ChatsFragment())
        fragmentArrayList.add(StatusFragment())

        fragmentArrayList.add(CallFragment())

        val adapter = ViewPagerAdapter(this,supportFragmentManager,fragmentArrayList)

        binding.viewPager.adapter = adapter

        binding.tabs.setupWithViewPager(binding.viewPager)


    }

}