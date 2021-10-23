package com.sirajapps.pingme.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sirajapps.pingme.R
import com.sirajapps.pingme.models.User
import com.sirajapps.pingme.models.UserOffline
import com.sirajapps.pingme.storage.UserOfflineAndUser
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UsersAdapter(private val context:Context,private val list:ArrayList<UserOfflineAndUser>,private val listener:UsersAdapterClicks):RecyclerView.Adapter<UsersViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
       val view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false)
       val holder = UsersViewHolder(view)
        view.setOnClickListener {
           listener.itemClicked(list[holder.adapterPosition],holder.adapterPosition)
        }
       return holder
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.userName.text = list[position].user.name
        if (list[position].offlineUser.lastMsg != null) {
            val date = SimpleDateFormat("hh:mm")
            holder.lastMessage.text = list[position].offlineUser.lastMsg?.message
            holder.lastMsgTime.text = date.format(Date(list[position].offlineUser.lastMsg!!.time))
        } else {
            holder.lastMessage.text = context.getString(R.string.tap_to_chat)
        }
        if (list[position].offlineUser.offlineImage != null) {
            Glide.with(context).load(list[position].offlineUser.offlineImage)
                .placeholder(R.drawable.profile_image)
                .into(holder.userImage)
        } else {
            Glide.with(context).load(list[position].user.onlineImageUri)
                .placeholder(R.drawable.profile_image)
                .into(holder.userImage)
        }
    }

    override fun getItemCount(): Int {
       return list.size
    }
}
class UsersViewHolder(item:View):RecyclerView.ViewHolder(item){
    val userName: TextView = item.findViewById(R.id.userName)
    val userImage: CircleImageView = item.findViewById(R.id.userImage)
    val lastMessage: TextView = item.findViewById(R.id.lastMessage)
    val lastMsgTime: TextView = item.findViewById(R.id.lastMessageTime)
}
interface UsersAdapterClicks{
    fun itemClicked(user: UserOfflineAndUser, pos:Int)
}