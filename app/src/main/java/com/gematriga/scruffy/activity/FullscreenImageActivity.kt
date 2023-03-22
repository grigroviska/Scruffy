package com.gematriga.scruffy.activity

import android.animation.ObjectAnimator
import android.app.DownloadManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.gematriga.scruffy.R
import com.gematriga.scruffy.databinding.ActivityFullscreenImageBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class FullscreenImageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFullscreenImageBinding
    private lateinit var dReference : DatabaseReference
    var fromWho : String? = null
    var imageUrl : String? = null

    private var isFullScreen = false
    private lateinit var decorView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.materialToolbar.setNavigationOnClickListener {

            onBackPressedDispatcher.onBackPressed()

        }

        binding.downloadButton.setOnClickListener {

            downloadImage()

        }

        imageUrl = intent.getStringExtra("imageUrl")
        val imageDate = intent.getStringExtra("imageDate")
        fromWho = intent.getStringExtra("fromWho")
        val imageView = findViewById<ImageView>(R.id.fullscreenImageView)

        Glide.with(this)
            .load(imageUrl)
            .into(imageView)

        binding.dateOfTheImage.text = imageDate

        checkData()

        decorView = window.decorView

        // Set the click listener on the ImageView
// Set the touch listener on the ImageView
        imageView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Set the time and count for the first click
                    if (event.pointerCount == 1) {
                        imageView.tag = event.downTime
                    }
                }
                MotionEvent.ACTION_UP -> {
                    // Get the time and count for the first click
                    val firstClickTime = imageView.tag as Long?
                    val count = event.pointerCount

                    if (firstClickTime != null) {
                        if (count == 1 && event.eventTime - firstClickTime < ViewConfiguration.getDoubleTapTimeout()) {
                            // Single click
                            toggleFullScreen()
                        } else if (count == 2) {
                            // Double click
                            toggleFullScreen()
                        }
                    }
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    // Reset the tag when another finger touches the screen
                    imageView.tag = null
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    // Set the time and count for the first click
                    if (event.pointerCount == 2) {
                        imageView.tag = event.eventTime
                    }
                }
            }
            true
        }


    }

    private fun downloadImage(){

        val request = DownloadManager.Request(Uri.parse(imageUrl))
            .setTitle("Scruffy")
            .setDescription("Downloading...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)

        val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        dm.enqueue(request)

    }


    private fun toggleFullScreen() {
        if (isFullScreen) {
            // Show the system bars
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            binding.appBarLayout.visibility = View.VISIBLE
            // Set the isFullScreen flag to false
            isFullScreen = false
        } else {
            // Hide the system bars
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            binding.appBarLayout.visibility = View.INVISIBLE
            // Set the isFullScreen flag to true
            isFullScreen = true
        }
    }

    private fun checkData(){

        dReference = FirebaseDatabase.getInstance().getReference("users")
        dReference.child(fromWho!!).get()
            .addOnSuccessListener {
                val nickName = it.child("name").value.toString()

                binding.nickName.text = nickName

            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

    }

}