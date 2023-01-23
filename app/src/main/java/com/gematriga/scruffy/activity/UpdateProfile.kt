package com.gematriga.scruffy.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.gematriga.scruffy.databinding.ActivityUpdateProfileBinding
import com.gematriga.scruffy.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_update_profile.*
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

    private var auId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.materialToolbar)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        auId = auth.currentUser?.uid.toString()

        checkData()

        binding.materialToolbar.setNavigationOnClickListener {

            onBackPressedDispatcher.onBackPressed()

        }

        binding.selectProfilePhoto.setOnClickListener {

            val imgUrIntent = Intent()
            imgUrIntent.action = Intent.ACTION_GET_CONTENT
            imgUrIntent.type = "image/*"
            @Suppress("DEPRECATION")
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

            } else{
                println(selectedImg)
                println(url)

                try {
                    println(changeUserName.text.toString())
                    println(nickName)
                    if (binding.changeUserName.text.toString() == nickName && selectedImg == null ){

                        Toast.makeText(this,"Please Enter Something", Toast.LENGTH_LONG).show()

                    }else if ((binding.changeUserName.text.toString() != nickName || selectedImg != null) || (binding.changeUserName.text.toString() != nickName && selectedImg != null)) {
                        if (selectedImg != null) {
                            println(2)
                            uploadData()
                        } else {
                            if (binding.changeUserName.text.toString() != nickName) {
                                println(3)
                                uploadTextInfo()
                            }
                        }
                    }


                }catch (e: Exception){

                    Toast.makeText(this,"111111",Toast.LENGTH_LONG).show()
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
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
            dReference.child(auId!!).get()
                .addOnSuccessListener {
                    url = it.child("imageUrl").value.toString()
                    nickName = it.child("name").value.toString()
                    phoneNumber = it.child("number").value.toString()

                    Glide.with(this).load(url).into(profileImageView)

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

}