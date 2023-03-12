package com.gematriga.scruffy.activity

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.gematriga.scruffy.databinding.ActivityProfileBinding
import com.gematriga.scruffy.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private lateinit var storage : FirebaseStorage
    private lateinit var selectedImg : Uri
    private lateinit var dialog: AlertDialog.Builder



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(materialToolbar)
        supportActionBar?.title = "Profile"

        //Querying the status of the dark mode
        val appSettingPrefs : SharedPreferences = getSharedPreferences("AppSettingPrefs",0)
        val isNightModeOn : Boolean = appSettingPrefs.getBoolean("NightMode",false)

        if (isNightModeOn){

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)


        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        }

        binding.materialToolbar.setNavigationOnClickListener {

            onBackPressedDispatcher.onBackPressed()

        }


        dialog = AlertDialog.Builder(this)
            .setMessage("Updating Profile..")
            .setCancelable(false)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.userImage.setOnClickListener {

            val imgUrIntent = Intent()
            imgUrIntent.action = Intent.ACTION_GET_CONTENT
            imgUrIntent.type = "image/*"
            startActivityForResult(imgUrIntent, 1)

        }


        binding.continueButton.setOnClickListener {

            try {

                if (binding.userName.text!!.isEmpty()){

                    Toast.makeText(this,"Please Enter Your Name", Toast.LENGTH_LONG).show()

                }else if (selectedImg.equals("")){

                    Toast.makeText(this,"Please Select Your Image", Toast.LENGTH_LONG).show()

                }else{
                    uploadData()
                }

            }catch (e: Exception){

                Toast.makeText(this,e.localizedMessage,Toast.LENGTH_LONG).show()
            }


        }

    }


    private fun uploadData() {

        val reference = storage.reference.child("Profile").child(Date().time.toString())
        reference.putFile(selectedImg).addOnCompleteListener{

            if (it.isSuccessful){

                reference.downloadUrl.addOnSuccessListener { task ->

                        uploadInfo(task.toString())
                }

            }

        }

    }

    private fun uploadInfo(imgUrl: String) {

        val defaultBackgroundImage = "defaultCoverScruffy.jpeg"
        val user = UserModel(auth.uid.toString(), binding.userName.text.toString(), auth.currentUser!!.phoneNumber.toString(), imgUrl, defaultBackgroundImage)

        database.reference.child("users")
            .child(auth.uid.toString())
            .setValue(user)
            .addOnSuccessListener {

                Toast.makeText(this,"Profile created.", Toast.LENGTH_LONG).show()
                startActivity(Intent(this,HomeActivity::class.java))
                finish()

            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null){

            if (data.data != null){

                selectedImg = data.data!!

                binding.userImage.setImageURI(selectedImg)

            }

        }

    }


}