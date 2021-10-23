package com.sirajapps.pingme

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.sirajapps.pingme.activitiyuis.SearchUserUi
import com.sirajapps.pingme.activitiyuis.allUsersList
import com.sirajapps.pingme.activitiyuis.cachedUsersList
import com.sirajapps.pingme.models.Chat
import com.sirajapps.pingme.models.Constants
import com.sirajapps.pingme.models.User
import com.sirajapps.pingme.models.UserOffline
import com.sirajapps.pingme.storage.AllRepository
import com.sirajapps.pingme.storage.MyViewModel
import com.sirajapps.pingme.ui.theme.PingMeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NewUserActivity : ComponentActivity() {
    private lateinit var viewModel: MyViewModel
    private lateinit var repository: AllRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PingMeTheme {
                SearchUserUi{ user, pos ->
                    newUser(user,pos)
                }
            }
        }
        viewModel = ViewModelProvider(this)[MyViewModel::class.java]
        repository = viewModel.getRepository()
        updateUsers(allUsersList)
    }
    private fun newUser(user: User, pos: Int) {
        lifecycleScope.launch {
            if(!repository.checkChat(user.uid)){
                repository.insertUser(user)
                repository.insertUserOffline(UserOffline(uid = user.uid))
                repository.insertChat(Chat(uid = user.uid))
            }
            val userOffline: UserOffline? = if(viewModel.getRepository().checkUserOffline(user.uid)){
                viewModel.getRepository().getOfflineUserById(user.uid)
            }
            else{
                null
            }
            val userString = Json.encodeToString(user)
            val userOfflineString:String? = if(userOffline!=null){
                Json.encodeToString(userOffline)
            }
            else{
                null
            }
            withContext(Dispatchers.Main) {
                val intent = Intent(this@NewUserActivity,ChatActivity::class.java)
                intent.putExtra("user",userString)
                if(userOfflineString!=null) {
                    intent.putExtra("userOffline", userOfflineString)
                }
                startActivity(intent)
            }
        }
    }
    private fun updateUsers(list: MutableState<ArrayList<User>>) {
        viewModel.getSingleListener(Firebase.database.reference.child("Users"))
            .observe(this,{
                if(it==null)return@observe
                val data = it.data
                val temList = ArrayList<User>()
                data.children.forEach { snap->
                    snap.getValue<User>()?.let { it1 -> temList.add(it1) }
                }
                list.value = temList
                cachedUsersList = list.value
            })

    }

    override fun onPause() {
        super.onPause()
        setStatus(false)
    }

    override fun onResume() {
        super.onResume()
        setStatus(true)
    }
    private fun setStatus(status:Boolean){
        Firebase.database.reference
            .child("Online")
            .child(Constants.curUserUid)
            .child("isOnline")
            .setValue(status)
    }
}
