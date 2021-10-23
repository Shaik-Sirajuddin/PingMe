package com.sirajapps.pingme.storage

import androidx.room.TypeConverter
import com.sirajapps.pingme.models.Message
import com.sirajapps.pingme.models.Status
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun fromMessageList(value : ArrayList<Message>) = Json.encodeToString(value)

    @TypeConverter
    fun toMessageList(value: String) = Json.decodeFromString<ArrayList<Message>>(value)

    @TypeConverter
    fun toStatusList(value:String) = Json.decodeFromString<ArrayList<Status>>(value)

    @TypeConverter
    fun fromStatusList(value: ArrayList<Status>) = Json.encodeToString(value)

    @TypeConverter
    fun toStatus(value:String) = Json.decodeFromString<Status?>(value)

    @TypeConverter
    fun fromStatus(value:Status?) = Json.encodeToString(value)

    @TypeConverter
    fun fromMessage(value: Message?) = Json.encodeToString(value)

    @TypeConverter
    fun  toMessage(value: String) = Json.decodeFromString<Message?>(value)


}
