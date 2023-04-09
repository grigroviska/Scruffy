package com.gematriga.scruffy.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.gematriga.scruffy.R
import com.gematriga.scruffy.databinding.ActivitySettingsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.File



open class SettingsActivity : AppCompatActivity(){

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

        binding.clearCacheLayout.setOnClickListener {

            val cacheDir = this.cacheDir
            deleteDir(cacheDir)

            Snackbar.make(it, "Cache cleared.", Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                .setBackgroundTint(ContextCompat.getColor(this, R.color.primaryThemeColor))
                .setTextColor(ContextCompat.getColor(this, R.color.white))
                .show()

        }

        binding.signOutLayout.setOnClickListener {

            val bottomSheetView = layoutInflater.inflate(R.layout.sign_out_layout, null)
            val bottomSheetDialog = BottomSheetDialog(this@SettingsActivity, R.style.CustomBottomSheetDialog)
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val yesButton = bottomSheetView.findViewById<Button>(R.id.signOutforExit)
            val noButton = bottomSheetView.findViewById<Button>(R.id.cancel)

            bottomSheetDialog.show()

            yesButton.setOnClickListener {
                // Perform sign out and navigate to MainActivity
                auth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                bottomSheetDialog.dismiss()
            }

            noButton.setOnClickListener {
                // Dismiss the bottom sheet
                bottomSheetDialog.dismiss()
            }

        }


    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        return dir!!.delete()

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