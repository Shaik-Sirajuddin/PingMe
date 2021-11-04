package com.sirajapps.pingme.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.sirajapps.pingme.R
import com.sirajapps.pingme.models.Message
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MessagesAdapter(val context: Context,val data:ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val MSG_SENT = 1
    val MSG_RECEIVE = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType==MSG_SENT){
            val view = LayoutInflater.from(context).inflate(R.layout.sender_item,parent,false)
            SendViewHolder(view)
        } else{
            val view = LayoutInflater.from(context).inflate(R.layout.receiver_item,parent,false)
            ReceiveViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(FirebaseAuth.getInstance().uid == data[position].senderId){
            MSG_SENT
        } else{
            MSG_RECEIVE
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       val simple = SimpleDateFormat("hh:mm am")
        if(holder.itemViewType == MSG_SENT){
            holder as SendViewHolder
           holder.message.text = data[position].message
           holder.time.text = simple.format(Date(data[position].time))
            when {
                data[position].offlineImageUri != null -> {
                    Glide.with(context).load(data[position].offlineImageUri)
                        .into(holder.image)
                    holder.image.visibility = View.VISIBLE
                }
                data[position].onlineImageUri != null -> {
                    Glide.with(context).load(data[position].onlineImageUri)
                        .into(holder.image)
                    holder.image.visibility = View.VISIBLE
                }
                else -> {
                    holder.image.visibility = View.GONE
                }
            }
            if(data[position].message.isEmpty()){
                holder.message.visibility = View.GONE
            }
        }
       else {
            holder as ReceiveViewHolder
            holder.message.text = data[position].message
            holder.time.text = simple.format(Date(data[position].time))
            when {
                data[position].offlineImageUri != null -> {
                    Glide.with(context).load(data[position].offlineImageUri)
                        .into(holder.image)
                    holder.image.visibility = View.VISIBLE
                }
                data[position].onlineImageUri != null -> {
                    Glide.with(context).load(data[position].onlineImageUri)
                        .into(holder.image)
                    holder.image.visibility = View.VISIBLE
                }
                else -> {
                    holder.image.visibility = View.GONE
                }
            }

            if(data[position].message.isEmpty()){
                holder.message.visibility = View.GONE
            }
        }
    }
    override fun getItemCount(): Int {
        return data.size
    }
}
class SendViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
    val message: TextView = itemView.findViewById(R.id.sender_msg)
    val time:TextView = itemView.findViewById(R.id.msg_time)
    val image:ImageView = itemView.findViewById(R.id.Image)
}
class ReceiveViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
    val message:TextView = itemView.findViewById(R.id.receive_msg)
    val time:TextView = itemView.findViewById(R.id.msg_time)
    val image:ImageView = itemView.findViewById(R.id.Image)
}