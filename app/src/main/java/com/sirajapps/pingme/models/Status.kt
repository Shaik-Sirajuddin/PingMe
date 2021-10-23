package com.sirajapps.pingme.models

import kotlinx.serialization.Serializable

@Serializable
data class Status
    (
    var userUid:String ="",
     var imageUri:String ="",
     var offlineImg:String = "",
     var videoUri:String? = null)