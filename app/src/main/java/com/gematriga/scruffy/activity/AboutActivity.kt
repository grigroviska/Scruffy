package com.gematriga.scruffy.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.util.Linkify
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.gematriga.scruffy.R
import com.gematriga.scruffy.databinding.ActivityAboutBinding
import com.gematriga.scruffy.databinding.ActivityCoverBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAboutBinding
    private var mediaPlayer : MediaPlayer? = null
    private var isMusicPlaying = false // Flag to keep track of music playback state

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val github = "https://github.com/grigroviska/Scruffy"
        val carrd = "https://alperkaragozoglu.carrd.co"
        val mail = "karagozoglualper@protonmail.com"
        val paragraph = "Creative and Designer: $github \nContact: $carrd \nMail: $mail"
        binding.contactText.text = paragraph
        Linkify.addLinks(binding.contactText, Linkify.ALL)

        binding.aboutText.text = "This is a messaging application.\n" +
                "                \"                However, this explanation is not that short.In addition, users of this application reflect their lifestyles on their profiles.\\n\" +\n" +
                "                \"If you are a developer, please try to add improvements to this application."


        // Load the animation
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        // Set up an animation listener to handle animation events
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                binding.aboutText.startAnimation(animation)
            }

            override fun onAnimationEnd(animation: Animation?) {
                // Animation ended
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // Animation repeated
            }
        })

        // Add click listener to Appname TextView
        binding.appName.setOnClickListener {
            if (isMusicPlaying) {
                pauseMusic() // Call stopMusic() to stop the music playback
            } else {
                playMusic() // Call playMusic() to play the music
            }
            isMusicPlaying = !isMusicPlaying // Toggle the music playback state
        }

        // Start the animation on the text view
        binding.aboutText.startAnimation(animation)


        // Initialize the MediaPlayer object with the audio file
        mediaPlayer = MediaPlayer.create(this, R.raw.tetris)
        playMusic()
    }

    // Starting music playback
    private fun playMusic() {
        mediaPlayer?.start()
    }

    // Stopping music playback
    /*private fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.prepare()
    }*/

    // Starting music pause
    private fun pauseMusic() {
        mediaPlayer?.pause()
    }

    // Release MediaPlayer resources when the activity is destroyed
    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
}