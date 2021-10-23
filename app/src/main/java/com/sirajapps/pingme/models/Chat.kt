package com.sirajapps.pingme.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = "chat_table")
data class Chat(
    var messages:ArrayList<Message> = ArrayList(),
    @PrimaryKey(autoGenerate = true)
    var key:Int = 0,
    var uid:String
){
    override fun hashCode(): Int {
        return Objects.hash(uid)
    }

    override fun equals(other: Any?): Boolean {
        if(other === this)return true
        if(other==null)return false
        if(other.javaClass == javaClass){
            other as Chat
            return other.uid == this.uid
        }
        return false
    }
}
