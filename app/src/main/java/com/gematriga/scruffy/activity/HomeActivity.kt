package com.gematriga.scruffy.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.LiveFolders.INTENT
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.gematriga.scruffy.Fragments.ChatsFragment
import com.gematriga.scruffy.Fragments.SettingsFragment
import com.gematriga.scruffy.Fragments.StatusFragment
import com.gematriga.scruffy.R
import com.gematriga.scruffy.databinding.ActivityHomeBinding
import com.google.android.material.internal.NavigationMenuItemView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.nav_header.*


class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var dReference : DatabaseReference
    lateinit var toggle : ActionBarDrawerToggle

    var url : String? = null
    var nickName : String? = null
    var phoneNumber : String? = null
    var auId : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.materialToolbar)

        auth = FirebaseAuth.getInstance()
        dReference = Firebase.database.reference
        auId = auth.currentUser?.uid.toString()

        checkData()

        replaceFragment(ChatsFragment())

        var drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = binding.navView

        toggle = ActionBarDrawerToggle(this,binding.drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener {

            when(it.itemId){

                R.id.nav_home -> replaceFragment(ChatsFragment())
                R.id.nav_settings -> replaceFragment(SettingsFragment())
                R.id.nav_profile -> startGo(ProfileActivity())
                R.id.nav_github -> github()
                R.id.sign_out -> signOut()

            }
            true
        }

        binding.materialToolbar.setNavigationOnClickListener {

            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }

        }

        if(auth.currentUser == null){

            startActivity(Intent(this,MainActivity::class.java))
            finish()

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu, menu)
        val item = menu?.findItem(R.id.search)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {



                return false

            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)){

            return true

        }

        return super.onOptionsItemSelected(item)
    }

    private fun startGo(activity: Activity) {

        val intent = Intent(this@HomeActivity,activity::class.java)
        startActivity(intent)
        binding.drawerLayout.closeDrawer(GravityCompat.START)

    }

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
        fragmentTransaction.commit()
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun signOut(){

        auth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()

    }

    private fun github(){

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/grigroviska"))
        startActivity(i)

    }

    private fun checkData(){

        println(auId)
        try {
            dReference = FirebaseDatabase.getInstance().getReference("users")
            dReference.child(auId!!).get()
                .addOnSuccessListener {
                    url = it.child("imageUrl").value.toString()
                    nickName = it.child("name").value.toString()
                    phoneNumber = it.child("number").value.toString()

                    Glide.with(this).load(url).into(nickPhoto)

                    user_name.text = nickName
                    phoneOrMail.text = phoneNumber

                }.addOnFailureListener {
                    Log.e("firebase", "Error getting data", it)
                }
        }catch (e : Exception){

            Toast.makeText(this, e.localizedMessage?.plus(e.message), Toast.LENGTH_LONG).show()
            println(e.localizedMessage?.plus(e.message))

        }


    }

}