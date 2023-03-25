package com.gematriga.scruffy.activity

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.gematriga.scruffy.R
import com.gematriga.scruffy.databinding.ActivityFullscreenImageBinding
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_fullscreen_image.*
import java.io.ByteArrayOutputStream


class FullscreenImageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFullscreenImageBinding
    private lateinit var dReference : DatabaseReference
    var fromWho : String? = null
    var imageUrl : String? = null

    private var isFullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.materialToolbar)
        supportActionBar!!.title = null

        binding.materialToolbar.setNavigationOnClickListener {

            onBackPressedDispatcher.onBackPressed()

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

        // Set the click listener on the ImageView
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

    private fun shareImage() {

        val bitmap = (fullscreenImageView.drawable as BitmapDrawable).bitmap

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, getImageUri(applicationContext, bitmap))
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        startActivity(Intent.createChooser(intent, "Share Image via"))

    }

    private fun getImageUri(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Scruffy", null)
        return Uri.parse(path)
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

            binding.appBarLayout.visibility = View.VISIBLE
            // Set the isFullScreen flag to false
            isFullScreen = false
        } else {

            binding.appBarLayout.visibility = View.INVISIBLE
            // Set the isFullScreen flag to true
            isFullScreen = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.image_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.shareImage -> shareImage()
            R.id.saveImage -> downloadImage()

        }

        return super.onOptionsItemSelected(item)
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