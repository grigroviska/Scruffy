package com.gematriga.scruffy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gematriga.scruffy.R
import com.gematriga.scruffy.databinding.ReceiverLayoutItemBinding
import com.gematriga.scruffy.databinding.SendItemLayoutBinding
import com.gematriga.scruffy.model.MessageModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter(var context : Context, var list : ArrayList<MessageModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var ITEM_SENT = 1
    var ITEM_RECEIVE = 2
    private var previousDate: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SENT)
            SentViewHolder(LayoutInflater.from(context).inflate(R.layout.send_item_layout, parent, false))
        else                    ReceiverViewHolder(LayoutInflater.from(context).inflate(R.layout.receiver_layout_item, parent, false))

    }

    override fun getItemViewType(position: Int): Int {
        return if(FirebaseAuth.getInstance().uid == list[position].senderId) ITEM_SENT else ITEM_RECEIVE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = list[position]
        val hourFormat = SimpleDateFormat("hh:mm a")
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val currentDate = millisecondsToDate(message.timeStamp, dateFormat)

        val showDate = currentDate != previousDate

        if (holder.itemViewType == ITEM_SENT){
            val viewHolder = holder as SentViewHolder

            if(message.messageType.equals("photo")){
                viewHolder.binding.image.visibility = View.VISIBLE
                viewHolder.binding.pLinear.visibility = View.VISIBLE
                viewHolder.binding.userMsg.visibility = View.GONE
                viewHolder.binding.mLinear.visibility = View.GONE
                Glide.with(context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.simpson)
                    .into(viewHolder.binding.image)
                viewHolder.binding.pdateMsg.text = millisecondsToDate(message.timeStamp, hourFormat)
            }

            if (showDate) {
                viewHolder.binding.dateText.text = currentDate
                viewHolder.binding.dateGroup.visibility = View.VISIBLE
            } else {
                viewHolder.binding.dateGroup.visibility = View.GONE
            }

            viewHolder.binding.dateMsg.text = millisecondsToDate(message.timeStamp, hourFormat)
            viewHolder.binding.userMsg.text = message.message

        } else {
            val viewHolder = holder as ReceiverViewHolder

            if(message.messageType.equals("photo")){
                viewHolder.binding.image.visibility = View.VISIBLE
                viewHolder.binding.pLinear.visibility = View.VISIBLE
                viewHolder.binding.pdateMsg.text = message.timeStamp.toString()
                viewHolder.binding.userMsg.visibility = View.GONE
                viewHolder.binding.mLinear.visibility = View.GONE
                Glide.with(context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.simpson)
                    .into(viewHolder.binding.image)
                viewHolder.binding.pdateMsg.text = millisecondsToDate(message.timeStamp, hourFormat)
            }

            if (showDate) {
                viewHolder.binding.dateText.text = currentDate
                viewHolder.binding.dateGroup.visibility = View.VISIBLE
            } else {
                viewHolder.binding.dateGroup.visibility = View.GONE
            }

            viewHolder.binding.dateMsg.text = millisecondsToDate(message.timeStamp, hourFormat)
            viewHolder.binding.userMsg.text = message.message
        }

        previousDate = currentDate
    }

    private fun millisecondsToDate(milliseconds: Long, dateFormat: SimpleDateFormat): String? {
        return dateFormat.format(Date(milliseconds))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class SentViewHolder(view: View) : RecyclerView.ViewHolder(view){

        var binding = SendItemLayoutBinding.bind(view)

    }

    inner class ReceiverViewHolder(view: View) : RecyclerView.ViewHolder(view){

        var binding = ReceiverLayoutItemBinding.bind(view)


    }

}