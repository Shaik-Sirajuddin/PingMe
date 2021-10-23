package com.sirajapps.pingme.storage

import androidx.room.Embedded
import androidx.room.Relation
import com.sirajapps.pingme.models.User
import com.sirajapps.pingme.models.UserOffline
import java.util.*

data class UserOfflineAndUser(
    @Embedded
    val user: User,
    @Relation(
        parentColumn = "uid",
        entityColumn = "uid"
    )
    val offlineUser:UserOffline
){
    override fun hashCode(): Int {
        return Objects.hash(user.uid)
    }

    override fun equals(other: Any?): Boolean {
        if(other === this)return true
        if(other==null)return false
        if(other.javaClass == javaClass){
            other as UserOfflineAndUser
            return other.user.uid == this.user.uid
        }
        return false
    }
}
