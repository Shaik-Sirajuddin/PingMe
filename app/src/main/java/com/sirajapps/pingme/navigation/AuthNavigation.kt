package com.sirajapps.pingme.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.sirajapps.pingme.activitiyuis.*
import com.sirajapps.pingme.models.User
import com.sirajapps.pingme.models.UserOffline


@Composable
fun authNavigation(context: Context):NavHostController {
    val navController = rememberNavController()
    NavHost(navController = navController,startDestination = Screen.SignUpScreen.route){
        composable(route = Screen.SignUpScreen.route){
            SignUpUi(context = context,navController)
        }
        composable(route = Screen.LogInScreen.route){
            LogInUi(context = context,navController)
        }
    }
    return navController
}
@Composable
fun mainNavigation(listener:ScreenCallbacks,navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
        composable(route = Screen.MainScreen.route) {
          MainUi(statusRecycle ={
              listener.statusRecycle(it)
          }, usersRecycle ={
              listener.usersRecycle(it)
          })
        }
        composable(route = Screen.SettingsScreen.route){
             SettingsUi()
        }
        composable(route = Screen.CallsScreen.route){
            CallsScreen()
        }

    }
}
@Composable
fun chatNavigation(context: Context,user: User,userOffline: UserOffline?,listener: ChatScreenCallBacks) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.ChatScreen.route) {
       composable(route = Screen.ChatScreen.route){
           ChatUi(context = context,
               userOffline = userOffline,
               user = user,
               listener = listener
           ){
               listener.messageRecycle(it)
           }
       }
    }
}
interface ScreenCallbacks{
    fun statusRecycle(recyclerView: RecyclerView)
    fun usersRecycle(recyclerView: RecyclerView)
}
interface ChatScreenCallBacks{
    fun sendMessage(msg:String,imageUri: String? = null,id:DatabaseReference? = null)
    fun messageRecycle(view:RecyclerView)
}