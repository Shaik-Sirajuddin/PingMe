package com.sirajapps.pingme.models

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DatabaseReference
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.lang.Exception

class Constants {
    companion object{
        var currentUser:User? = null
        var allChatsRef:DatabaseReference? = null
        lateinit var curUserUid:String
    }

}

fun Activity.showShortToast(it:String){
    Toast.makeText(this,it.trim(),Toast.LENGTH_SHORT).show()
}
fun Activity.showLongToast(it:String){
    Toast.makeText(this,it.trim(),Toast.LENGTH_LONG).show()
}
