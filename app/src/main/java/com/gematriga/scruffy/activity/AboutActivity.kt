package com.gematriga.scruffy.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.gematriga.scruffy.R
import com.gematriga.scruffy.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAboutBinding
    private var mediaPlayer : MediaPlayer? = null
    private var isMusicPlaying = false // Flag to keep track of music playback state

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mail = "karagozoglualper@protonmail.com"
        val paragraph = "Creative and Designer: Grigroviska \nContact: Alper Karagözoğlu \nMail: $mail"
        binding.contactText.text = paragraph

        // Create a SpannableString from the contact paragraph text
        val spannableString = SpannableString(paragraph)

        // Define the clickable span for the GitHub link
        val clickableSpanGitHub = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle the click event, e.g. navigate to the GitHub link
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/grigroviska/Scruffy"))
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                // Customize the appearance of the clickable link, if desired
                ds.color = Color.WHITE // Set the link color
                ds.typeface = Typeface.defaultFromStyle(Typeface.BOLD) // Make text bold
                ds.isUnderlineText = true // Add underline to the link
            }
        }

        // Define the clickable span for the Carrd link
        val clickableSpanCarrd = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle the click event, e.g. navigate to the Carrd link
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://alperkaragozoglu.carrd.co"))
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                // Customize the appearance of the clickable link, if desired
                ds.color = Color.WHITE // Set the link color
                ds.typeface = Typeface.defaultFromStyle(Typeface.BOLD)// Make text bold
                ds.isUnderlineText = true // Add underline to the link
            }
        }

        // Define the clickable span for the Mail link
        val clickableSpanMail = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle the click event, e.g. navigate to the Carrd link
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:$mail"))
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                // Customize the appearance of the clickable link, if desired
                ds.color = Color.WHITE // Set the link color
                ds.typeface = Typeface.defaultFromStyle(Typeface.BOLD)// Make text bold
                ds.isUnderlineText = true // Add underline to the link
            }
        }

        // Set the clickable spans for the Google and GitHub links in the SpannableString
        spannableString.setSpan(clickableSpanGitHub, paragraph.indexOf("Grigroviska"), paragraph.indexOf("Grigroviska") + "Grigroviska".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(clickableSpanCarrd, paragraph.indexOf("Alper Karagözoğlu"), paragraph.indexOf("Alper Karagözoğlu") + "Alper Karagözoğlu".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(clickableSpanMail, paragraph.indexOf("$mail"), paragraph.indexOf("$mail") + "$mail".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set the SpannableString as the text for the TextView
        binding.contactText.text = spannableString

        // Make the TextView clickable
        binding.contactText.movementMethod = LinkMovementMethod.getInstance()

        binding.aboutText.text = "This is a messaging application.\n\n" +
                "However, this explanation is not that short.In addition, users of this application reflect their lifestyles on their profiles. \n If you are a developer, please try to add improvements to this application. \n Also, thanks for checking out this project, don't forget to star and follow up :)"


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

        val appNameAnimator = ObjectAnimator.ofFloat(binding.appName, "alpha", 1f, 0f, 1f)
        appNameAnimator.duration = 1000

        val repeatAnimator = ValueAnimator.ofInt(0, 3)
        repeatAnimator.duration = 1000
        repeatAnimator.repeatCount = 4

        // Start the repeatAnimator to flash the Appname text
        repeatAnimator.start()

        repeatAnimator.addUpdateListener { animator ->
            if (animator.animatedValue as Int == 0) {
                // Start the ObjectAnimator when the repeat starts
                appNameAnimator.start()
            }
        }


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
        mediaPlayer?.stop()
        mediaPlayer = null
        super.onDestroy()
    }
}