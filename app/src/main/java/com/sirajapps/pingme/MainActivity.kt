package com.sirajapps.pingme

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.sirajapps.pingme.activitiyuis.MainScreen
import com.sirajapps.pingme.adapters.StatusAdapter
import com.sirajapps.pingme.adapters.UsersAdapter
import com.sirajapps.pingme.adapters.UsersAdapterClicks
import com.sirajapps.pingme.models.*
import com.sirajapps.pingme.navigation.ScreenCallbacks
import com.sirajapps.pingme.storage.AllRepository
import com.sirajapps.pingme.storage.MyViewModel
import com.sirajapps.pingme.storage.UserOfflineAndUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.collections.ArrayList
import android.net.Uri
import android.widget.Toast
import com.sirajapps.pingme.storage.FirebaseQueryLiveData
import com.sirajapps.pingme.ui.theme.PingMeTheme
import java.util.*


class MainActivity : ComponentActivity(), ScreenCallbacks, UsersAdapterClicks {
    private lateinit var database: FirebaseDatabase
    private val usersList = ArrayList<UserOfflineAndUser>()
    private val cachedFriendsList = ArrayList<UserOfflineAndUser>()
    private val userStatusList = ArrayList<UserStatus>()
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var statusAdapter: StatusAdapter
    private lateinit var viewModel: MyViewModel
    private lateinit var repository: AllRepository
    private var changeOnline = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usersAdapter = UsersAdapter(this, usersList, this)
        statusAdapter = StatusAdapter(this, userStatusList)
        val fireUser = Firebase.auth.currentUser
        database = Firebase.database
        setContent {
            PingMeTheme() {
                MainScreen(listener = this) {
                    changeOnline = false
                    val pickIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    pickIntent.type = "image/* video/*"
                    getContent.launch(pickIntent)
                }
            }
        }
        viewModel = ViewModelProvider(this)[MyViewModel::class.java]
        repository = viewModel.getRepository()
        val intent = intent
        val isNewUser = !intent.getBooleanExtra("isOnStart", true)
        if (isNewUser) {
            if (Constants.currentUser != null) {
                viewModel.addUser(Constants.currentUser!!)
                val userOffline = UserOffline(uid = Constants.currentUser!!.uid)
                Constants.curUserUid = Constants.currentUser!!.uid
                viewModel.insertUserOffline(userOffline) {
                    offlineUsers()
                }
            } else {
                val user = User(
                    fireUser!!.uid, fireUser.displayName.toString(),
                    email = fireUser.email!!
                )
                val offlineUser = UserOffline(uid = user.uid)
                Constants.currentUser = user
                Constants.curUserUid = user.uid
                viewModel.addUser(user)
                viewModel.insertUserOffline(offlineUser) {
                    offlineUsers()
                }
            }
            Constants.allChatsRef = database.reference.child("Chats")
                .child(Constants.curUserUid)
            viewModel.getSingleListener(
                database.reference.child("Users").child(Constants.curUserUid)
            )
                .observe(this) {
                    if (it == null) return@observe
                    val data = it.data
                    val user = data.getValue<User>()
                    if (user != null) {
                        Constants.currentUser = user
                        viewModel.updateUser(user)
                    }
                }
        } else {
            offlineUsers()
            lifecycleScope.launch {
                Constants.currentUser = repository.getUserById(fireUser!!.uid)
            }
            Constants.curUserUid = fireUser!!.uid
            Constants.allChatsRef = database.reference.child("Chats")
                .child(Constants.curUserUid)
        }

