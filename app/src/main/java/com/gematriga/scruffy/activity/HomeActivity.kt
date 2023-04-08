package com.gematriga.scruffy.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.gematriga.scruffy.R
import com.gematriga.scruffy.adapter.ChatAdapter
import com.gematriga.scruffy.databinding.ActivityHomeBinding
import com.gematriga.scruffy.model.UserModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.nav_header.*
import java.util.*
import kotlin.system.exitProcess


class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var dReference : DatabaseReference
    private var database : FirebaseDatabase? = null
    private lateinit var toggle : ActionBarDrawerToggle
    lateinit var userList : ArrayList<UserModel>


    private var url : String? = null
    private var backgroundUrl : String? = null
    private var nickName : String? = null
    private var phoneNumber : String? = null
    private var currentId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.materialToolbar)

        auth = FirebaseAuth.getInstance()
        dReference = Firebase.database.reference
        currentId = auth.currentUser?.uid.toString()
        database = FirebaseDatabase.getInstance()
        userList = ArrayList()

        checkData()

        //Querying the status of the dark mode
        val appSettingPrefs : SharedPreferences = getSharedPreferences("AppSettingPrefs",0)
        val isNightModeOn : Boolean = appSettingPrefs.getBoolean("NightMode",false)

        if (isNightModeOn){

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)


        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        }


        //Commands that evaluate online status
        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Online")

        //Slidable menu
        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = binding.navView

        toggle = ActionBarDrawerToggle(this,binding.drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener {

            when(it.itemId){

                //R.id.nav_home -> replaceFragment(ChatsFragment())
                R.id.nav_settings -> startGo(SettingsActivity())
                R.id.nav_profile -> startGo(UpdateProfile())
                R.id.nav_about -> startGo(AboutActivity())
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

        checkDataRecycler()

    }

    override fun onBackPressed() {
        if (isTaskRoot) {
            AlertDialog.Builder(this@HomeActivity)
                .setTitle("Exit")
                .setMessage("Do you want to exit the application?")
                .setPositiveButton("Yes") { _, _ ->
                    finishAffinity()
                    exitProcess(0)
                }
                .setNegativeButton("No", null)
                .show()
        } else {
            finishAffinity()
            exitProcess(0)
        }
    }

    //User search box codes
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu, menu)
        val item = menu?.findItem(R.id.search)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                database!!.reference.child("users")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            userList.clear()
                            for (snapshot1 in snapshot.children){

                                val user = snapshot1.getValue(UserModel::class.java)
                                if(user!!.uid !=FirebaseAuth.getInstance().uid && user.name!!.lowercase(
                                        Locale.getDefault()
                                    )
                                        .contains(newText.toString())){
                                        userList.add(user)
                                }

                            }
                            binding.userListRecyclerView.adapter = ChatAdapter(this@HomeActivity , userList)


                        }

                        override fun onCancelled(error: DatabaseError) {
                            userList.clear()
                            checkDataRecycler()
                        }
                    })

                return false

            }

        })

        return super.onCreateOptionsMenu(menu)
    }
    //Checking Latest Data
    private fun checkDataRecycler(){

        database!!.reference.child("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    userList.clear()
                    for (snapshot1 in snapshot.children){

                        val user = snapshot1.getValue(UserModel::class.java)
                        if (user!!.uid !=FirebaseAuth.getInstance().uid){

                            userList.add(user)

                        }

                    }

                    binding.userListRecyclerView.adapter = ChatAdapter(this@HomeActivity , userList)


                }

                override fun onCancelled(error: DatabaseError) {

                }
            })


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)){

            return true

        }

        return super.onOptionsItemSelected(item)
    }

    //Lines leading to activities
    private fun startGo(activity: Activity) {

        val goToPage = Intent(this@HomeActivity,activity::class.java)
        startActivity(goToPage)
        binding.drawerLayout.closeDrawer(GravityCompat.START)


    }
    /*
    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
        fragmentTransaction.commit()
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }*/

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

        try {
            dReference = FirebaseDatabase.getInstance().getReference("users")
            dReference.child(currentId!!).get()
                .addOnSuccessListener {
                    url = it.child("imageUrl").value.toString()
                    nickName = it.child("name").value.toString()
                    phoneNumber = it.child("number").value.toString()
                    backgroundUrl = it.child("backgroundUrl").value.toString()

                    Glide.with(applicationContext).load(url).into(nickPhoto)

                    user_name.text = nickName
                    phoneOrMail.text = phoneNumber

                    cover()

                    nickPhoto.setOnClickListener {

                        startActivity(Intent(this,UpdateProfile::class.java))

                    }

                }.addOnFailureListener {
                    Log.e("firebase", "Error getting data", it)
                }
        }catch (e : Exception){

            Toast.makeText(this, e.localizedMessage?.plus(e.message), Toast.LENGTH_LONG).show()
            println(e.localizedMessage?.plus(e.message))

        }
    }

    private fun cover(){

        val coverPreferences = getSharedPreferences("CoverPref", 0)
        val coverGet = coverPreferences.getString("cover","blueSky")

        try {

            when(coverGet){

                "blueSky" -> Glide.with(applicationContext).load(R.drawable.default_cover).into(gifImageView2)

                "cityCenter" -> Glide.with(applicationContext).load(R.drawable.citycenter_cover).into(gifImageView2)

                "rainyDay" -> Glide.with(applicationContext).load(R.drawable.rainy_cover).into(gifImageView2)

                "computerStore" -> Glide.with(applicationContext).load(R.drawable.computerstore).into(gifImageView2)

                "cafeMood" -> Glide.with(applicationContext).load(R.drawable.cafemood).into(gifImageView2)

            }

        }catch (e : Exception){

            Toast.makeText(this@HomeActivity, e.localizedMessage, Toast.LENGTH_LONG).show()

        }

    }

    //Commands that evaluate online status

    override fun onRestart() {
        super.onRestart()
        super.onStart()


        cover()

        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Online")
    }

    override fun onStart() {
        super.onStart()

        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Online")
    }
    override fun onResume() {
        super.onResume()

        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Online")

    }

    override fun onDestroy() {
        super.onDestroy()

        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Offline")

    }

}