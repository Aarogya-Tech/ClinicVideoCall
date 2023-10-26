package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.VideoCall.PushNotification
import com.aarogyaforworkers.aarogyaFDC.R
import com.aarogyaforworkers.aarogyaFDC.VideoCall.RetrofitInstance
import com.aarogyaforworkers.aarogyaFDC.VideoCall.VideoConferencing
import com.aarogyaforworkers.aarogyaFDC.VideoCall.data
import com.aarogyaforworkers.awsapi.models.AdminProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCallingLobbyScreen(navHostController:NavHostController) {
    val context= LocalContext.current
    var doctor = MainActivity.adminDBRepo.adminProfileState.value
    val adminList = MainActivity.adminDBRepo.groupMembersProfileList.value.filter { it.admin_id != "" && it.admin_id != doctor.admin_id  }
    var isSelected = remember { mutableStateOf(false) }
    val selectedIndex = mutableStateOf(setOf<Int>())
    var selectedAdmin = remember { mutableStateOf("") }

    when(MainActivity.adminDBRepo.GroupMembersSyncedState.value){

        true -> {

        }

        false -> {
            MainActivity.adminDBRepo.updateGroupMembersSyncedState(null)
        }

        null -> {

        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    BoldTextView(doctor.hospitalName, fontSize = 20)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        MainActivity.adminDBRepo.updateGroupMembersSyncedState(null)
                        navHostController.navigate(Destination.Home.routes)
                    }) {
                        Icon(imageVector = Icons.Filled.ArrowBack,contentDescription = "Back Button")
                    }
                },
            )
        },
        bottomBar = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                FloatingActionButton(onClick = {
                    if(selectedAdmin.value.isNotEmpty())
                    {
                        if(MainActivity.callRepo.confrenceId.value == null){
                            MainActivity.callRepo.refreshConfrenceId()
                        }
                        val callerInfo = MainActivity.callRepo.confrenceId.value!! + "-:-" + doctor.first_name + "-:-" + doctor.hospitalName + "-:-" + doctor.profile_pic_url

                        PushNotification(
                            selectedAdmin.value,
                            data(callerInfo)
                        ).also { it1 ->
                            sendNotification(it1,context)
                        }
                    } }, modifier = Modifier.padding(8.dp)) {
                    Icon(imageVector = Icons.Default.VideoCall, contentDescription = "Video")
                }
            }
        }
    ){
        Column(modifier= Modifier
            .fillMaxSize()
            .padding(it))
        {

            Row(Modifier.fillMaxWidth()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 23.dp)
                        .selectable(selected = isSelected.value,
                            onClick = {
                                isSelected.value = !isSelected.value
                                when(isSelected.value){
                                    true -> selectedIndex.value = adminList.indices.toSet()
                                    false -> selectedIndex.value = emptySet()
                                }
                            })
                ) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(32.dp)
                                .background(
                                    color = if (isSelected.value) Color(0xFF2f5597) else Color(
                                        0xffdae3f3
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                ), contentAlignment = Alignment.Center) {
                            if (isSelected.value){
                                Icon(imageVector = Icons.Default.Check, contentDescription = "checkIcon", Modifier.size(28.dp), tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.width(15.dp))
                        RegularTextView(title = "Select All", fontSize = 22)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier= Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)){
                itemsIndexed(adminList){index, admin ->
                    GroupCard(firstName = admin.first_name,
                        lastName = admin.last_name,
                        isSelected = isSelected.value || selectedIndex.value.contains(index)) {

                        if(isSelected.value){
                            isSelected.value = false
                            selectedIndex.value = (adminList.indices.toSet() - index)
                        }else if(selectedIndex.value.contains(index)){
                            selectedIndex.value = selectedIndex.value - index
                        }else{
                            selectedIndex.value = selectedIndex.value + index
                        }

                        isSelected.value = selectedIndex.value.size == adminList.size

                        selectedAdmin.value = admin.token
                    }
                    Spacer(modifier = Modifier.height(8.dp))
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

@Composable
fun GroupCard(firstName: String, lastName: String, isSelected: Boolean, onSelect: () -> Unit ){
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .selectable(selected = isSelected, onClick = { onSelect() })
            .background(Color(0x80DAE3F3), RoundedCornerShape(100.dp))) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(45.dp)
                    .background(
                        color = if (isSelected) Color(0xFF2f5597) else Color(0xffdae3f3),
                        shape = CircleShape
                    ), contentAlignment = Alignment.Center) {
                if (isSelected){
                    Icon(imageVector = Icons.Default.Check, contentDescription = "checkIcon", Modifier.size(25.dp), tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.width(15.dp))

            RegularTextView(title = "$firstName $lastName", fontSize = 22)
        }
    }
}



//        LazyColumn(modifier= Modifier
//            .padding(it)
//            .padding(horizontal = 15.dp)){
//            items(adminList){admin->
//                AdminCard(admin = admin){
//                    if(it.token.isNotEmpty())
//                    {
//                        if(MainActivity.callRepo.confrenceId.value == null){
//                            MainActivity.callRepo.refreshConfrenceId()
//                        }
//                        val doctor = MainActivity.adminDBRepo.adminProfileState.value
//                        val callerInfo = MainActivity.callRepo.confrenceId.value!! + "-:-" + doctor.first_name + "-:-" + doctor.hospitalName + "-:-" + doctor.profile_pic_url
//
//                        PushNotification(
//                            it.token,
//                            data(callerInfo)
//                        ).also {
//                            sendNotification(it,context)
//                        }
//                    }
//                }
//            }
//        }