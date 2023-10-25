package com.aarogyaforworkers.aarogyaFDC.composeScreens

import Commons.HomePageTags
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VideoChat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.NotificationData
import com.aarogyaforworkers.aarogyaFDC.PushNotification
import com.aarogyaforworkers.aarogyaFDC.R
import com.aarogyaforworkers.aarogyaFDC.RetrofitInstance
import com.aarogyaforworkers.aarogyaFDC.VideoConferencing
import com.aarogyaforworkers.aarogyaFDC.data
import com.aarogyaforworkers.aarogyaFDC.isfromcall
import com.aarogyaforworkers.aarogyaFDC.ui.theme.defDark
import com.aarogyaforworkers.awsapi.models.AdminProfile
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
//AdminProfile(admin_id="07db655d-3bb8-4f3f-85b4-79f49cbdd67b", email="thakurravi450@gmail.com", phone="919340413756", first_name="Dr. Ravi", last_name="Thakur", age="25", gender="Male", weight="180", height="165", location="Bengaluru", profile_pic_url="https://aarogyaforworkers5c90f62fdef040a798f1911e2c5d81213923-dev.s3.ap-south-1.amazonaws.com/public/sub_users_Profile_Pictures/07db655d-3bb8-4f3f-85b4-79f49cbdd67b.jpg", total_sessions_taken="10", total_users_added="5", isVerified="Yes", hospitalName="AarogyaTech Clinic", designation="Mayo Clinic USA", isDoctor="Yes", groups="07db655d-3bb8-4f3f-85b4-79f49cbdd67b,99fde97a-8e1a-427b-93a3-9503d84e5eb4", groupid="AAAA", registration_id="AAAA"),
val adminList= listOf(AdminProfile(admin_id="e59816ed-afdd-4452-a3c7-6c20fbd9fc1b", email="katul0529@gmail.com", phone="918423782058", first_name="Dr. Atul", last_name="Kumar", age="30", gender="Male", weight="180", height="6.0", location="15-O, MIG complex, Mayur Vihar Phase 3, Pocket-2, Delhi 110096", profile_pic_url="https://aarogyaforworkers5c90f62fdef040a798f1911e2c5d81213923-dev.s3.ap-south-1.amazonaws.com/public/sub_users_Profile_Pictures/e59816ed-afdd-4452-a3c7-6c20fbd9fc1b.jpg", total_sessions_taken="10", total_users_added="5", isVerified="Yes", hospitalName="Health Sunrise Path Lab", designation="MBBS Dental surgeon, Consultant Pedodontist & Child Counselor" , isDoctor="Yes", groups="9aa4f1c6-5f6b-4978-b337-e339983a9432,e59816ed-afdd-4452-a3c7-6c20fbd9fc1b", groupid="G2IN", registration_id=""))
@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun VideoCallingLobbyScreen(navHostController:NavHostController)
{

    val context= LocalContext.current

    if(isfromcall)
    {
        isfromcall=false
        val intent = Intent(context, VideoConferencing::class.java)
        context.startActivity(intent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(horizontal = 15.dp,),
                title = {
                    BoldTextView("Video Calling", fontSize = 25)
                },
                navigationIcon = {
                    IconButton(onClick = {
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
                                        "e6tJFFGaTaKf5DX_BfBFOT:APA91bGLLCGC8SiXqc4eQpodHG4jpqsgn6mwlVK9RKSzOMrQhNkTzehveXpBu1VfiEI3aXfOE5GXCNLFnhFO0mBCSLgvKY_Lv292eMT99Q53YjQU3ebMXU3zYAamSVxu9XNfqnMbd9dA",
                                        data("Conference ID")
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
                AdminCard(admin = admin)
            }
        }
    }


//    Surface(modifier=Modifier.fillMaxSize()) {
//
//        AndroidView(
//            factory = {
//                View.inflate(it, R.layout.video_calling_lobby_screen,null)
//            },
//            modifier=Modifier.fillMaxSize(),
//            update = {
//
//                MainActivity.zegoCloudViewModel.xml=it
//
//                val yourUserID = it.findViewById<TextView>(R.id.your_user_id)
//
//                yourUserID.text = "Your User ID :${MainActivity.zegoCloudViewModel.userName}"
//
//                MainActivity.zegoCloudViewModel.initVoiceButton()
//
//                MainActivity.zegoCloudViewModel.initVideoButton()
//            }
//        )
//    }
}

@Composable
fun AdminCard(admin: AdminProfile)
{
    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
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

                        MainActivity.zegoCloudViewModel.xml=it

                        MainActivity.zegoCloudViewModel.initVoiceButton()

                        MainActivity.zegoCloudViewModel.initVideoButton()
                    }
                )
            }
        }
    }
}

public fun adminGenderShort(admin: AdminProfile): String {
    return when(admin.gender?.toUpperCase()) {
        "MALE" -> "M"
        "FEMALE" -> "F"
        "OTHER" -> "O"
        else -> ""
    }
}

fun sendNotification(notification: PushNotification,context:Context) = CoroutineScope(Dispatchers.IO).launch {
    try {
        val response = RetrofitInstance.api.postNotification(notification)
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