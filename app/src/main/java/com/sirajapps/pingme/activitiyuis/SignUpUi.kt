package com.sirajapps.pingme.activitiyuis

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.sharp.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.sirajapps.pingme.R
import com.sirajapps.pingme.SignUpActivity
import com.sirajapps.pingme.navigation.Screen
import com.sirajapps.pingme.ui.theme.*


var emailError = mutableStateOf(false)
var passwordError = mutableStateOf(false)
var nameError = mutableStateOf(false)
private var loader = mutableStateOf(false)
private lateinit var bitmap:MutableState<Bitmap>
@Composable
fun SignUpUi(context:Context,controller:NavController) {
    bitmap = remember {
        mutableStateOf(BitmapFactory.decodeResource(context.resources, R.drawable.profile_image))
    }
    val imageLauncher  = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(),
            onResult = {
                getImage(context,it)
            })
        var name by remember {
            mutableStateOf("")
        }
        var email by remember {
            mutableStateOf("")
        }
        var password by remember {
            mutableStateOf("")
        }
        var nameColor by remember {
            mutableStateOf(Brush.horizontalGradient(listOf(MyBlack,
                MyBlack)))
        }
        var emailColor by remember {
            mutableStateOf(Brush.horizontalGradient(listOf(MyBlack,
                MyBlack)))
        }
        var passwordColor by remember {
            mutableStateOf(Brush.horizontalGradient(listOf(MyBlack,
                MyBlack)))
        }
        val scrollState  = rememberScrollState()
        var isPassVisible by remember {
            mutableStateOf(false)
        }
        val icon = if(isPassVisible){
            painterResource(id = R.drawable.design_ic_visibility)
        }
        else{
            painterResource(id = R.drawable.design_ic_visibility_off)
        }
        Box(
            modifier = Modifier
                .background(SignUpBlack)
                .fillMaxSize()
        )
        {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState, enabled = true),
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.back_image),
                            contentDescription = "Back",
                            colorFilter = ColorFilter.tint(DarkWhite),
                            modifier = Modifier
                                .padding(start = 20.dp)
                                .border(1.dp, MyBlack, RoundedCornerShape(15.dp))
                                .size(50.dp),
                            contentScale = ContentScale.Inside
                        )
                        Text(
                            text = "Sign up",
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkWhite,
                            modifier = Modifier.padding(start = 20.dp)
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    )
                    Text(
                        text = "Sign up with one of the following options",
                        color = LightWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                    Button(
                        onClick = {
                            googleSignIn(context)
                        },
                        modifier = Modifier
                            .padding(20.dp)
                            .border(1.dp, MyBlack, RoundedCornerShape(10.dp))
                            .height(50.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = SignUpBlack
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.google_icon),
                                contentDescription = "Google sign in",
                                modifier = Modifier
                                    .padding(2.dp)
                                    .size(20.dp),
                                colorFilter = ColorFilter.tint(DarkWhite)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxSize()
                    )
                    {  // Details Column
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            ProfileImage(modifier = Modifier) {
                                imageLauncher.launch("image/*")
                            }
                            val focus = LocalFocusManager.current
                            Text(
                                text = "Name",
                                modifier = Modifier
                                    .padding(bottom = 10.dp, top = 10.dp)
                                    .align(Alignment.Start),
                                color = DarkWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            TextField(value = name,
                                onValueChange = {
                                    name = it
                                    if (nameError.value && it.isNotEmpty()) {
                                        nameError.value = false
                                    }
                                },
                                singleLine = true,
                                colors = TextFieldDefaults.textFieldColors(
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    cursorColor = DarkWhite
                                ),
                                modifier = Modifier
                                    .background(SignUpBlack)
                                    .fillMaxWidth()
                                    .border(1.dp, brush = nameColor, RoundedCornerShape(10.dp))
                                    .onFocusChanged {
                                        nameColor = if (it.isFocused) {
                                            Brush.horizontalGradient(
                                                listOf(
                                                    GradientStart,
                                                    GradientEnd
                                                )
                                            )
                                        } else {
                                            Brush.horizontalGradient(
                                                listOf(
                                                    MyBlack,
                                                    MyBlack
                                                )
                                            )
                                        }
                                    },
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
                                        "itsMe",
                                        color = MyGrey
                                    )
                                },
                                trailingIcon = {
                                    if (nameError.value) {
                                        Icon(
                                            Icons.Sharp.Warning,
                                            "error",
                                            tint = MaterialTheme.colors.error
                                        )
                                    }
                                }
                            )
                            if (nameError.value) {
                                Text(
                                    text = "Enter a valid name",
                                    fontSize = 14.sp,
                                    color = Color.Red
                                )
                            }
                            //Email
                            Text(
                                text = "Email",
                                modifier = Modifier
                                    .padding(bottom = 10.dp, top = 10.dp)
                                    .align(Alignment.Start),
                                color = DarkWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            TextField(value = email,
                                onValueChange = {
                                    email = it
                                },
                                singleLine = true,
                                colors = TextFieldDefaults.textFieldColors(
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    cursorColor = DarkWhite
                                ),
                                modifier = Modifier
                                    .background(SignUpBlack)
                                    .fillMaxWidth()
                                    .border(1.dp, emailColor, RoundedCornerShape(10.dp))
                                    .onFocusChanged {
                                        emailColor = if (it.isFocused) {
                                            Brush.horizontalGradient(
                                                listOf(
                                                    GradientStart,
                                                    GradientEnd
                                                )
                                            )
                                        } else {
                                            Brush.horizontalGradient(
                                                listOf(
                                                    MyBlack,
                                                    MyBlack
                                                )
                                            )
                                        }
                                    },
                                textStyle = TextStyle(
                                    color = DarkWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focus.clearFocus()
                                    }
                                ),
                                placeholder = {
                                    Text(
                                        text = "itsme@gmail.com",
                                        color = MyGrey
                                    )
                                },
                                trailingIcon = {
                                    if (emailError.value) {
                                        Icon(
                                            Icons.Filled.Warning,
                                            "Error",
                                            tint = MaterialTheme.colors.error
                                        )
                                    }
                                }
                            )
                            if (emailError.value) {
                                Text(
                                    text = "Enter a valid email",
                                    color = Color.Red,
                                    fontSize = 12.sp
                                )
                            }
                            // Password

                            Text(
                                text = "Password",
                                modifier = Modifier
                                    .padding(bottom = 10.dp, top = 10.dp)
                                    .align(Alignment.Start),
                                color = DarkWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            TextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    if (it.length >= 6) {
                                        passwordError.value = false
                                    }
                                },
                                singleLine = true,
                                colors = TextFieldDefaults.textFieldColors(
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    cursorColor = DarkWhite
                                ),
                                modifier = Modifier
                                    .background(SignUpBlack)
                                    .fillMaxWidth()
                                    .border(1.dp, passwordColor, RoundedCornerShape(10.dp))
                                    .onFocusChanged {
                                        passwordColor = if (it.isFocused) {
                                            Brush.horizontalGradient(
                                                listOf(
                                                    GradientStart,
                                                    GradientEnd
                                                )
                                            )
                                        } else {
                                            Brush.horizontalGradient(
                                                listOf(
                                                    MyBlack,
                                                    MyBlack
                                                )
                                            )
                                        }
                                    },
                                textStyle = TextStyle(
                                    color = DarkWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focus.clearFocus()
                                    }
                                ),
                                placeholder = {
                                    Text(
                                        text = "Pick a strong password",
                                        color = MyGrey
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        isPassVisible = !isPassVisible
                                    }) {
                                        Icon(
                                            painter = icon,
                                            contentDescription = "visibility",
                                            tint = DarkWhite
                                        )
                                    }
                                },
                                visualTransformation = if (isPassVisible) {
                                    VisualTransformation.None
                                } else {
                                    PasswordVisualTransformation()
                                },
                            )
                            if (passwordError.value) {
                                Text(
                                    text = "Minimum length of password is 6",
                                    color = Color.Red,
                                    fontSize = 12.sp
                                )
                            }
                            Button(
                                onClick = {
                                    createAccount(context, email.trim(), password.trim(), name.trim())
                                },
                                modifier = Modifier
                                    .padding(top = 30.dp)
                                    .height(55.dp)
                                    .fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.Transparent
                                ),
                                contentPadding = PaddingValues()

                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                listOf(
                                                    GradientStart,
                                                    GradientEnd
                                                )
                                            ),
                                            RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Create Account",
                                        fontSize = 18.sp,
                                        color = DarkWhite
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .padding(top = 50.dp)
                                    .fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Already have an account?",
                                    fontSize = 15.sp,
                                    color = MyGrey
                                )
                                Text(
                                    modifier = Modifier
                                        .padding(start = 5.dp)
                                        .clickable {
                                            controller.navigate(Screen.LogInScreen.route)
                                        }
                                        .padding(vertical = 10.dp, horizontal = 10.dp),
                                    text = "Log in",
                                    fontSize = 15.sp,
                                    color = DarkWhite
                                )
                            }
                        }
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize()
            ) {
                ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                    val load = createRef()

                    if (loader.value) {
                        CircularProgressIndicator(modifier = Modifier.constrainAs(load) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                            color = GradientStart)
                    }
                }
            }
        }
    }
