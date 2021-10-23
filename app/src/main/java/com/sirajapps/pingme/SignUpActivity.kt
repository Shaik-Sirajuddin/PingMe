package com.sirajapps.pingme

import android.R.attr
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sirajapps.pingme.activitiyuis.*
import com.sirajapps.pingme.models.Constants
import com.sirajapps.pingme.models.User
import com.sirajapps.pingme.navigation.authNavigation
import com.sirajapps.pingme.navigation.Screen
import com.sirajapps.pingme.ui.theme.PingMeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.random.Random
import android.graphics.Bitmap.CompressFormat

import android.R.attr.bitmap
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class SignUpActivity : ComponentActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth:FirebaseAuth
    private var signIn = false
    private lateinit var navController:NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PingMeTheme{
                navController = authNavigation(this)
            }
        }
        auth = Firebase.auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.clientId))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser!=null){
            updateUI(currentUser,isOnStart = true)
        }
    }
    private suspend fun createUserInDb(fUser: FirebaseUser?, userName: String,bitmap: Bitmap?,imgUrl:String? = null):User?{
        if(fUser==null)return null
        val inputStream:ByteArray?= if(bitmap!=null){
            val bos = ByteArrayOutputStream()
            bitmap.compress(CompressFormat.JPEG, 0 , bos)
            bos.toByteArray()
        }
        else{
            null
        }
        val imageUri:String? = if(inputStream!=null) {
            Firebase.storage.reference
                .child("Users")
                .child("ProfileImage")
                .child(fUser.uid)
                .putBytes(inputStream)
                .await()
            Firebase.storage.reference
                .child("Users")
                .child("ProfileImage")
                .child(fUser.uid)
                .downloadUrl
                .await()
                .toString()
        }
        else{
            imgUrl
        }
        val user = User(
            uid = fUser.uid,
            name = userName,
            email = fUser.email!!,
            onlineImageUri = imageUri
        )
        val database = Firebase.database.reference
        val map = HashMap<String,Any>()
        map[user.uid] = user
        database.child("Users")
            .updateChildren(map)
            .await()
        return user
    }
    fun createAccount(userName:String,email:String,password:String,bitmap: Bitmap?){
       try {
           showBar()
           lifecycleScope.launch {
               val signInMethodsList = auth.fetchSignInMethodsForEmail(email).await().signInMethods
               if(signInMethodsList?.size!=0){
                   throw Exception("0")
               }
               val user = auth.createUserWithEmailAndPassword(email.trim(), password).await().user
               val tUser = createUserInDb(user,userName,bitmap)
               withContext(Dispatchers.Main) {
                   updateUI(user,tUser = tUser)
               }
           }
       }catch(e:Exception){
           hideBar()
           if(e.message.equals("0")) {
               Toast.makeText(this,"This email ID is already used by someone else",Toast.LENGTH_SHORT).show()
           }
           else{
               Toast.makeText(this,"Account creation failed",Toast.LENGTH_SHORT).show()
           }
       }

    }
    fun googleSignIn(isSignIn:Boolean){
        signIn = isSignIn
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }
    fun signInUser(email: String,password: String){
            lifecycleScope.launch{
                try {
                    val signInMethodsList =
                        auth.fetchSignInMethodsForEmail(email).await().signInMethods
                    if (signInMethodsList == null || signInMethodsList.size == 0) {
                        throw Exception("0")
                    }
                    if (signInMethodsList.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                        auth.signInWithEmailAndPassword(email, password).await()
                        updateUI(auth.currentUser)
                    } else {
                        throw Exception("1")
                    }
                }catch(e:Exception){
                    withContext(Dispatchers.Main) {
                        when (e.message.toString()) {
                            "0" -> {
                                Toast.makeText(baseContext, "Account doesn't exist", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            "1" -> {
                                Toast.makeText(
                                    baseContext,
                                    "Account is registered with google sign in",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {
                                Toast.makeText(baseContext, e.message.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }

    }
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        showBar()
        try {
            val account = task.getResult(ApiException::class.java)!!
            lifecycleScope.launch {
                firebaseAuthWithGoogle(account.idToken!!,account.email!!,account.displayName,account.photoUrl)
            }
        } catch (e: ApiException) {
            Log.e("ac",e.message.toString())
            updateUI(null)
        }
    }
    private suspend fun firebaseAuthWithGoogle(idToken: String, email: String, displayName: String?,profile: Uri?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val signInMethodsList = auth.fetchSignInMethodsForEmail(email).await().signInMethods
        if(signInMethodsList!=null && signInMethodsList.size!=0){
            val isGoogle = signInMethodsList.contains(GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD)
            if(!isGoogle){
                withContext(Dispatchers.Main) {
                    if (!signIn) {
                        updateUI(null, "Account already exists")
                    }
                    else{
                        updateUI(null,"Please sign in through email and password")
                    }
                }
                return
            }
        }
        val task = auth.signInWithCredential(credential).await()
        val name = displayName ?: Random.nextFloat().toString()
        val tUser = createUserInDb(auth.currentUser,name,bitmap = null,imgUrl = profile.toString())
        withContext(Dispatchers.Main) {
           updateUI(task.user,tUser = tUser)
        }
    }

    private fun updateUI(user:FirebaseUser?,message:String? = null,isOnStart:Boolean = false,tUser: User? = null){
        if(!isOnStart) {
            hideBar()
        }
        if(user==null){
            val msg= message ?: "Sign In Failed"
            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
        }
        else{
            if(tUser!=null){
                Constants.currentUser = tUser
            }
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("isOnStart",isOnStart)
            startActivity(intent)
            finish()
        }
    }
    private fun hideBar(){
        if(navController.currentDestination?.route == Screen.SignUpScreen.route){
            stopLoader()
        }
        else{
            stopLogInLoader()
        }
    }
    private fun showBar(){
        if(navController.currentDestination?.route == Screen.SignUpScreen.route){
            startLoader()
        }
        else{
           startLogInLoader()
        }
    }

}
