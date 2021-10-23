package com.sirajapps.pingme.models

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    var messageId:String = "",
    var message:String = "",
    var senderId:String = "",
    var onlineImageUri:String? = null,
    var offlineImageUri:String? = null,
    var time:Long  = 0)