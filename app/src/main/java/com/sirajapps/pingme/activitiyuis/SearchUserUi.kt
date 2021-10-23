package com.sirajapps.pingme.activitiyuis

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.sirajapps.pingme.NewUserActivity
import com.sirajapps.pingme.R
import com.sirajapps.pingme.models.User
import com.sirajapps.pingme.ui.theme.DarkBackground
import com.sirajapps.pingme.ui.theme.DarkWhite
import com.sirajapps.pingme.ui.theme.MyGreen
import com.sirajapps.pingme.ui.theme.MyGrey


val allUsersList = mutableStateOf(ArrayList<User>())
var cachedUsersList = ArrayList<User>()
@Composable
fun SearchUserUi(newUser:(user: User, pos:Int)->Unit){
    val context = LocalContext.current as NewUserActivity
    Column(modifier = Modifier
        .fillMaxSize()
        .background(DarkBackground),
        horizontalAlignment = Alignment.Start) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = R.drawable.ic_baseline_arrow_back_24),
                contentDescription = "Back",
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp)
                    .size(30.dp)
                    .clickable {
                        context.finish()
                    },
                colorFilter = ColorFilter.tint(DarkWhite))
            Text(
                text = "New Chat",
                color = DarkWhite,
                fontSize = 18.sp,
                modifier = Modifier.padding(10.dp)
            )
        }
        SearchBar(modifier = Modifier.padding(top = 10.dp)){
            searchUpdate(it.trim())
        }
        LazyColumn(modifier = Modifier.fillMaxSize()){
            itemsIndexed(allUsersList.value) { pos, user ->
                if (user.onlineImageUri != null) {
                    UserView(imgUrl = user.onlineImageUri!!, name = user.name){
                        newUser(user,pos)
                    }
                }
                else{
                    UserView( name = user.name){
                        newUser(user,pos)
                    }
                }
            }
        }
    }
}
@Composable
fun UserView(imgUrl:String = "R.drawable.profile_image",name:String,click:()->Unit){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .wrapContentHeight()
        .background(DarkBackground)
        .clickable {
            click()
        },
        verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = rememberImagePainter(imgUrl,
                builder = {
                    placeholder(R.drawable.profile_image)
                }
            ),
            contentDescription = "img",
            modifier = Modifier
                .clip(CircleShape)
                .border(1.dp, MyGreen, CircleShape)
                .size(60.dp),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier
            .padding(start = 20.dp,top = 10.dp,bottom = 5.dp)
            .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround){
            Text(text = name,
                modifier = Modifier.padding(bottom = 10.dp),
                color = DarkWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold)
            Text(text  = "Tap to chat",
                color = MyGrey,
                fontSize = 14.sp)
        }
    }
}
fun searchUpdate(name:String){
    val tList = if(name.isEmpty()){
         cachedUsersList
    }
    else{
        cachedUsersList.filter {
            it.name.contains(name.trim(),true) ||
            it.email.contains(name.trim(),true)
        }
    }
    allUsersList.value = tList as ArrayList<User>
}
