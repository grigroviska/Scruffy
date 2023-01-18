package com.gematriga.scruffy.activity

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.gematriga.scruffy.R
import com.gematriga.scruffy.adapter.MessageAdapter
import com.gematriga.scruffy.databinding.ActivityChatBinding
import com.gematriga.scruffy.model.MessageModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    private lateinit var binding : ActivityChatBinding
    private lateinit var database : FirebaseDatabase
    private lateinit var dReference : DatabaseReference

    private lateinit var senderUid : String
    private lateinit var receiverUid : String

    private lateinit var senderRoom : String
    private lateinit var receiverRoom : String

    private lateinit var list : ArrayList<MessageModel>
    var url : String? = null
    var nickName : String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        senderUid = FirebaseAuth.getInstance().uid.toString()

        receiverUid = intent.getStringExtra("uid")!!

        list = ArrayList()

        senderRoom = senderUid+receiverUid
        receiverRoom = receiverUid+senderUid

        database = FirebaseDatabase.getInstance()

        checkData()

        binding.backButton.setOnClickListener {

            onBackPressed()

        }

        binding.sendMessage.setOnClickListener {

            if (binding.messageBox.text.isNotEmpty()) {

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