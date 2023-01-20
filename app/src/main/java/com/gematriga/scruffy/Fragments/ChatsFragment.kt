package com.gematriga.scruffy.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.gematriga.scruffy.R
import com.gematriga.scruffy.adapter.ChatAdapter
import com.gematriga.scruffy.databinding.FragmentChatsBinding
import com.gematriga.scruffy.databinding.FragmentSettingsBinding
import com.gematriga.scruffy.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!
    private var database : FirebaseDatabase? = null
    lateinit var userList : ArrayList<UserModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        val view = binding.root

        database = FirebaseDatabase.getInstance()
        userList = ArrayList()


        database!!.reference.child("users")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    userList.clear()
                    for (snapshot1 in snapshot.children){

                        val user = snapshot1.getValue(UserModel::class.java)
                        if (user!!.uid !=FirebaseAuth.getInstance().uid){

                            userList.add(user)

                        }

                    }

                    binding.userListRecyclerView.adapter = ChatAdapter(requireContext(), userList)


                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}