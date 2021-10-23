package com.sirajapps.pingme.activitiyuis

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sirajapps.pingme.ChatActivity
import com.sirajapps.pingme.R
import com.sirajapps.pingme.models.Message
import com.sirajapps.pingme.models.User
import com.sirajapps.pingme.models.UserOffline
import com.sirajapps.pingme.navigation.ChatScreenCallBacks
import com.sirajapps.pingme.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

val status = mutableStateOf("Online")
val messages = mutableStateOf(ArrayList<Message>())
val listState = LazyListState()
var focus = false
var hisUid: String = ""

@Composable
fun ChatUi(
    context: Context,
    userOffline: UserOffline?,
    user: User,
    navController: NavHostController,
    listener: ChatScreenCallBacks,
) {
    val img by remember {
        if (userOffline?.offlineImage != null)
            mutableStateOf(userOffline.offlineImage)
        else {
            mutableStateOf(user.onlineImageUri)
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painterResource(id = R.drawable.whatsapp_background_chat),
            contentDescription = "background",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(
            Modifier
                .fillMaxSize()
        ) {
            val (toolBar, chat, msgBox) = createRefs()
            Box(modifier = Modifier
                .constrainAs(toolBar) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                }
                .padding(top = 0.dp)) {
                ToolBar(context = context, image = img, name = user.name)
            }
            Box(modifier = Modifier.constrainAs(chat) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(toolBar.bottom)
                bottom.linkTo(msgBox.top)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            }) {
                ChatRecycle(uid = user.uid, context)
                if (messages.value.size > 0) {
                    //SideEffect {
                    context as ChatActivity
                    context.lifecycleScope.launch {
                        if (messages.value.size > 0) {
                           // listState.scrollToItem(messages.value.size - 1)
                        }
                    }
                    //}
                }
            }
            Box(modifier = Modifier.constrainAs(msgBox) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            }) {
                SendBox(listener)
            }
        }

    }
}

@Composable
fun ChatRecycle(uid: String, context: Context) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        itemsIndexed(messages.value) { index, msg ->
            if (msg.senderId == uid) {
                val date = SimpleDateFormat("hh:mm a")
                Box(modifier = Modifier.fillMaxWidth()) {
                    ReceivedMessageBox(
                        msg = msg.message,
                        date = date.format(Date(msg.time)),
                        image = msg.offlineImageUri ?: msg.onlineImageUri,
                        //isOnline = msg.onlineImageUri==null
                    )
                }
            } else {
                val date = SimpleDateFormat("hh:mm a")
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    SentMessageBox(
                        msg = msg.message,
                        date = date.format(Date(msg.time)),
                        image = msg.onlineImageUri,
                        //isOnline = msg.offlineImageUri==null
                    )
                }
            }
        }
    }

}

@Composable
fun ReceivedMessageBox(
    msg: String,
    image: String? = null,
    date: String,
    isOnline:Boolean = true
) {
    Box(
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxWidth(0.5f)
            .padding(3.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .padding(5.dp)
                .background(
                    ChatColor,
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 20.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    )
                )
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Column(horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceEvenly) {
                if (image != null) {
                    val context = LocalContext.current
                    var bitmapState by remember {
                        mutableStateOf(BitmapFactory.decodeResource(context.resources,
                            R.drawable.profile_image).asImageBitmap())
                    }
                    val scope = rememberCoroutineScope()
                    SideEffect {
                        scope.launch(Dispatchers.IO){
                            Glide.with(context).asBitmap().load(image).into(
                                object : CustomTarget<Bitmap>() {
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap>?,
                                    ) {
                                        bitmapState = resource.asImageBitmap()
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {}
                                }
                            )
                        }
                    }
                    Image(
                        bitmap = bitmapState,
                        contentDescription = "Image",
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(10.dp)
                            )
                            .sizeIn(10.dp, 10.dp, 200.dp, 200.dp)
                    )
                }
                Text(
                    text = msg,
                    color = DarkWhite,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(end = 20.dp)
                )
                Text(
                    text = date,
                    color = SearchBarText,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }
}

@Composable
fun SentMessageBox(
    msg: String,
    image: String? = null,
    isOnline: Boolean = true,
    date: String,
) {
    Box(
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxWidth(0.5f)
            .padding(3.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .padding(2.dp)
                .background(
                    GreenMessage,
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 0.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    )
                )
                .padding(horizontal = 10.dp, vertical = 5.dp)

        ) {
            Column(horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceEvenly) {
                if (image != null) {
                    val context = LocalContext.current as ChatActivity
                    if(isOnline) {
                        Image(
                            painter = rememberImagePainter(image,
                            builder={
                                placeholder(R.drawable.profile_image)
                            }),
                        contentDescription = "image",
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(10.dp)
                            )
                            .sizeIn(10.dp, 10.dp, 200.dp, 200.dp)
                        )
                    }else{

                        val bitmap = context.getImageBitmap(context,Uri.parse(image))
                        Image(
                            bitmap =bitmap.asImageBitmap(),
                            contentDescription = "Image",
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(10.dp)
                                )
                                .sizeIn(10.dp, 10.dp, 200.dp, 200.dp)
                        )
                    }
                }
                Text(
                    text = msg,
                    color = DarkWhite,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(end = 20.dp)
                )
                Text(
                    text = date,
                    color = SearchBarText,
                    fontSize = 10.sp
                )
            }

        }
    }
}


