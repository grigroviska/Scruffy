package com.gematriga.scruffy.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gematriga.scruffy.databinding.ActivityHomeBinding
import com.gematriga.scruffy.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}