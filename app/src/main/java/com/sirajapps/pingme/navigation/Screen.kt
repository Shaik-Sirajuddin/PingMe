package com.sirajapps.pingme.navigation

sealed class Screen(val route:String){
    object SignUpScreen : Screen("sign_up_screen")
    object LogInScreen : Screen("log_in_screen")
    object MainScreen :Screen("main_screen")
    object SearchUserScreen :Screen("search_user_screen")
    object ChatScreen : Screen("chat_screen")
    object SettingsScreen : Screen("settings_screen")
    object CallsScreen : Screen("calls_screen")
}