package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogyaFDC.Data
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.PushNotification
import com.aarogyaforworkers.aarogyaFDC.R
import com.aarogyaforworkers.aarogyaFDC.VideoCall.RetrofitInstance
import com.aarogyaforworkers.aarogyaFDC.VideoCall.VideoConferencing
import com.aarogyaforworkers.aarogyaFDC.ui.theme.defDark
import com.aarogyaforworkers.awsapi.models.AdminProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCallingLobbyScreen(navHostController:NavHostController)
{
    val context= LocalContext.current

    when(MainActivity.adminDBRepo.GroupMembersSyncedState.value){

        true -> {

        }

        false -> {
            MainActivity.adminDBRepo.updateGroupMembersSyncedState(null)
        }

        null -> {

        }
    }

    val doctor = MainActivity.adminDBRepo.adminProfileState.value

    val adminList = MainActivity.adminDBRepo.groupMembersProfileList.value.filter { it.admin_id != "" && it.admin_id != doctor.admin_id  }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(horizontal = 15.dp,),
                title = {
                    BoldTextView("Video Calling", fontSize = 25)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        MainActivity.adminDBRepo.updateGroupMembersSyncedState(null)
                        navHostController.navigate(Destination.Home.routes)
                    }) {
                        Icon(imageVector = Icons.Filled.ArrowBack,contentDescription = "Back Button")
                    }
                },
                actions = {
                    Box(
                        Modifier
                            .size(44.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFD4B6), shape = CircleShape),
                            contentAlignment = Alignment.Center) {
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .padding(10.dp)
                            ) {
                                IconButton(onClick = {
                                    PushNotification(
                                        "cHKTE0nZRAe7kOuBzxqkQe:APA91bGFKfTIMlJKQZT4lt-XbzrNNHBijdnUC7Zincv7n93Zf24K78-i8e9fZZ0onHrill0it5-QTjGLX02LpmXZh_Wu7xzuEsduPSNGseIsQPC_rIftYUT2weYuTaoCApZEm_v6mL17",
                                        Data("Conference ID")
                                    ).also {
                                        sendNotification(it,context)
                                    }
                                }) {
                                    Icon(imageVector = Icons.Default.Group, contentDescription = "Group Call", Modifier.size(44.dp),
                                        tint = defDark )
                                }
                            }
                        }
                    }
                }
            )
        },
    ){
        LazyColumn(modifier= Modifier
            .padding(it)
            .padding(horizontal = 15.dp)){
            items(adminList){admin->
                AdminCard(admin = admin){
                    if(it.token.isNotEmpty()){
                        if(MainActivity.callRepo.confrenceId.value == null){
                            MainActivity.callRepo.refreshConfrenceId()
                        }
                        val doctor = MainActivity.adminDBRepo.adminProfileState.value
                        val callerInfo = MainActivity.callRepo.confrenceId.value!! + "-:-" + doctor.first_name + "-:-" + doctor.hospitalName + "-:-" + doctor.profile_pic_url

                        PushNotification(
                            it.token,
                            Data(callerInfo)
                        ).also {
                            sendNotification(it,context)
                        }
                    }
                }
            }
        }
    }

    if(MainActivity.adminDBRepo.GroupMembersSyncedState.value != true) showProgress()
}

@Composable
fun AdminCard(admin: AdminProfile, onSelected : (AdminProfile) -> Unit)
{
    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .clickable { onSelected(admin) }
            .background(Color(0xBFE2D2FD), shape = RoundedCornerShape(8.dp))
    ) {
        Row(
            Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                UserImageView(imageUrl = admin.profile_pic_url, size = 55.dp) {}
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Row {
                    LabelWithoutIconView(title = admin.first_name.capitalize(Locale.ROOT))
                    Spacer(modifier = Modifier.width(5.dp))
                    LabelWithoutIconView(title = admin.last_name.capitalize(Locale.ROOT))
                }
                Row {
                    LabelWithIconView(title = adminGenderShort(admin),icon = if(checkIsMale(admin.gender)) Icons.Default.Male else Icons.Default.Female)
                    Spacer(modifier = Modifier.width(5.dp))
                    LabelWithIconView(title = admin.age, icon = Icons.Default.Cake)
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                AndroidView(
                    factory = {
                        View.inflate(it, R.layout.video_calling_lobby_screen,null)
                    },
                    update = {


                    }
                )
            }
        }
    }
}

fun adminGenderShort(admin: AdminProfile): String {
    return when(admin.gender?.toUpperCase()) {
        "MALE" -> "M"
        "FEMALE" -> "F"
        "OTHER" -> "O"
        else -> ""
    }
}

fun sendNotification(notification: PushNotification, context:Context) = CoroutineScope(Dispatchers.IO).launch {
    try {
        val response = RetrofitInstance.api.postNotification(notification)
        Log.d("TAG", "sendNotification: $response")
        if(response.isSuccessful) {
            Log.d("TAG", "Response: $response")
            val intent = Intent(context, VideoConferencing::class.java)
            context.startActivity(intent)
        } else {
            Log.e("TAG", response.errorBody().toString())
        }
    } catch(e: Exception) {
        Log.e("TAG", e.toString())
    }
}