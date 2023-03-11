package com.gematriga.scruffy.activity

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
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


        val coverPreferences = getSharedPreferences("CoverPref", 0)
        val coverGet = coverPreferences.getString("cover","default")

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)

        when (coverGet) {
            "default" -> binding.radioButton1.isChecked = true
            "cityCenter" -> binding.radioButton2.isChecked = true
            "rainCover" -> binding.radioButton3.isChecked = true
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.radioButton1) {

                if (coverGet != "default") {
                    coverPreferences.edit().putString("cover", "default").apply()

                }

            } else if (checkedId == R.id.radioButton2) {

                if (coverGet != "cityCenter") {
                    coverPreferences.edit().putString("cover", "cityCenter").apply()

                }

            } else {

                if (coverGet != "rainCover") {
                    coverPreferences.edit().putString("cover", "rainCover").apply()

                }

            }
        }

    }

}