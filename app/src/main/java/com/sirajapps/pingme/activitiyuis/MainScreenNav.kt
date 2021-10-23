package com.sirajapps.pingme.activitiyuis

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sirajapps.pingme.R
import com.sirajapps.pingme.models.BottomNavItem
import com.sirajapps.pingme.navigation.Screen
import com.sirajapps.pingme.navigation.ScreenCallbacks
import com.sirajapps.pingme.navigation.mainNavigation
import com.sirajapps.pingme.ui.theme.DarkBackground
import com.sirajapps.pingme.ui.theme.MyGreen
import com.sirajapps.pingme.ui.theme.SearchBarText

@Composable
fun MainScreen(listener:ScreenCallbacks,uploadStatus:()->Unit){
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController,
                items = listOf(BottomNavItem("Chats",Screen.MainScreen.route, painterResource(id = R.drawable.messenger)),
                BottomNavItem("Status","", painterResource(id = R.drawable.camera)),
                BottomNavItem("Calls",Screen.CallsScreen.route, painterResource(id = R.drawable.telephone)),
                BottomNavItem("Settings",Screen.SettingsScreen.route, painterResource(id = R.drawable.settings)
                )),
                onClick ={item->
                    if(item.route==""){
                        uploadStatus()
                    }else{
                        if(navController.currentDestination?.route !=item.route) {
                            navController.navigate(item.route)
                        }
                    }
                })
        }
    ) {
        mainNavigation(listener = listener,navController = navController)
    }
}
@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    items: List<BottomNavItem>,
    onClick: (item: BottomNavItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    BottomNavigation(
        backgroundColor = DarkBackground,
        elevation = 10.dp
    )
    {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route && item.name!="Status"
            BottomNavigationItem(
                selected = selected,
                selectedContentColor = MyGreen,
                unselectedContentColor = SearchBarText,
                onClick = {
                    onClick(item)
                },
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.name,
                        modifier = Modifier.padding(12.dp)
                    )
                })
        }
    }
}
