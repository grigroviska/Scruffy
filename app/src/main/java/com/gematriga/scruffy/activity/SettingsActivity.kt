package com.gematriga.scruffy.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gematriga.scruffy.databinding.ActivityHomeBinding
import com.gematriga.scruffy.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingsBinding
    private var database : FirebaseDatabase? = null

    private var currentId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        currentId = FirebaseAuth.getInstance().uid

        //Commands that evaluate online status
        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Online")

        binding.materialToolbar.setNavigationOnClickListener {

            onBackPressedDispatcher.onBackPressed()

        }

    }
    //Commands that evaluate online status
    override fun onResume() {
        super.onResume()

        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Online")

    }

    override fun onPause() {
        super.onPause()

        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Offline")
    }

    override fun onStop() {
        super.onStop()

        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Offline")

    }

    override fun onDestroy() {
        super.onDestroy()

        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Offline")
    }

}