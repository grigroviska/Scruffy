package com.gematriga.scruffy.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.gematriga.scruffy.R
import com.gematriga.scruffy.databinding.ActivityFullscreenImageBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class FullscreenImageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFullscreenImageBinding
    private lateinit var database : FirebaseDatabase
    private lateinit var dReference : DatabaseReference
    var fromWho : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.materialToolbar.setNavigationOnClickListener {

            onBackPressedDispatcher.onBackPressed()

        }

        val imageUrl = intent.getStringExtra("imageUrl")
        val imageDate = intent.getStringExtra("imageDate")
        fromWho = intent.getStringExtra("fromWho")
        val imageView = findViewById<ImageView>(R.id.fullscreenImageView)

        Glide.with(this)
            .load(imageUrl)
            .into(imageView)

        binding.dateOfTheImage.text = imageDate

        checkData()


    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (binding.materialToolbar.visibility == View.VISIBLE) {
                    binding.appBarLayout.visibility = View.GONE
                } else {
                    binding.appBarLayout.visibility = View.VISIBLE
                }
            }
        }
        return super.onTouchEvent(event)
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