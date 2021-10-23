package com.sirajapps.pingme.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = "user_status_table")
data class UserStatus
    (
    @PrimaryKey(autoGenerate = false)
    var userUid:String= "",
     var userName:String = "",
     var statusList:ArrayList<Status> = ArrayList(),
     var lastStatus:Status = Status()){
    override fun hashCode(): Int {
        return Objects.hash(userUid)
    }

    override fun equals(other: Any?): Boolean {
        if(other === this)return true
        if(other==null)return false
        if(other.javaClass == javaClass){
            other as UserStatus
            return other.userUid == this.userUid
        }
        return false
    }
 }