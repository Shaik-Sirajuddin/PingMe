package com.sirajapps.pingme.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sirajapps.pingme.R
import com.sirajapps.pingme.models.UserStatus
import de.hdodenhof.circleimageview.CircleImageView

class StatusAdapter(private val context: Context, private val list:ArrayList<UserStatus>):RecyclerView.Adapter<StatusHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusHolder {
      val view = LayoutInflater.from(context).inflate(R.layout.status_item,parent,false)
      val holder = StatusHolder(view)
        holder.image.setOnClickListener {

        }
      return holder
    }

    override fun onBindViewHolder(holder: StatusHolder, position: Int) {
        Glide.with(context)
            .load(list[position].lastStatus.imageUri)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.image)
        holder.statusName.text = list[position].userName
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class StatusHolder(item: View):RecyclerView.ViewHolder(item){
    val image:CircleImageView = item.findViewById(R.id.statusIcon)
    val statusName: TextView = item.findViewById(R.id.statusName)
}
