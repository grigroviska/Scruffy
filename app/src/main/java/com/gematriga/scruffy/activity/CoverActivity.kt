package com.gematriga.scruffy.activity

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.gematriga.scruffy.databinding.ActivityCoverBinding
import com.google.firebase.database.FirebaseDatabase

class CoverActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCoverBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val coverPreferences = getSharedPreferences("CoverPref", MODE_PRIVATE)
        val coverGet = coverPreferences.getString("cover","default")
        println(coverGet)


        binding.defaultCover.setOnCheckedChangeListener {butonView, ischecked ->

            if (coverGet != "default") {
                binding.defaultCover.isChecked = true
                binding.citycenterCover.isChecked = false
                binding.rainCover.isChecked = false
                coverPreferences.edit().putString("cover", "default").apply()
                println(coverGet)
            }

        }

        binding.citycenterCover.setOnCheckedChangeListener {butonView, ischecked ->

            if (coverGet != "cityCenter") {
                binding.defaultCover.isChecked = false
                binding.citycenterCover.isChecked = true
                binding.rainCover.isChecked = false
                coverPreferences.edit().putString("cover", "cityCenter").apply()
                println(coverGet)
            }

        }

        binding.rainCover.setOnCheckedChangeListener {butonView, ischecked ->

            if (coverGet != "rainCover") {
                binding.defaultCover.isChecked = false
                binding.citycenterCover.isChecked = false
                binding.rainCover.isChecked = true
                coverPreferences.edit().putString("cover", "rainCover").apply()
                println(coverGet)
            }

        }

    }

}