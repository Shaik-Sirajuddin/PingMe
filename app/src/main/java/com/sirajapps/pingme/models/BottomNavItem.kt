package com.sirajapps.pingme.models

import androidx.compose.ui.graphics.painter.Painter


data class BottomNavItem(
    val name:String,
    val route:String,
    val icon: Painter,
    val badgeCount:Int = 0
)