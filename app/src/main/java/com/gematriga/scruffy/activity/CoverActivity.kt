package com.gematriga.scruffy.activity

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.gematriga.scruffy.R
import com.gematriga.scruffy.databinding.ActivityCoverBinding
import com.google.firebase.database.FirebaseDatabase
import pl.droidsonroids.gif.GifImageView

class CoverActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCoverBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoverBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val coverPreferences = getSharedPreferences("CoverPref", MODE_PRIVATE)
        val coverGet = coverPreferences.getString("cover","default")
        


        binding.defaultCover.setOnCheckedChangeListener { compoundButton, b ->
            binding.defaultCover.isChecked = true
            binding.citycenterCover.isChecked = false
            binding.rainCover.isChecked = false
            if (coverGet != "default") {
                coverPreferences.edit().putString("cover", "default").apply()
                Log.d("CoverActivity", "New cover preference: ${coverPreferences.getString("cover","default")}")
                println("$coverGet")
            }
        }

        binding.citycenterCover.setOnCheckedChangeListener { compoundButton, b ->
            binding.citycenterCover.isChecked = true
            binding.defaultCover.isChecked = false
            binding.rainCover.isChecked = false
            if (coverGet != "cityCenter") {
                coverPreferences.edit().putString("cover", "cityCenter").apply()
                Log.d("CoverActivity", "New cover preference: ${coverPreferences.getString("cover","cityCenter")}")
                println("$coverGet")
            }
        }

        binding.rainCover.setOnCheckedChangeListener { compoundButton, b ->
            binding.rainCover.isChecked = true
            binding.citycenterCover.isChecked = false
            binding.defaultCover.isChecked = false
            if (coverGet != "rainCover") {
                coverPreferences.edit().putString("cover", "rainCover").apply()
                Log.d("CoverActivity", "New cover preference: ${coverPreferences.getString("cover","rainCover")}")
                println("$coverGet")
            }
        }
    }

}