        val usersLiveData = viewModel.usersLiveData
        usersLiveData.observe(this) {
            if (it != null) {
                val data = it.data
                val lastMessage = data.child("lastMessage").getValue<Message?>()
                val ind = usersList.indexOf(
                    UserOfflineAndUser(
                        User(uid = data.key!!),
                        UserOffline(uid = data.key!!)
                    )
                )
                if (ind >= 0) {
                    val tempUsr = usersList[ind]
                    usersList.removeAt(ind)
                    tempUsr.offlineUser.lastMsg = lastMessage
                    viewModel.updateUserOffline(tempUsr.offlineUser)
                    usersList.add(0, tempUsr)
                    usersAdapter.notifyItemMoved(ind, 0)
                    usersAdapter.notifyItemChanged(0)
                } else {
                    viewModel.getSingleListener(
                        database.reference.child("Users").child(data.key!!)
                    ).observe(this) { obj ->
                        if (obj != null) {
                            val data1 = obj.data
                            val friend = data1.getValue<User>()
                            val friendOff = UserOffline(uid = friend!!.uid, lastMsg = lastMessage)
                            val friendOffline = UserOfflineAndUser(friend, friendOff)
                            viewModel.addUser(friend)
                            viewModel.insertUserOffline(friendOff)
                            viewModel.addChat(Chat(uid = friend.uid)) {
                                usersList.add(0, friendOffline)
                                cachedFriendsList.add(friendOffline)
                                usersAdapter.notifyItemInserted(0)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setStatus(true)
        changeOnline = true
    }

    override fun onPause() {
        super.onPause()
        if(changeOnline)
        setStatus(false)
    }

    override fun statusRecycle(recyclerView: RecyclerView) {
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = statusAdapter
    }

    override fun usersRecycle(recyclerView: RecyclerView) {
        recyclerView.itemAnimator = null
        recyclerView.adapter = usersAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun offlineUsers() {
        lifecycleScope.launch {
            var all = repository.getAllUseRelations()
            all = all.sortedByDescending { it.offlineUser.lastMsg?.time }
            usersList.addAll(all)
            cachedFriendsList.addAll(usersList)
            userStatusList.addAll(repository.getAllUserStatus())
            withContext(Dispatchers.Main) {
                if (usersList.size > 0) {
                    usersAdapter.notifyItemRangeChanged(0, usersList.size)
                }
                if (userStatusList.size > 0) {
                    statusAdapter.notifyItemRangeChanged(0, userStatusList.size)
                }
            }
        }
    }

    override fun itemClicked(user: UserOfflineAndUser, pos: Int) {
        newUser(user.user, pos)
    }

    private fun newUser(user: User, pos: Int) {
        lifecycleScope.launch {
            if (!repository.checkChat(user.uid)) {
                repository.insertUser(user)
                repository.insertUserOffline(UserOffline(uid = user.uid))
                repository.insertChat(Chat(uid = user.uid))
            }
            val userOffline: UserOffline = viewModel.getRepository().getOfflineUserById(user.uid)

            val userString = Json.encodeToString(user)
            val userOfflineString: String = Json.encodeToString(userOffline)
            withContext(Dispatchers.Main) {
                changeOnline = false
                val intent = Intent(this@MainActivity, ChatActivity::class.java)
                intent.putExtra("user", userString)
                intent.putExtra("userOffline", userOfflineString)
                startActivity(intent)
            }
        }
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val data = it.data ?: return@registerForActivityResult
            val selectedMediaUri: Uri = data.data ?: return@registerForActivityResult
            if (selectedMediaUri.toString().contains("image")) {
                Toast.makeText(this@MainActivity, "ImageSelected", Toast.LENGTH_SHORT).show()
            } else if (selectedMediaUri.toString().contains("video")) {
                Toast.makeText(this@MainActivity, "VideoSelected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun uploadStatus() {

    }

    fun searchUser(name: String) {
        val listToAdd = if (name.isEmpty()) {
            cachedFriendsList
        } else {
            cachedFriendsList.filter {
                it.user.name.contains(name, true) ||
                        it.user.email.contains(name, true)
            }
        }
        usersList.clear()
        usersList.addAll(listToAdd)
        usersAdapter.notifyDataSetChanged()
    }
    private fun setStatus(status:Boolean){
        database.reference
            .child("Online")
            .child(Constants.curUserUid)
            .child("isOnline")
            .setValue(status)
    }
    fun changeUserUpdate(status:Boolean){
        changeOnline = status
    }
}