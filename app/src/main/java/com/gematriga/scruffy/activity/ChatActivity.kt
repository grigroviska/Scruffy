package com.gematriga.scruffy.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.gematriga.scruffy.adapter.MessageAdapter
import com.gematriga.scruffy.databinding.ActivityChatBinding
import com.gematriga.scruffy.model.MessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    private lateinit var binding : ActivityChatBinding
    private lateinit var database : FirebaseDatabase
    private lateinit var dReference : DatabaseReference
    private lateinit var storage : FirebaseStorage

    private lateinit var senderUid : String
    private lateinit var receiverUid : String

    private lateinit var senderRoom : String
    private lateinit var receiverRoom : String

    private lateinit var list : ArrayList<MessageModel>
    var dialog : ProgressDialog?= null
    var url : String? = null
    var nickName : String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        senderUid = FirebaseAuth.getInstance().uid.toString()

        receiverUid = intent.getStringExtra("uid")!!

        list = ArrayList()

        dialog = ProgressDialog(this@ChatActivity)
        dialog!!.setMessage("Uploading image...")
        dialog!!.setCancelable(false)

        senderRoom = senderUid+receiverUid
        receiverRoom = receiverUid+senderUid

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        checkData()

        binding.backButton.setOnClickListener {

            onBackPressedDispatcher.onBackPressed()

        }

        val handler = Handler()
        binding.messageBox.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                database!!.reference.child("Presence")
                    .child(senderUid!!)
                    .setValue("Typing...")

                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping,1000)
            }

            var userStoppedTyping = Runnable {

                database!!.reference.child("Presence")
                    .child(senderUid)
                    .setValue("Online")

            }

        })


        binding.attach.setOnClickListener {

            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,25)

        }

        binding.sendMessage.setOnClickListener {

            if (binding.messageBox.text.isNotEmpty() && binding.messageBox.text.toString().trim().isNotBlank()) {

                val message =
                    MessageModel(binding.messageBox.text.toString(), senderUid, Date().time)

                val randomKey = database.reference.push().key

                database.reference.child("chats")
                    .child(senderRoom).child("message").child(randomKey!!).setValue(message)
                    .addOnSuccessListener {

                        database.reference.child("chats").child(receiverRoom).child("message")
                            .child(randomKey!!).setValue(message).addOnSuccessListener {

                                binding.messageBox.text = null

                            }

                    }
            }

        }

        database!!.reference.child("Presence").child(receiverUid!!)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){

                        val status = snapshot.getValue(String::class.java)
                        if (status == "offline"){

                            binding.status.visibility = View.GONE

                        }else{

                            binding.status.setText(status)
                            binding.status.visibility = View.VISIBLE

                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }


            })


        database.reference.child("chats").child(senderRoom).child("message")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()

                    for (snapshot1 in snapshot.children){

                        val data = snapshot1.getValue(MessageModel::class.java)
                        list.add(data!!)

                    }

                    binding.recyclerView.adapter = MessageAdapter(this@ChatActivity,list)

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ChatActivity,"Error: $error", Toast.LENGTH_SHORT).show()
                }

            })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == 25) {

                if (data != null) {

                    if (data.data != null) {

                        val selectedImage = data.data
                        val calendar = Calendar.getInstance()
                        var reference = storage.reference.child("chats")
                            .child(calendar.timeInMillis.toString() + "")
                        dialog!!.show()
                        reference.putFile(selectedImage!!)
                            .addOnCompleteListener { task ->
                                dialog!!.dismiss()
                                if (task.isSuccessful) {

                                    reference.downloadUrl.addOnSuccessListener { uri ->

                                        val filePath = uri.toString()

                                            val message =
                                                MessageModel("photo", senderUid, Date().time, filePath)

                                            val randomKey = database.reference.push().key

                                            database.reference.child("chats")
                                                .child(senderRoom).child("message").child(randomKey!!).setValue(message)
                                                .addOnSuccessListener {

                                                    database.reference.child("chats").child(receiverRoom).child("message")
                                                        .child(randomKey!!).setValue(message).addOnSuccessListener {

                                                            binding.messageBox.text = null

                                                        }

                                                }
                                    }

                                }

                            }
                    }

                }
            }
        }catch (e: Exception){

            Toast.makeText(this@ChatActivity,e.localizedMessage,Toast.LENGTH_LONG).show()

        }

    }

    override fun onResume() {
        super.onResume()

        database!!.reference.child("Presence")
            .child(senderUid!!)
            .setValue("Online")

    }

    override fun onPause() {
        super.onPause()

        database!!.reference.child("Presence")
            .child(senderUid!!)
            .setValue("Offline")
    }

    override fun onStop() {
        super.onStop()

        database!!.reference.child("Presence")
            .child(senderUid!!)
            .setValue("Offline")

    }

    override fun onDestroy() {
        super.onDestroy()

        database!!.reference.child("Presence")
            .child(senderUid!!)
            .setValue("Offline")
    }

    private fun checkData(){

        dReference = FirebaseDatabase.getInstance().getReference("users")
        dReference.child(receiverUid).get()
            .addOnSuccessListener {
                url = it.child("imageUrl").value.toString()
                nickName = it.child("name").value.toString()

                Glide.with(this).load(url).into(binding.nickProfile)

                binding.nickName.setText(nickName)
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

    }
}