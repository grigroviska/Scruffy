package com.gematriga.scruffy.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gematriga.scruffy.databinding.ActivityHomeBinding
import com.gematriga.scruffy.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.materialToolbar.setNavigationOnClickListener {

            onBackPressedDispatcher.onBackPressed()

        }

    }
}