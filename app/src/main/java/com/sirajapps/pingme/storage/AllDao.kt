package com.sirajapps.pingme.storage

import androidx.room.*
import com.sirajapps.pingme.models.Chat
import com.sirajapps.pingme.models.User
import com.sirajapps.pingme.models.UserOffline
import com.sirajapps.pingme.models.UserStatus

@Dao
interface AllDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE )
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: Chat)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStatus(status:UserStatus)

    @Update
    suspend fun updateUserStatus(status: UserStatus)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserOffline(userOffline: UserOffline)

    @Update
    suspend fun updateUserOffline(userOffline: UserOffline)

    @Update
    suspend fun updateUser(user:User)

    @Update
    suspend fun updateChat(chat: Chat)

    @Query("SELECT EXISTS(SELECT * FROM user_table WHERE uid = :id)")
    suspend fun checkUser(id:String):Boolean

    @Query("SELECT EXISTS(SELECT * FROM chat_table WHERE uid = :id)")
    suspend fun checkChat(id:String):Boolean

    @Query("SELECT * FROM user_table")
    suspend fun getAllUsers():List<User>

    @Transaction
    @Query("SELECT * FROM user_table WHERE uid = :id LIMIT 1")
    suspend fun getChatWithUserId(id:String):List<UserAndChat>

    @Query("SELECT EXISTS(SELECT * FROM user_offline_table WHERE uid = :id)")
    suspend fun checkUserOffline(id: String):Boolean

    @Transaction
    @Query("SELECT * FROM user_table WHERE uid = :id LIMIT 1")
    suspend fun getUserOffRelationByUserId(id: String):List<UserOfflineAndUser>

    @Transaction
    @Query("SELECT * FROM user_table")
    suspend fun getAllUserOfflineRelations():List<UserOfflineAndUser>

    @Query("SELECT * FROM user_status_table")
    suspend fun getAllUserStatus():List<UserStatus>

    @Query("SELECT * FROM user_table WHERE uid = :id LIMIT 1")
    suspend fun getUserById(id:String):User

    @Query("SELECT * FROM user_offline_table WHERE uid = :id LIMIT 1")
    suspend fun getOfflineUserById(id:String):UserOffline
}