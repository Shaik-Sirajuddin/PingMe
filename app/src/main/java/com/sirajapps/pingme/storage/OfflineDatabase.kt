package com.sirajapps.pingme.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sirajapps.pingme.models.Chat
import com.sirajapps.pingme.models.User
import com.sirajapps.pingme.models.UserOffline
import com.sirajapps.pingme.models.UserStatus


@Database(entities = [User::class, Chat::class,UserOffline::class,UserStatus::class,],version = 1,exportSchema = false)
@TypeConverters(Converters::class)
abstract class OfflineDatabase: RoomDatabase(){

    abstract fun getDao():AllDao

    companion object {
        @Volatile
        private var INSTANCE: OfflineDatabase? = null

        fun getDatabase(context: Context): OfflineDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OfflineDatabase::class.java,
                    "all_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}