@Composable
fun ProfileImage(modifier: Modifier = Modifier,click:()->Unit){
    Box(modifier.fillMaxWidth()) {
        Column(modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                bitmap.value.asImageBitmap(),
                contentDescription = "profileImage",
                modifier = Modifier
                    .padding(5.dp)
                    .background(DarkWhite, CircleShape)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .size(120.dp)
                    .clickable {
                       click()
                    },
                contentScale = ContentScale.Crop
            )
            Text(
                text = "Tap to select profile image",
                color = MyGrey,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(3.dp)
            )
        }
    }
}
fun getImage(context: Context, uri:Uri?){
    if(uri==null)return
    val imageStream = context.contentResolver.openInputStream(uri)
    bitmap.value = BitmapFactory.decodeStream(imageStream)
}

fun createAccount(context:Context,email:String,password:String,userName:String){
    var flag = false
    if(password.length<6){
        passwordError.value = true
        flag = true
    }
    if(email.length < 11 || !email.endsWith("@gmail.com")){
        emailError.value = true
        flag = true
    }
    if(userName.isEmpty()){
        nameError.value = true
        flag = true
    }
    if(flag)return
    val signUpAct = context as SignUpActivity
    signUpAct.createAccount(email = email,password = password,userName = userName,bitmap = bitmap.value)
}
fun googleSignIn(context: Context,isSignIn:Boolean = false){
    val singUpAct = context as SignUpActivity
    singUpAct.googleSignIn(isSignIn)
}
fun startLoader(){
    loader.value = true
}
fun stopLoader(){
    loader.value = false
}