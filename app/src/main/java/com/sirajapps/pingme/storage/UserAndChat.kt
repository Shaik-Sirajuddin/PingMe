package com.sirajapps.pingme.storage

import androidx.room.Embedded
import androidx.room.Relation
import com.sirajapps.pingme.models.Chat
import com.sirajapps.pingme.models.User

data class UserAndChat(
    @Embedded
    val user: User,
    @Relation(
        parentColumn = "uid",
        entityColumn = "uid"
    )
    val chat: Chat
)
