package com.sirajapps.pingme.activitiyuis

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.RecyclerView
import com.sirajapps.pingme.MainActivity
import com.sirajapps.pingme.NewUserActivity
import com.sirajapps.pingme.R
import com.sirajapps.pingme.models.User
import com.sirajapps.pingme.ui.theme.*


@Composable
fun MainUi(statusRecycle:(recyclerView:RecyclerView)->Unit,
           usersRecycle:(recyclerView:RecyclerView)->Unit){
    val context = LocalContext.current as MainActivity
    Box(modifier = Modifier
        .fillMaxSize()
        .background(DarkBackground)){
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(10.dp))
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween){
                Text(modifier= Modifier.padding(start = 32.dp,bottom = 10.dp),
                    text ="Ping me",
                color = DarkWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold)
                Icon(modifier = Modifier
                    .padding(end = 20.dp)
                    .size(27.dp)
                    .clickable {
                        context.changeUserUpdate(false)
                        val intent = Intent(context,NewUserActivity::class.java)
                        context.startActivity(intent)
                    },
                    painter = painterResource(id = R.drawable.new_message)
                    ,contentDescription ="New chat",
                   tint = MyGreen
                )
            }
            SearchBar{
               context.searchUser(it.trim())
            }
            Text(modifier= Modifier.padding(start = 15.dp,top = 10.dp),
                text = "Status",
                color = DarkWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium)
            AndroidView(
                factory = { context: Context ->
                    val view = LayoutInflater.from(context)
                        .inflate(R.layout.status_recycler_view, null, false)
                    val statusRecyclerView =  view.findViewById<RecyclerView>(R.id.statusRecyclerView)
                    statusRecycle(statusRecyclerView)
                    view
                }
            )
            Text(modifier= Modifier.padding(start = 15.dp,top = 10.dp),
                text = "Chats",
                color = DarkWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium)
            AndroidView(modifier = Modifier.fillMaxWidth(),
                factory = { context: Context ->
                    val view = LayoutInflater.from(context)
                        .inflate(R.layout.users_recyclerview, null, false)
                    val userRecyclerView =  view.findViewById<RecyclerView>(R.id.usersRecyclerView)
                    usersRecycle(userRecyclerView)
                    view
                }
            )

        }
    }
}
@Composable
fun SearchBar(modifier: Modifier = Modifier,newText:(text:String)->Unit){
    var searchText by remember {
        mutableStateOf("")
    }
    val focus = LocalFocusManager.current
    TextField(value = searchText,
        onValueChange = {
            searchText = it
            newText(it)
        },
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            cursorColor = DarkWhite
        ),
        modifier = modifier
            .padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 0.dp)
            .background(SearchBarBackground, RoundedCornerShape(30.dp))
            .fillMaxWidth(),
        textStyle = TextStyle(
            color = DarkWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focus.clearFocus()
            }
        ),
        placeholder = {
            Text(
                text = "Search",
                color = SearchBarText
            )
        },
        leadingIcon = {
            Icon(Icons.Default.Search,contentDescription = "Search",tint = SearchBarText)
        }
    )
}