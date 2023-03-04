package com.gematriga.scruffy.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.gematriga.scruffy.R
import com.gematriga.scruffy.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.selectbackground.*
import java.util.*


open class SettingsActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingsBinding
    private var database : FirebaseDatabase? = null

    private var currentId : String? = null
    private var selectedImg : Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        currentId = FirebaseAuth.getInstance().uid
        var preferenceManager: PreferenceManager

        //Commands that evaluate online status
        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Online")

        binding.materialToolbar.setNavigationOnClickListener {

            onBackPressedDispatcher.onBackPressed()

        }

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

        binding.nightModeSwitch.setOnClickListener {

            if(isNightModeOn){

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefsEdit.putBoolean("NightMode", false)
                sharedPrefsEdit.apply()

            }else{

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefsEdit.putBoolean("NightMode", true)
                sharedPrefsEdit.apply()

            }


        }

        binding.coverMode.setOnClickListener {

            val goToCover = Intent(this@SettingsActivity, CoverActivity::class.java)
            startActivity(goToCover)

        }

        binding.selectBackground.setOnClickListener {

            /*val view = View.inflate(this@SettingsActivity, R.layout.selectbackground,null)
            val builder = AlertDialog.Builder(this@SettingsActivity)
            builder.setView(view)

            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)*/


            val imgUrIntent = Intent()
            imgUrIntent.action = Intent.ACTION_GET_CONTENT
            imgUrIntent.type = "image/*"
            @Suppress("DEPRECATION")
            startActivityForResult(imgUrIntent, 4)
        }


    }


    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
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