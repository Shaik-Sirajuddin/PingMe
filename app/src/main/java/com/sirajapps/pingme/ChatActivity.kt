package com.sirajapps.pingme

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.sirajapps.pingme.activitiyuis.*
import com.sirajapps.pingme.models.*
import com.sirajapps.pingme.navigation.ChatScreenCallBacks
import com.sirajapps.pingme.navigation.chatNavigation
import com.sirajapps.pingme.storage.AllRepository
import com.sirajapps.pingme.storage.Data
import com.sirajapps.pingme.storage.MyViewModel
import com.sirajapps.pingme.ui.theme.PingMeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : ComponentActivity(), ChatScreenCallBacks {
    private lateinit var viewModel: MyViewModel
    private lateinit var repository: AllRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var chatPerson: User
    private lateinit var userOffline: UserOffline
    private lateinit var soundPool: SoundPool
    private var sendSound: Int = 0
    private var receiveSound: Int = 0
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val userString = intent.getStringExtra("user")!!
        val userOfflineString = intent.getStringExtra("userOffline")!!
        chatPerson = Json.decodeFromString(userString)
        userOffline = Json.decodeFromString(userOfflineString)
        if (hisUid != chatPerson.uid) {
            messages.value = ArrayList()
        }
        hisUid = chatPerson.uid
        setContent {
            PingMeTheme {
                chatNavigation(context = this,
                    user = chatPerson,
                    userOffline = userOffline,
                    listener = this)
            }
        }
        viewModel = ViewModelProvider(this)[MyViewModel::class.java]
        repository = viewModel.getRepository()
        auth = Firebase.auth
        database = Firebase.database
        lifecycleScope.launch(Dispatchers.Default) {
            val chat = repository.getChatOfUser(chatPerson.uid)[0]
            withContext(Dispatchers.Main) {
                updateMessages(chat.chat.messages)
            }
        }
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(audioAttributes)
            .build()

        val chatData = viewModel.getChatLiveData(
            Firebase.database.reference.child("Chats")
                .child(Constants.curUserUid)
                .child(chatPerson.uid)
                .child("Messages")
        )
        chatData.observe(this) {
            if (it == null) return@observe
            if (it.type == Data.childRemoved) {

            } else {
                val data = it.data
                val message = data.getValue<Message>()
                addMessage(message!!)
                soundPool.play(receiveSound, 0.75f, 0.75f, 0, 0, 1f)
                Firebase.database.reference
                    .child("Chats")
                    .child(Constants.curUserUid)
                    .child(chatPerson.uid)
                    .child("Messages")
                    .child(message.messageId)
                    .removeValue()
                viewModel.addMessageToDatabase(chatPerson.uid, message)
            }
        }
        sendSound = soundPool.load(this, R.raw.sendsound, 1)
        receiveSound = soundPool.load(this, R.raw.receive_sound, 1)

    }

    override fun sendMessage(msg: String, imageUri: String?,id:DatabaseReference?) {
        val msgId = id ?:database.reference
            .child("Chats")
            .child(chatPerson.uid)
            .child(Constants.curUserUid)
            .child("Messages")
            .push()
        val message = Message(message = msg,
            time = Date().time,
            messageId = msgId.key.toString(),
            senderId = auth.uid!!,
            onlineImageUri = imageUri
        )
        val ref1 = database.reference.child("Chats").child(chatPerson.uid).child(Constants.curUserUid)
            .child("lastMessage")
        val ref2 = database.reference.child("Chats").child(Constants.curUserUid).child(chatPerson.uid)
            .child("lastMessage")
        viewModel.sendMessage(msgId, message) {
                viewModel.updateLastMsg(ref1, ref2, it)
                userOffline.lastMsg = it
                viewModel.updateUserOffline(userOffline)
        }
        if(imageUri == null) {
            addMessage(message)
            viewModel.addMessageToDatabase(chatPerson.uid, message)
            soundPool.play(sendSound, 0.5f, 0.5f, 0, 0, 1f)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        focus = false
        soundPool.release()
    }

    override fun onPause() {
        super.onPause()
        setStatus(false)
    }

    override fun onResume() {
        super.onResume()
        setStatus(true)
    }

    private fun setStatus(status: Boolean) {
        Firebase.database.reference
            .child("Online")
            .child(Constants.curUserUid)
            .child("isOnline")
            .setValue(status)
    }

    fun pickContent() {
        val pickIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/* video/*"
        getContent.launch(pickIntent)
    }
    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val data = it.data ?: return@registerForActivityResult
            val selectedMediaUri: Uri = data.data ?: return@registerForActivityResult
            if (selectedMediaUri.toString().contains("image")) {
                handleImageSelected(selectedMediaUri)
            } else if (selectedMediaUri.toString().contains("video")) {
                Toast.makeText(this@ChatActivity, "VideoSelected", Toast.LENGTH_SHORT).show()
            }
        }

    fun getImageBitmap(context: Context, uri: Uri): Bitmap {
        return try {
            val imageStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(imageStream)
        }catch(e:FileNotFoundException){
            val file = File(uri.path!!).inputStream()
            BitmapFactory.decodeStream(file)
        }
    }

    private fun updateMessageOffline(message:Message){
        addMessage(message)
        viewModel.addMessageToDatabase(chatPerson.uid, message)
        soundPool.play(sendSound, 0.5f, 0.5f, 0, 0, 1f)
    }
    private fun handleImageSelected(imageUri:Uri){
        val msgId = database.reference
            .child("Chats")
            .child(chatPerson.uid)
            .child(Constants.curUserUid)
            .child("Messages")
            .push()
        val date = Date().time
        val uri = OfflineSaver.copyFile(
            this,imageUri,fileName = date.toString()+OfflineSaver.IMAGE,
             outputPath = OfflineSaver.CHAT_IMAGE_PATH)
        val message = Message(message = "",
            time = date,
            messageId = msgId.key.toString(),
            senderId = auth.uid!!,
            onlineImageUri = imageUri.toString(),
            offlineImageUri = uri
        )
        val image = getImageBitmap(this, Uri.parse(uri))
        updateMessageOffline(message)
        viewModel.uploadImage(image) { imUri ->
            sendMessage("", imUri,msgId)
        }
    }
}
