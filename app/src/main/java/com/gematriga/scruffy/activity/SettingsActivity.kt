package com.gematriga.scruffy.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.gematriga.scruffy.R
import com.gematriga.scruffy.databinding.ActivitySettingsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*


open class SettingsActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingsBinding
    private var database : FirebaseDatabase? = null
    private lateinit var auth : FirebaseAuth

    private var currentId : String? = null
    private var selectedImg : Uri? = null

    private lateinit var locale: Locale


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

        binding.languageLayout.setOnClickListener {

            showLanguageBottomSheet()

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

    private fun showLanguageBottomSheet(){

        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_language, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)

        val languageRadioGroup = bottomSheetView.findViewById<RadioGroup>(R.id.language_radio_group)

        languageRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.english_radio_button -> setLocale("en", this)
                R.id.turkish_radio_button -> setLocale("tr",this)
                R.id.german_radio_button -> setLocale("de",this)
            }
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()

    }

    private fun setLocale(languageCode: String, context: Context) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context?.resources
        val config = Configuration(resources?.configuration)

        config.setLocale(locale)

        resources?.updateConfiguration(config, resources.displayMetrics)

        // If you want to save the selected language preference for future app launches,
        // you can use SharedPreferences here
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