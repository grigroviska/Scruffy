package com.gematriga.scruffy.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gematriga.scruffy.databinding.ActivityAboutBinding
import com.gematriga.scruffy.databinding.ActivityCoverBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}