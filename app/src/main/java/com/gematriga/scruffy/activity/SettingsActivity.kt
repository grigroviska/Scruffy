package com.gematriga.scruffy.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.gematriga.scruffy.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.selectbackground.*
import java.util.*


open class SettingsActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingsBinding
    private var database : FirebaseDatabase? = null
    private lateinit var auth : FirebaseAuth

    private var currentId : String? = null
    private var selectedImg : Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        currentId = FirebaseAuth.getInstance().uid

        //Commands that evaluate online status
        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Online")

        binding.materialToolbar.setNavigationOnClickListener {

            onBackPressedDispatcher.onBackPressed()

        }
        //Querying the status of the dark mode
        val appSettingPrefs : SharedPreferences = getSharedPreferences("AppSettingPrefs",0)
        val sharedPrefsEdit : SharedPreferences.Editor = appSettingPrefs.edit()
        val isNightModeOn : Boolean = appSettingPrefs.getBoolean("NightMode",false)

        if (isNightModeOn){

            binding.nightModeSwitch.isChecked = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)


        }else{
            binding.nightModeSwitch.isSelected = false
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        }

        //Dark mode on/off
        binding.nightModeSwitch.setOnClickListener {

            if(isNightModeOn){

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefsEdit.putBoolean("NightMode", false)

            }else{

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefsEdit.putBoolean("NightMode", true)

            }
            sharedPrefsEdit.apply()
        }

        binding.coverMode.setOnClickListener {

            val goToCover = Intent(this@SettingsActivity, CoverActivity::class.java)
            startActivity(goToCover)

        }

        binding.selectBackgroundLayout.setOnClickListener {

            val imgUrIntent = Intent()
            imgUrIntent.action = Intent.ACTION_GET_CONTENT
            imgUrIntent.type = "image/*"
            startActivityForResult(imgUrIntent, 4)

        }

        binding.signOutLayout.setOnClickListener {

            val builder = AlertDialog.Builder(this@SettingsActivity)
            builder.setTitle("Are you sure?")
            builder.setMessage("Are you going out :(")
            builder.setPositiveButton("Yes") { dialog, which ->

                auth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()

            }
            builder.setNegativeButton("No") { dialog, which ->
            }
            val dialog = builder.create()
            dialog.show()

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == 4) {

                if (data != null) {

                    if (data.data != null) {

                        selectedImg = data.data

                        val inputStream = contentResolver.openInputStream(selectedImg!!)
                        val bytes = inputStream?.readBytes()
                        val encoded = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
                        val prefs = getSharedPreferences("chatBackground", Context.MODE_PRIVATE)
                        val editor = prefs.edit()
                        editor.putString("photo", encoded)
                        editor.apply()

                        Toast.makeText(this@SettingsActivity,"Successfully selected background.",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }catch (e: Exception){

            println(e.localizedMessage)

        }
    }

    //Commands that evaluate online status
    override fun onRestart() {
        super.onRestart()
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