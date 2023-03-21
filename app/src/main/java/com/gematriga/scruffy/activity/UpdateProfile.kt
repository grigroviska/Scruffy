package com.gematriga.scruffy.activity

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.gematriga.scruffy.R
import com.gematriga.scruffy.databinding.ActivityUpdateProfileBinding
import com.gematriga.scruffy.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_update_profile.*
import kotlinx.android.synthetic.main.nav_header.*
import java.util.*

class UpdateProfile : AppCompatActivity() {

    private lateinit var binding : ActivityUpdateProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private lateinit var storage : FirebaseStorage
    private var selectedImg : Uri? = null
    private lateinit var dReference : DatabaseReference

    private lateinit var url : String
    private var nickName : String? = null
    private var phoneNumber : String? = null

    private var currentId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.materialToolbar)

        cover()

        //Querying the status of the dark mode
        val appSettingPrefs : SharedPreferences = getSharedPreferences("AppSettingPrefs",0)
        val isNightModeOn : Boolean = appSettingPrefs.getBoolean("NightMode",false)

        if (isNightModeOn){

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)


        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        }

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        currentId = auth.currentUser?.uid.toString()

        //Commands that evaluate online status
        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Online")

        checkData()

        binding.materialToolbar.setNavigationOnClickListener {

            onBackPressedDispatcher.onBackPressed()


        }

        binding.selectProfilePhoto.setOnClickListener {

            val imgUrIntent = Intent()
            imgUrIntent.action = Intent.ACTION_GET_CONTENT
            imgUrIntent.type = "image/*"
            startActivityForResult(imgUrIntent, 1)

        }



        binding.inviteYourFriends.setOnClickListener {

            val invite = Intent(Intent.ACTION_SEND)
            invite.type = "text/plain"
            invite.putExtra(Intent.EXTRA_TEXT, "Scruffy! Download and write to me now."+ "\n" + "https://github.com/grigroviska/Scruffy")
            invite.putExtra(Intent.EXTRA_STREAM, url)
            startActivity(Intent.createChooser(invite, "Share Via :)"))

        }

        binding.editProfileButton.setOnClickListener {

            if (binding.userNameProfile.visibility == View.VISIBLE){

                binding.selectProfilePhoto.visibility = View.VISIBLE
                binding.selectBackground.visibility = View.VISIBLE
                binding.userNameProfile.visibility = View.INVISIBLE
                binding.changeUserName.visibility = View.VISIBLE

                binding.selectBackground.setOnClickListener {

                    startActivity(Intent(this,CoverActivity::class.java))
                    binding.selectProfilePhoto.visibility = View.INVISIBLE
                    binding.selectBackground.visibility = View.INVISIBLE
                    binding.userNameProfile.visibility = View.VISIBLE
                    binding.changeUserName.visibility = View.INVISIBLE

                }

            } else{
                println(selectedImg)
                println(url)

                try {
                    println(changeUserName.text.toString())
                    println(nickName)
                    if (binding.changeUserName.text.toString() == nickName && selectedImg == null ){


                    }else if ((binding.changeUserName.text.toString() != nickName || selectedImg != null) || (binding.changeUserName.text.toString() != nickName && selectedImg != null)) {
                        if (selectedImg != null) {
                            uploadData()
                        } else {
                            if (binding.changeUserName.text.toString() != nickName) {
                                uploadTextInfo()
                            }
                        }
                    }


                }catch (e: Exception){

                    Toast.makeText(this,e.localizedMessage,Toast.LENGTH_LONG).show()
                }

                binding.selectProfilePhoto.visibility = View.INVISIBLE
                binding.selectBackground.visibility = View.INVISIBLE
                binding.userNameProfile.visibility = View.VISIBLE
                binding.changeUserName.visibility = View.INVISIBLE

            }


        }
    }

    private fun uploadTextInfo() {

        val user = UserModel(auth.uid.toString(), binding.changeUserName.text.toString(), auth.currentUser!!.phoneNumber.toString(), url)

        database.reference.child("users")
            .child(auth.uid.toString())
            .setValue(user)
            .addOnSuccessListener {

                Toast.makeText(this,"Profile updated.", Toast.LENGTH_LONG).show()
                startActivity(Intent(this,HomeActivity::class.java))
                finish()

            }

    }

    private fun uploadData() {

        val reference = storage.reference.child("Profile").child(Date().time.toString())
        reference.putFile(selectedImg!!).addOnCompleteListener{

            if (it.isSuccessful){

                reference.downloadUrl.addOnSuccessListener { task ->

                    uploadInfo(task.toString())
                }

            }

        }

    }

    private fun uploadInfo(imgUrl: String) {

        val user = UserModel(auth.uid.toString(), binding.changeUserName.text.toString(), auth.currentUser!!.phoneNumber.toString(), imgUrl)

        database.reference.child("users")
            .child(auth.uid.toString())
            .setValue(user)
            .addOnSuccessListener {

                Toast.makeText(this,"Profile updated.", Toast.LENGTH_LONG).show()
                startActivity(Intent(this,HomeActivity::class.java))
                finish()

            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

            if (data != null){

                if (data.data != null){

                    selectedImg = data.data!!

                    binding.profileImageView.setImageURI(selectedImg)

                }

            }

    }

    private fun checkData(){

        try {
            dReference = FirebaseDatabase.getInstance().getReference("users")
            dReference.child(currentId!!).get()
                .addOnSuccessListener {
                    url = it.child("imageUrl").value.toString()
                    nickName = it.child("name").value.toString()
                    phoneNumber = it.child("number").value.toString()

                    Glide.with(applicationContext).load(url).into(profileImageView)

                    userNameProfile.text = nickName
                    changeUserName.setText(nickName)
                    phoneNumberProfile.text = phoneNumber

                }.addOnFailureListener {
                    Log.e("firebase", "Error getting data", it)
                }

        }catch (e : Exception){

            Toast.makeText(this, e.localizedMessage?.plus(e.message), Toast.LENGTH_LONG).show()
            println(e.localizedMessage?.plus(e.message))

        }


    }

    private fun cover(){

        val coverPreferences = getSharedPreferences("CoverPref", 0)
        val coverGet = coverPreferences.getString("cover","blueSky")

        try {

            when(coverGet){

                "blueSky" -> Glide.with(applicationContext).load(R.drawable.profile_bg).into(backgroundImage)

                "cityCenter" -> Glide.with(applicationContext).load(R.drawable.citycenter_cover).into(backgroundImage)

                "rainyDay" -> Glide.with(applicationContext).load(R.drawable.rainy_cover).into(backgroundImage)

                "computerStore" -> Glide.with(applicationContext).load(R.drawable.computerstore).into(backgroundImage)

                "cafeMood" -> Glide.with(applicationContext).load(R.drawable.cafemood).into(backgroundImage)

            }

        }catch (e : Exception){

            Toast.makeText(this@UpdateProfile, e.localizedMessage, Toast.LENGTH_LONG).show()

        }

    }

    //Commands that evaluate online status
    override fun onRestart() {
        super.onRestart()
        super.onStart()

        cover()

        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Online")
    }

    override fun onResume() {
        super.onResume()

        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Online")

    }

    override fun onDestroy() {
        super.onDestroy()

        val currentId = FirebaseAuth.getInstance().uid

        database!!.reference.child("Presence")
            .child(currentId!!)
            .setValue("Offline")
    }

}