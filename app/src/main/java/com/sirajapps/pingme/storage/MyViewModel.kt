package com.sirajapps.pingme.storage

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sirajapps.pingme.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class MyViewModel(application: Application): AndroidViewModel(application) {
    private val database = OfflineDatabase.getDatabase(application)
    private val dao = database.getDao()
    private val repository = AllRepository(dao)
    private var chatLiveData:FirebaseQueryLiveData? = null
    val usersLiveData by lazy {
        FirebaseQueryLiveData(Constants.allChatsRef!!,5)
    }
    fun getRepository():AllRepository = repository

    fun addUser(user:User,done:(user:User)->Unit ={}){
        viewModelScope.launch(Dispatchers.IO){
            repository.insertUser(user)
            withContext(Dispatchers.Main){
                done(user)
            }
        }
    }
    fun updateUser(user:User){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUser(user)
        }
    }
    fun addChat(chat:Chat,done:(chat:Chat)->Unit ={}) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertChat(chat)
            withContext(Dispatchers.Main){
                done(chat)
            }
        }
    }
    fun updateChat(chat: Chat) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateChat(chat)
        }
    }
    fun insertUserStatus(status: UserStatus,done:(status:UserStatus)->Unit ={}){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUserStatus(status)
            withContext(Dispatchers.Main){
                done(status)
            }
        }
    }
    fun updateUserStatus(status: UserStatus){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserStatus(status)
        }
    }
    fun insertUserOffline(userOffline: UserOffline,done:(user:UserOffline)->Unit ={}){
        viewModelScope.launch(Dispatchers.IO) {
        repository.insertUserOffline(userOffline)
            withContext(Dispatchers.Main) {
                done(userOffline)
            }
        }
    }
    fun updateUserOffline(userOffline: UserOffline){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserOffline(userOffline)
        }
    }
    fun getSingleListener(ref:DatabaseReference):FirebaseQueryLiveData{
        return FirebaseQueryLiveData(ref,FirebaseQueryLiveData.singleEventType)
    }
    fun sendMessage(ref:DatabaseReference,msg:Message,complete:(msg:Message)->Unit) {
        viewModelScope.launch {
            ref.setValue(msg).await()
            complete(msg)
        }
    }
    fun getChatLiveData(ref: DatabaseReference): FirebaseQueryLiveData {
        return if(chatLiveData!=null){
            chatLiveData!!
        } else{
            chatLiveData = FirebaseQueryLiveData(ref,FirebaseQueryLiveData.childAddAndRemoveType)
            chatLiveData!!
        }
    }
    fun addMessageToDatabase(uid:String,message:Message){
        GlobalScope.launch {
            val chat =  repository.getChatOfUser(uid)[0]
            val list = chat.chat.messages
            list.add(message)
            chat.chat.messages = list
            updateChat(chat.chat)
        }
    }
    fun updateLastMsg(ref1: DatabaseReference, ref2: DatabaseReference, it: Message) {
       viewModelScope.launch {
           ref1.setValue(it)
           ref2.setValue(it)
       }
    }
    fun uploadImage(bitmap: Bitmap,onComplete:(imageUri:String)->Unit){
        viewModelScope.launch {
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0, bos)
            val inputStream: ByteArray = bos.toByteArray()
            val name = Firebase.database.reference.push().key!!
            val imageUri: String = run {
                Firebase.storage.reference
                    .child("Chats")
                    .child("Images")
                    .child(Constants.curUserUid)
                    .child(name)
                    .putBytes(inputStream)
                    .await()
                Firebase.storage.reference
                    .child("Chats")
                    .child("Images")
                    .child(Constants.curUserUid)
                    .child(name)
                    .downloadUrl
                    .await()
                    .toString()
            }
            onComplete(imageUri)
        }
    }
}


