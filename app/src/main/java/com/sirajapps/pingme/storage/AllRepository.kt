package com.sirajapps.pingme.storage

import com.sirajapps.pingme.models.Chat
import com.sirajapps.pingme.models.User
import com.sirajapps.pingme.models.UserOffline
import com.sirajapps.pingme.models.UserStatus


class AllRepository(private val allDao: AllDao){

    suspend fun insertUser(user: User){
        allDao.insertUser(user)
    }
    suspend fun insertChat(chat: Chat){
        allDao.insertChat(chat)
    }
    suspend fun updateUser(user: User){
        allDao.updateUser(user)
    }
    suspend fun updateChat(chat: Chat){
        allDao.updateChat(chat)
    }
    suspend fun checkChat(id:String) = allDao.checkChat(id)
    suspend fun getAllUsers():List<User> = allDao.getAllUsers()

    suspend fun getChatOfUser(userId:String):List<UserAndChat> = allDao.getChatWithUserId(userId)

    suspend fun checkUserExistence(userId:String):Boolean = allDao.checkUser(userId)

    suspend fun insertUserStatus(status: UserStatus){
        allDao.insertUserStatus(status)
    }
    suspend fun updateUserStatus(status: UserStatus){
        allDao.updateUserStatus(status)
    }
    suspend fun insertUserOffline(userOffline: UserOffline){
        allDao.insertUserOffline(userOffline)
    }
    suspend fun updateUserOffline(userOffline: UserOffline){
        allDao.updateUserOffline(userOffline)
    }
    suspend fun checkUserOffline(id: String):Boolean = allDao.checkUserOffline(id)

    suspend fun getUserRelationById(id: String):List<UserOfflineAndUser> = allDao.getUserOffRelationByUserId(id)

    suspend fun getAllUseRelations():List<UserOfflineAndUser> = allDao.getAllUserOfflineRelations()

    suspend fun getAllUserStatus():List<UserStatus> = allDao.getAllUserStatus()

    suspend fun getUserById(id:String) = allDao.getUserById(id)

    suspend fun getOfflineUserById(id:String) = allDao.getOfflineUserById(id)
}