package com.sirajapps.pingme.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "user_offline_table")
data class UserOffline(
    @PrimaryKey(autoGenerate = true)
    var key:Int = 0,
    var uid:String = "",
    var offlineImage:String? = null,
    var lastMsg:Message? = null)