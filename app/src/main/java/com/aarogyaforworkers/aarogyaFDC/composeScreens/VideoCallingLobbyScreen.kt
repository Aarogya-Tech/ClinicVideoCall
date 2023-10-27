package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
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
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogyaFDC.Data
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.PushNotification
import com.aarogyaforworkers.aarogyaFDC.VideoCall.RetrofitInstance
import com.aarogyaforworkers.aarogyaFDC.VideoCall.VideoConferencing
import com.aarogyaforworkers.aarogyaFDC.ui.theme.logoOrangeColor
import com.aarogyaforworkers.awsapi.models.AdminProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                FloatingActionButton(onClick = {
                    if(MainActivity.callRepo.confrenceId.value == null){
                        MainActivity.callRepo.refreshConfrenceId()
                    }
                    var callerInfo = MainActivity.callRepo.confrenceId.value!! + "-:-" + doctor.first_name + "-:-" + doctor.hospitalName + "-:-" + doctor.profile_pic_url
                    if(MainActivity.callRepo.selectedCallersProfile.value.isNotEmpty()){
                        if(MainActivity.callRepo.selectedCallersProfile.value.size == 1){
                            callerInfo += "-:-" + doctor.token
                        }else{
                            callerInfo += "-:-" + ""
                        }
                        MainActivity.callRepo.selectedCallersProfile.value.filter { it.token.isNotEmpty() }.forEach {
                            PushNotification(
                                it.token,
                                Data(callerInfo)
                            ).also { it1 ->
                                if(MainActivity.callRepo.isOnCallScreen){
                                    sendNotification(it1,context, true)
                                }else{
                                    MainActivity.callRepo.isOnCallScreen = true
                                    sendNotification(it1,context, false)
                                }
                            }
                        }
                    } }, modifier = Modifier.padding(8.dp), containerColor = logoOrangeColor, contentColor = Color.White) {
                    Icon(imageVector = Icons.Default.VideoCall, contentDescription = "Video", modifier = Modifier.size(45.dp))
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
                                when (isSelected.value) {
                                    true -> {
                                        selectedIndex.value = adminList.indices.toSet()
                                        val list = adminList.filter { it.admin_id.isNotEmpty() }
                                        val newList  = arrayListOf<AdminProfile>()
                                        list.forEach {
                                            newList.add(it)
                                        }
                                        MainActivity.callRepo.updateGroupMembersProfileList(newList)
                                    }
                                    false -> {
                                        selectedIndex.value = emptySet()
                                        MainActivity.callRepo.updateGroupMembersProfileList(
                                            arrayListOf()
                                        )
                                    }
                                }
                            })
                ) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(24.dp)
                                .background(
                                    color = if (isSelected.value) Color(0xFF2f5597) else Color(
                                        0xffdae3f3
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                ), contentAlignment = Alignment.Center) {
                            if (isSelected.value){
                                Icon(imageVector = Icons.Default.Check, contentDescription = "checkIcon", Modifier.size(20.dp), tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.width(15.dp))
                        RegularTextView(title = "Select All", fontSize = 18)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn(modifier= Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)){
                itemsIndexed(adminList){index, admin ->
                    GroupCard(firstName = admin.first_name,
                        lastName = admin.last_name,
                        isSelected = isSelected.value || selectedIndex.value.contains(index)) {

                        val list = MainActivity.callRepo.selectedCallersProfile.value.filter { it.admin_id.isNotEmpty() }

                        val newList  = arrayListOf<AdminProfile>()

                        list.forEach {
                            newList.add(it)
                        }

                        if(isSelected.value){
                            isSelected.value = false
                            selectedIndex.value = (adminList.indices.toSet() - index)
                            newList.remove(admin)
                        }else if(selectedIndex.value.contains(index)){
                            selectedIndex.value = selectedIndex.value - index
                            newList.remove(admin)
                        }else{
                            selectedIndex.value = selectedIndex.value + index
                            newList.add(admin)
                        }

                        isSelected.value = selectedIndex.value.size == adminList.size

                        MainActivity.callRepo.updateGroupMembersProfileList(newList)

                        selectedAdmin.value = admin.token
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
    if(MainActivity.adminDBRepo.GroupMembersSyncedState.value != true) showProgress()
}


fun adminGenderShort(admin: AdminProfile): String {
    return when(admin.gender?.toUpperCase()) {
        "MALE" -> "M"
        "FEMALE" -> "F"
        "OTHER" -> "O"
        else -> ""
    }
}

fun sendNotification(notification: PushNotification, context:Context, isOnCallScreen : Boolean) = CoroutineScope(Dispatchers.IO).launch {
    try {
        val response = RetrofitInstance.api.postNotification(notification)
        Log.d("TAG", "sendNotification: $response")
        if(response.isSuccessful) {
            Log.d("TAG", "Response: $response")
            if(!isOnCallScreen){
                val intent = Intent(context, VideoConferencing::class.java)
                context.startActivity(intent)
            }
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
