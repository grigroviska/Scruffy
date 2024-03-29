package com.gematriga.scruffy.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gematriga.scruffy.R
import com.gematriga.scruffy.activity.ChatActivity
import com.gematriga.scruffy.activity.FullscreenImageActivity
import com.gematriga.scruffy.activity.UpdateProfile
import com.gematriga.scruffy.databinding.ChatUserItemLayoutBinding
import com.gematriga.scruffy.databinding.ShortprofileBinding
import com.gematriga.scruffy.model.UserModel


class ChatAdapter(var context : Context, var list : ArrayList<UserModel>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {



    inner class ChatViewHolder(view : View) : RecyclerView.ViewHolder(view){

        var binding : ChatUserItemLayoutBinding = ChatUserItemLayoutBinding.bind(view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_user_item_layout,parent,false))
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        var user = list[position]

        Glide.with(context).load(user.imageUrl).into(holder.binding.userImage)
        holder.binding.userName.text = user.name

        holder.itemView.setOnClickListener {

            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("uid", user.uid)
            context.startActivity(intent)

        }

        holder.binding.userImage.setOnClickListener {

            val view = View.inflate(context,R.layout.shortprofile, null)
            val binding = ShortprofileBinding.bind(view)
            Glide.with(context).load(user.imageUrl).into(binding.uNickPhoto)
            binding.uNickName.text = user.name

            val builder = AlertDialog.Builder(context)
            builder.setView(view)

            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            binding.uNickPhoto.setOnClickListener {

                val intent = Intent(context, FullscreenImageActivity::class.java)
                intent.putExtra("imageUrl", user.imageUrl)
                intent.putExtra("fromWho", user.uid)
                context.startActivity(intent)

            }

            binding.goToOtherProfile.setOnClickListener {

                val goToOtherProfile = Intent(context, UpdateProfile::class.java)
                goToOtherProfile.putExtra("fromChat", "true")
                goToOtherProfile.putExtra("uid", user.uid)
                context.startActivity(goToOtherProfile)
                dialog.dismiss()

            }

            binding.goToMessage.setOnClickListener {

                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("uid", user.uid)
                context.startActivity(intent)
                dialog.dismiss()

            }

        }



    }

    override fun getItemCount(): Int {
        return list.size
    }

}