@Composable
fun ToolBar(context: Context, image: String?, name: String) {
    context as ChatActivity
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .background(Color.Black),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painterResource(id = R.drawable.ic_baseline_arrow_back_24),
            contentDescription = "back",
            colorFilter = ColorFilter.tint(DarkWhite),
            modifier = Modifier
                .size(40.dp)
                .background(Color.Transparent, CircleShape)
                .padding(10.dp)
                .clickable {
                    context.finish()
                },
        )
        Image(
            painter = rememberImagePainter(image,
                builder = {
                    placeholder(R.drawable.profile_image)
                }),
            contentDescription = "profilePhoto",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(start = 0.dp)
                .size(35.dp)
                .clip(CircleShape)
        )
        Column(
            modifier = Modifier
                .padding(start = 12.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = name,
                color = DarkWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = status.value,
                fontSize = 14.sp,
                color = DarkWhite,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
fun SendBox(listener: ChatScreenCallBacks) {

    val context = LocalContext.current as ChatActivity
    var sendMsg by remember {
        mutableStateOf("")
    }
    var sendImgId by remember {
        mutableStateOf(R.drawable.ic_baseline_mic_24)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(0.88f)
                .background(ChatColor, RoundedCornerShape(30.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(R.drawable.ic_baseline_emoji_emotions_24),
                    contentDescription = "emoji",
                    modifier = Modifier
                        .background(Color.Transparent)
                        .padding(horizontal = 10.dp)
                        .size(25.dp)
                )
                TextField(
                    value = sendMsg,
                    onValueChange = {
                        sendMsg = it
                        sendImgId = if (it.isEmpty()) {
                            R.drawable.ic_baseline_mic_24
                        } else {
                            R.drawable.ic_baseline_send_24
                        }
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        cursorColor = DarkWhite
                    ),
                    textStyle = TextStyle(DarkWhite, 18.sp),
                    modifier = Modifier
                        .background(ChatColor)
                        .fillMaxWidth(0.85f)
                        .padding(0.dp)
                        .onFocusChanged {
                            if (!it.isFocused && focus) {
                                focus = false
                                context.finish()
                            } else {
                                focus = true
                            }
                        },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                    )
                )
                Image(
                    painterResource(id = R.drawable.link),
                    contentDescription = "link",
                    colorFilter = ColorFilter.tint(DarkWhite),
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(18.dp)
                        .clickable {
                            context.pickContent()
                        }
                )
            }
        }
        Image(
            painter = painterResource(id = sendImgId),
            contentDescription = "mic",
            modifier = Modifier
                .padding(0.dp)
                .clip(CircleShape)
                .size(45.dp)
                .background(GreenLime, CircleShape)
                .padding(10.dp)
                .clickable {
                    if (sendMsg.isNotEmpty()) {
                        val tempMsg = sendMsg
                        sendMsg = ""
                        listener.sendMessage(tempMsg)
                    }
                },
            colorFilter = ColorFilter.tint(DarkWhite)
        )
    }
}

fun updateMessages(list: ArrayList<Message>) {
    messages.value = list
}

fun addMessage(msg: Message) {
    val list = ArrayList<Message>()
    list.addAll(messages.value)
    list.add(msg)
    messages.value = list
}