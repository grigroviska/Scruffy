package com.gematriga.scruffy.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.gematriga.scruffy.R
import com.gematriga.scruffy.databinding.ActivityCoverBinding
class CoverActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCoverBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.materialToolbar.setNavigationOnClickListener {

            onBackPressedDispatcher.onBackPressed()


        }

        //Querying the status of the dark mode
        val appSettingPrefs : SharedPreferences = getSharedPreferences("AppSettingPrefs",0)
        val isNightModeOn : Boolean = appSettingPrefs.getBoolean("NightMode",false)

        if (isNightModeOn){

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)


        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        }


        val coverPreferences = getSharedPreferences("CoverPref", 0)
        val coverGet = coverPreferences.getString("cover","blueSky")

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)

        when (coverGet) {
            "blueSky" -> binding.radioButton1.isChecked = true
            "cityCenter" -> binding.radioButton2.isChecked = true
            "rainyDay" -> binding.radioButton3.isChecked = true
            "computerStore" -> binding.radioButton4.isChecked = true
            "cafeMood" -> binding.radioButton5.isChecked = true
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.radioButton1) {

                if (coverGet != "blueSky") {
                    coverPreferences.edit().putString("cover", "blueSky").apply()

                }

            } else if (checkedId == R.id.radioButton2) {

                if (coverGet != "cityCenter") {
                    coverPreferences.edit().putString("cover", "cityCenter").apply()

                }

            } else if(checkedId == R.id.radioButton3){

                if (coverGet != "rainyDay") {
                    coverPreferences.edit().putString("cover", "rainyDay").apply()

                }

            }else if(checkedId == R.id.radioButton4){

                if (coverGet != "computerStore") {
                    coverPreferences.edit().putString("cover", "computerStore").apply()

                }

            } else{

                if (coverGet != "cafeMood") {
                    coverPreferences.edit().putString("cover", "cafeMood").apply()

                }

            }
        }

    }

}