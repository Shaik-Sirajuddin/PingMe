package com.sirajapps.pingme.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = false)
    var uid:String = "",
    var name:String = "",
    var email:String  = "",
    var onlineImageUri: String? = null){
    override fun hashCode(): Int {
        return Objects.hash(uid)
    }

    override fun equals(other: Any?): Boolean {
        if(other === this)return true
        if(other==null)return false
        if(other.javaClass == javaClass){
            other as User
            return other.uid == this.uid
        }
        return false
    }
}