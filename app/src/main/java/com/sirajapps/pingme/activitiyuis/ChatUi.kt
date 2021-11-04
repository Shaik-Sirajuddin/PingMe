package com.sirajapps.pingme.activitiyuis

import android.content.Context
import android.view.LayoutInflater
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import androidx.recyclerview.widget.RecyclerView
import coil.compose.rememberImagePainter
import com.sirajapps.pingme.ChatActivity
import com.sirajapps.pingme.R
import com.sirajapps.pingme.models.User
import com.sirajapps.pingme.models.UserOffline
import com.sirajapps.pingme.navigation.ChatScreenCallBacks
import com.sirajapps.pingme.ui.theme.*

val status = mutableStateOf("Online")
var focus = false
var hisUid: String = ""

@Composable
fun ChatUi(
    context: Context,
    userOffline: UserOffline?,
    user: User,
    listener: ChatScreenCallBacks,
    messageRecycle:(recycle:RecyclerView)->Unit
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
               AndroidView(factory = { context ->
                   val view = LayoutInflater.from(context).inflate(R.layout.users_recyclerview,null,false)
                   val recycle = view.findViewById<RecyclerView>(R.id.usersRecyclerView)
                   messageRecycle(recycle)
                   return@AndroidView view
               },
               modifier = Modifier.fillMaxWidth())
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